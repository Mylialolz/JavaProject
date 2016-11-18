import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private ArrayList<JLabel> mMemeToDisplay;

    private final String mIp;
    private final int mPort;
    private final String mPseudo;

    private Socket mSocket;
    private boolean mConnected;
    private Thread mThread;

    private int mCodeType = -1;
    private boolean mEnJeu = false;
    private boolean mCanVote = false;

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
    private JLabel mLabelThemeRound;
    private JTabbedPane mTabPane;
    private JPanel Chat;
    private JPanel mPaneMeme;
    private JButton mButtonEnvoyerMeme;
    private JTextField mTextFieldCheminMeme;
    private JCheckBox mCheckBoxFile;
    private JCheckBox mCheckBoxURL;
    private JLabel mMessageAvantPartie;
    private JList mJListSalleJeu;
    private GridLayout mGridMemeLayout;
    private double width;
    private double height;


    public ClientGUI(String ip, int port, String pseudo) {
        mIp = ip;
        mPort = port;
        mConnected = false;
        mPseudo = pseudo;
        mThread = new Thread(this);




        mTabPane.setTitleAt(0, "Chat");
        mTabPane.setTitleAt(1, "Les memes");

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

        mButtonEnvoyerMeme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(mCheckBoxFile.isSelected() && !mCheckBoxURL.isSelected()){
                    String chemin = mTextFieldCheminMeme.getText();
                    sendMemeByFile(chemin);
                }

                if(!mCheckBoxFile.isSelected() && mCheckBoxURL.isSelected()){
                    String url = mTextFieldCheminMeme.getText();
                    sendMemeByURL(url);
                }

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

    public int sendMemeByFile(String chemin) {

        try {
            String extension = "";
            final int i = chemin.lastIndexOf('.');
            if (i > 0) {
                extension = chemin.substring(i+1);
            }

            if(extension.matches("png") || extension.matches("jpg")) {
                out.writeUTF(CONSTANTE.ENVOYER_MEME);
                BufferedImage image = ImageIO.read(new File(chemin));
                ImageIO.write(image, extension, out);
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int sendMemeByURL(String URL){
        return 0;
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
                mLabelIndicatifSalleRole.setText("Vous n'êtes dans aucune salle de jeu !");
                mLabelTimer.setText("Temps avant la fin du round : 0");
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
                        case CONSTANTE.TIMER_PHASE_JEU :
                            final int time = Integer.parseInt(in.readUTF());
                            final int bound = Integer.parseInt(in.readUTF());
                            mLabelTimer.setText("Temps avant la fin du round : " + time + "/" + bound);
                            break;
                        case CONSTANTE.PHASE_JEU :
                            final int phase = Integer.parseInt(in.readUTF());
                            if(phase == 1) {
                                mLabelIndicatifSalleRole.setText("Phase 1 : les joueurs conçoivent leur meme !");
                            }
                            if (phase == 2){
                                mLabelIndicatifSalleRole.setText("Phase 2 : l'audience vote !");
                            }
                            break;
                        case CONSTANTE.NB_ROUND_RESTANTS:
                            final int r = Integer.parseInt(in.readUTF());
                            final int b = Integer.parseInt(in.readUTF());
                            mLabelNbRoundRestants.setText("Nombre de tours restant : " + r + "/" + b);
                            break;
                        case CONSTANTE.TEMPS_AVANT_PROCHAINE_PARTIE :
                            String m = in.readUTF();
                            mMessageAvantPartie.setText(m);
                            break;
                        case CONSTANTE.VALIDATION_UPVOTE:
                            mCanVote = in.readBoolean();
                            break;
                        case CONSTANTE.DIFFUSION_MEME :
                            final int index = in.readInt();
                            BufferedImage bImage = ImageIO.read(mSocket.getInputStream());
                            ImageIcon imageIcon = new ImageIcon(bImage);
                            Image scaledImage = imageIcon.getImage().getScaledInstance((int)width/6, (int)height/4, Image.SCALE_SMOOTH);
                            ImageIcon iconLogo = new ImageIcon(scaledImage);
                            mMemeToDisplay.get(index).setIcon(iconLogo);
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


        mTabPane = new JTabbedPane();
        mGridMemeLayout = new GridLayout(2,3);
        mPaneMeme = new JPanel(mGridMemeLayout);
        mMemeToDisplay = new ArrayList<>();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.getWidth();
        height = screenSize.getHeight();

        for(int i = 0; i < 6; i++) {

            ImageIcon iconLogo = new ImageIcon("C:\\Users\\Antoine\\Desktop\\oiseau.jpg");

            Image scaledImage = iconLogo.getImage().getScaledInstance((int)width/6, (int)height/4, Image.SCALE_SMOOTH);

            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(scaledImage));
            mMemeToDisplay.add(label);

            int finalI = i;

            label.addMouseListener(new MouseAdapter() {
                private int index = finalI;
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2 || SwingUtilities.isRightMouseButton(e)){
                        envoyerVote(index);
                    }
                }
            });

            mPaneMeme.add(label);

        }
    }

    public void envoyerVote(int i) {

        if (mConnected && mEnJeu) {

            if(mCanVote) {

                final int r = JOptionPane.showConfirmDialog(mPaneMeme, "Veuillez confirmer votre vote pour ce meme",
                        "Confirmation du vote", JOptionPane.YES_NO_OPTION);

                if (r == JOptionPane.YES_OPTION) {
                    try {

                        out.writeUTF(CONSTANTE.ENVOYER_UPVOTE);
                        out.writeUTF("" + i);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {

                final int r = JOptionPane.showConfirmDialog(mPaneMeme, "Vous avez déjà voté !",
                        "Erreur", JOptionPane.YES_NO_OPTION);

            }
        }
    }

}
