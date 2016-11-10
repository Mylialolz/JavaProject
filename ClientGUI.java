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


    public final static int GUEST = 0;
    public final static int MASTER = 1;

    private JPanel panneauBot;

    //private ArrayList<Joueur> mArrayPlayers;
    private DefaultListModel mDefaultlistPlayers;
    private DefaultListModel mDefaultlistScore;

    private final String mIp;
    private final int mPort;
    private final String mPseudo;

    private Socket mSocket;
    private boolean mConnected;
    private Thread mThread;

    private int mCodeType = -1;
    private boolean mEnJeu = false;

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
    private JList mJListSalle;
    private JLabel mLabelNbAudience;
    private JButton mButtonAccessGamePlayer;
    private JButton mButtonAccessGameAudience;
    private JLabel mLabelIndicatifSalleRole;
    private JLabel mJLabelScore;
    private JLabel mLabelNbRoundRestants;
    private JButton mButtonQuitterPartie;
    private JLabel mLabelNbPlayer;
    private JLabel mLabelTimer;
    private JList mJListSalleJeu;

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

        mButtonAccessGamePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trouverSalleJeu(MASTER);
            }
        });

        mButtonAccessGameAudience.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trouverSalleJeu(GUEST);
            }
        });

        mButtonQuitterPartie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitterSalleJeu();
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

    private void setScoreTab() throws IOException, ClassNotFoundException{

        mDefaultlistScore.removeAllElements();

        ArrayList<String> array = (ArrayList<String>) input.readObject();

        for (String s : array) {
            mDefaultlistScore.addElement(s);
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

        if(mConnected && !message.matches("")) {
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
            String p = mTextAreaChat.getText();

            if(p.matches("")){
                mTextAreaChat.setText(data);
            }
            else {
                mTextAreaChat.setText(p + "\n" + data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAudienceAndPlayers() throws IOException, ClassNotFoundException{

        if(mEnJeu == true && mConnected == true) {

            ArrayList<String> array = (ArrayList<String>) input.readObject();
            mLabelNbAudience.setText(array.get(0));
            mLabelNbPlayer.setText(array.get(1));

        }

    }

    public void trouverSalleJeu(int code){

        if(mEnJeu == false && mConnected == true) {

            try {
                out.writeUTF(CONSTANTE.CLIENT_SERVER_SALLE_JEU);
                out.write(code);

                mCodeType = code;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return;
    }

    public void quitterSalleJeu(){
        if(mEnJeu == true) {
            try {
                out.writeUTF(CONSTANTE.QUITTER_PARTIE);
                mDefaultlistScore.removeAllElements();
                mDefaultlistScore.addElement("Aucune salle de jeu");
                mButtonAccessGameAudience.setEnabled(true);
                mButtonAccessGamePlayer.setEnabled(true);
                mJLabelScore.setText("Score : 0");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        case CONSTANTE.CLIENT_SERVER_NOUVEAU_AUDIENCE_NOUVEAU_JOUEUR :
                            setAudienceAndPlayers();
                            break;
                        case CONSTANTE.VALIDATION_PRESENT_PARTIE :
                            gestionCoDecoPartie();
                            break;
                        case CONSTANTE.TABLEAU_SCORES :
                            setScoreTab();
                            break;
                        case CONSTANTE.CLIENT_PERSO_SCORE:
                            final int score = Integer.parseInt(in.readUTF());
                            if(score == -1) {
                                mJLabelScore.setText("Vous êtes spectateur.");
                            }
                            else
                                mJLabelScore.setText("Score : " + score);
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

    private void gestionCoDecoPartie() throws IOException {
        mEnJeu = in.readBoolean();
        mButtonAccessGameAudience.setEnabled(!mEnJeu);
        mButtonAccessGamePlayer.setEnabled(!mEnJeu);
        if(mEnJeu){
            switch(mCodeType){
                default:break;
                case ClientGUI.MASTER :
                    mLabelIndicatifSalleRole.setText("Vous êtes un joueur : balancez vos memes !");
                    break;
                case ClientGUI.GUEST :
                    mLabelIndicatifSalleRole.setText("Vous êtes un viewer : balancez vos likes !");
                    break;
            }
        }
        else {
            mCodeType = -1;
            mLabelNbPlayer.setText("Nombre de joueurs : 0");
            mLabelNbAudience.setText("Aucune audience pour le moment");
            mLabelIndicatifSalleRole.setText("Vous n'êtes dans aucune salle !");
            mJLabelScore.setText("Score : 0");
            mLabelTimer.setText("Temps restant avant fin du round : 0");
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        mDefaultlistPlayers = new DefaultListModel();
        mJListJoueurs = new JList(mDefaultlistPlayers);
        mDefaultlistScore= new DefaultListModel();
        mJListSalle = new JList(mDefaultlistScore);
        mDefaultlistScore.addElement("Aucune score");
        mDefaultlistPlayers.addElement("Aucun joueurs connectés");
    }
}
