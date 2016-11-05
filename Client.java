import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Antoine on 04/11/2016.
 */
public class Client implements ActionListener, Runnable {


    private JPanel panneauPrincipal;
    private JPanel panneauCentral;
    private JButton lancerRequete;
    private JTextArea chat;
    private JFrame mainFrame;
    private JLabel messageTop;
    private JButton boutonConnexion;

    private JTextField mWriteMessage;

    private JList mListPlayers;
    //private ArrayList<Joueur> mArrayPlayers;
    private DefaultListModel listPlayers;

    private final String mIp;
    private final int mPort;
    private final String mPseudo;

    private Socket mSocket;
    private boolean mConnected;
    private Thread mThread;


    DataInputStream in;
    DataOutputStream out;
    ObjectInputStream input;

    public Client(String ip, int port, String pseudo) {
        mIp = ip;
        mPort = port;
        mConnected = false;
        mPseudo = pseudo;

        mThread = new Thread(this);
        this.prepareGUI();
    }

    private void prepareGUI() {

        mainFrame = new JFrame("MEME ENJAIL"); //Création d'une fenêtre avec un titre
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(mainFrame,
                        "Are you sure to close this window?", "Really Closing?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (mConnected)
                        disconnect();
                    System.exit(0);
                }
            }
        });

        panneauPrincipal = new JPanel(new BorderLayout()); //Création d'un panneau avec disposition selon BorderLayout

        boutonConnexion = new JButton("Se connecter");
        panneauPrincipal.add(boutonConnexion, BorderLayout.PAGE_END);
        boutonConnexion.addActionListener(this);

        listPlayers = new DefaultListModel();
        mListPlayers = new JList(listPlayers);
        panneauPrincipal.add(mListPlayers, BorderLayout.EAST);

        messageTop = new JLabel("Vous n'êtes pas connecté.");
        panneauPrincipal.add(messageTop, BorderLayout.PAGE_START);


        panneauCentral = new JPanel(new BorderLayout());
        mWriteMessage = new JTextField();
        mWriteMessage.setColumns(50);
        panneauCentral.add(mWriteMessage, BorderLayout.PAGE_END);
        lancerRequete = new JButton("Envoyer message");
        lancerRequete.addActionListener(this);
        panneauCentral.add(lancerRequete, BorderLayout.EAST);
        chat = new JTextArea();
        chat.setColumns(200);
        chat.setEditable(false);
        panneauCentral.add(chat, BorderLayout.WEST);

        panneauPrincipal.add(panneauCentral, BorderLayout.CENTER);



        //Finitions
        mainFrame.setContentPane(panneauPrincipal); //On range le panneau dans la fenêtre
        mainFrame.pack();
        mainFrame.setVisible(true);

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

        listPlayers.removeAllElements();

        ArrayList<Joueur> arrayPlayers =(ArrayList<Joueur>) input.readObject();
        System.out.println("size : " + arrayPlayers.size());

        for (Joueur e : arrayPlayers) {
            listPlayers.addElement(e.getPseudo());
        }

    }


    public int disconnect() {
        try {
            System.out.println("Disconnecting...");

            out.writeUTF(CONSTANTE.CLIENT_SERVER_STOP);
            out.writeUTF("stop");


            in.close();
            input.close();
            out.close();
            mSocket.close();


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


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == boutonConnexion) {
            this.connect();
        }

        if(source == lancerRequete){
            this.envoyerMessageChat(mWriteMessage.getText());
        }

    }


    public void envoyerMessageChat(String message){

        if(mConnected) {
            try {

                System.out.println("Envoi message dans le chat...");

                out.writeUTF(CONSTANTE.CLIENT_SERVER_CHAT);
                out.writeUTF(message);

                String pText = chat.getText();

                if(pText.matches("")) {
                    chat.setText(mPseudo + ": " + message + "\n");
                }
                else {
                    chat.setText(pText + "\n" + mPseudo + ": " + message);
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
            String p = chat.getText();

            if(p.matches("")){
                chat.setText(data);
            }
            else {
                chat.setText(p + "\n" + data);
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
}
