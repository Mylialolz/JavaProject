import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Antoine on 04/11/2016.
 */
public class ClientGUI implements Runnable {


    private JPanel panneauBot;

    //private ArrayList<Joueur> mArrayPlayers;
    private DefaultListModel mDefaultlistPlayers;

    private final String mIp;
    private final int mPort;
    private final String mPseudo;

    private Socket mSocket;
    private boolean mConnected;
    private Thread mThread;

    DataInputStream in;
    DataOutputStream out;
    ObjectInputStream input;

    private JLabel messageTop;
    private JPanel panneauTop;
    private JPanel panneauCentral;
    private JTextField mTexteMessageChat;
    private JButton mButtonConnectionServer;
    private JButton mButtonEnvoyerMessage;
    private JTextArea mTextAreaChat;
    private JPanel mainPanel;
    private JList mJListJoueurs;

    public ClientGUI(String ip, int port, String pseudo) {
        mIp = ip;
        mPort = port;
        mConnected = false;
        mPseudo = pseudo;
        mThread = new Thread(this);

        mButtonEnvoyerMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               envoyerMessageChat(mTexteMessageChat.getText());
                mTexteMessageChat.setText("");
            }
        });
        mButtonConnectionServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });


    }

    public JPanel getMainPanel(){
        return mainPanel;
    }


    public void connect() {
        try {
            if (mConnected == false) {

                mSocket = new Socket(mIp, mPort);

                try {
                    in = new DataInputStream(mSocket.getInputStream());
                    out = new DataOutputStream(mSocket.getOutputStream());
                    input = new ObjectInputStream(mSocket.getInputStream());

                    out.writeUTF(CONSTANTE.CLIENT_SERVER_PSEUDO);
                    out.writeUTF(mPseudo);

                    mConnected = true;
                    messageTop.setText("Vous êtes connecté.");

                    mThread.start();

                    mButtonConnectionServer.setEnabled(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
            mConnected = false;
            messageTop.setText("Vous n'êtes pas connecté.");
        }

    }

    private void setPlayersList() throws IOException, ClassNotFoundException {

        mDefaultlistPlayers.removeAllElements();

        ArrayList<Joueur> arrayPlayers =(ArrayList<Joueur>) input.readObject();
        System.out.println("size : " + arrayPlayers.size());

        for (Joueur e : arrayPlayers) {
            mDefaultlistPlayers.addElement(e.getPseudo());
        }

    }

    public int disconnect() {
        try {

            if(mConnected) {
                System.out.println("Disconnecting...");
                out.writeUTF(CONSTANTE.CLIENT_SERVER_STOP);
                out.writeUTF("stop");
                in.close();
                input.close();
                out.close();
                mSocket.close();

                mButtonConnectionServer.setEnabled(true);
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int sendMeme(Socket socket, String chemin) {

        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedImage image = ImageIO.read(new File(chemin));
            ImageIO.write(image, "png", outputStream);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }


    public void envoyerMessageChat(String message){

        if(mConnected) {
            try {

                System.out.println("Envoi message dans le chat...");

                out.writeUTF(CONSTANTE.CLIENT_SERVER_CHAT);
                out.writeUTF(message);

                String pText = mTextAreaChat.getText();

                if(pText.matches("")) {
                    mTextAreaChat.setText(mPseudo + ": " + message + "\n");
                }
                else {
                    mTextAreaChat.setText(pText + "\n" + mPseudo + ": " + message);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void setChat(){

        String data = null;
        try {
            data = in.readUTF();
            String p = mTexteMessageChat.getText();

            if(p.matches("")){
                mTexteMessageChat.setText(data);
            }
            else {
                mTexteMessageChat.setText(p + "\n" + data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {

        boolean erreur = false;
        boolean fermerConnection = false;
        String data = "";

        if (mSocket != null && in != null) {

            while (!fermerConnection) {

                try {
                    data = in.readUTF();

                    System.out.println("Instruction recue : " + data);

                    switch (data) {
                        default:
                            break;
                        case CONSTANTE.CLOSE_CONNECTION:
                            fermerConnection = true;
                            break;
                        case CONSTANTE.NOUVELLE_LISTE:
                                setPlayersList();
                            break;
                        case CONSTANTE.CLIENT_SERVER_CHAT:
                                setChat();
                            break;

                    }
                } catch (SocketException se) {
                    se.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException s) {
                    s.printStackTrace();
                }
            }
        }

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        mDefaultlistPlayers = new DefaultListModel();
        mJListJoueurs = new JList(mDefaultlistPlayers);
    }
}
