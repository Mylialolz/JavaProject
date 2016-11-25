import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.rmi.server.Activation$ActivationSystemImpl_Stub;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
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
    private ArrayList<JLabel> mMemeGeneratorSearchToDisplay;
    private ArrayList<ResearchMemeListe> mResearchMemeList;

    private int pageIndex = 0;

    private final String mIp;
    private final int mPort;
    private String mPseudo;

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
    private JTextField mTextFieldGeneratorSearchMeme;
    private JButton mButtonSearchGeneratorMeme;
    private JScrollBar mScrollBarGeneratorSearch;
    private JPanel mPaneGeneratorSearchMeme;
    private JButton mButtonSuivantGenerator;
    private JButton mButtonPrecedentGenerator;
    private JLabel mLabelIndicationMemeGeneratorSearch;
    private JPanel mMainPaneSearchGenerator;
    private JList mJListSalleJeu;
    private GridLayout mGridMemeLayout;
    private GridLayout mGridGeneratorSearchLayout;
    private double width;
    private double height;


    public ClientGUI(String ip, int port) {
        mIp = ip;
        mPort = port;
        mConnected = false;
        mThread = new Thread(this);

        mTabPane.setTitleAt(0, "Chat");
        mTabPane.setTitleAt(1, "Partie");
        mTabPane.setTitleAt(2, "Les memes");

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
                String s = (String)JOptionPane.showInputDialog(
                        mButtonConnectionServer,
                        "Entrez votre pseudo",
                        JOptionPane.QUESTION_MESSAGE);
                if ((s != null) && (s.length() > 0)){
                    mPseudo = s;
                }
                else{
                    mPseudo = "Unknown";
                }
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

        mButtonSearchGeneratorMeme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageIndex = 0;
                visualiserMeme(pageIndex);
            }
        });

        mButtonSuivantGenerator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageIndex++;
                visualiserMeme(pageIndex);
            }
        });

        mButtonPrecedentGenerator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageIndex--;
                visualiserMeme(pageIndex);
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

                    mTabPane.setSelectedIndex(0);

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

    public void updateMeme(int idJoueur) throws IOException {

        if (mEnJeu == true && mConnected == true) {

            final String urlString = in.readUTF();
            System.out.println("URL : " + urlString);

            if (urlString != null) {

                Image image = UrlHandler.getImageFromURL(urlString);
                Image scaledImage = image.getScaledInstance((int) width / 6, (int) height / 4, Image.SCALE_SMOOTH);
                mMemeToDisplay.get(idJoueur).setIcon(new ImageIcon(scaledImage));

            }
        }
    }

    private void visualiserMeme(int page){

        mLabelIndicationMemeGeneratorSearch.setText("Chargement en cours ...");

        String s = mTextFieldGeneratorSearchMeme.getText();
        final int indexEspace = s.indexOf(' ');
        if(indexEspace > 0) {
            s = s.substring(0, indexEspace);
        }
        ArrayList<ResearchMemeListe> array = Meme.researchMemes(s, page);
        mResearchMemeList = array;

        if(array != null){
            int l = 0;
            for(int i = 0; i < array.size(); i++){
               String imageURL = array.get(i).getImageUrl();
                Image image = null;
                try {
                    image = UrlHandler.getImageFromURL(imageURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Image scaledImage = null;
                if(image != null) {
                    scaledImage = image.getScaledInstance((int) width / 6, (int) height / 4, Image.SCALE_SMOOTH);
                }
                else {
                }
                if(scaledImage != null) {
                    mMemeGeneratorSearchToDisplay.get(l).setIcon(new ImageIcon(scaledImage));
                    l++;
                }
            }
        }
        mLabelIndicationMemeGeneratorSearch.setText("Chargement terminé.");


    }

    @Override
    public void run() {

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
                            System.out.println("Diffusion");
                            int j = in.readInt();
                            System.out.println("id joueur : " + j);
                            updateMeme(j);
                            break;
                    }
                } catch (SocketException se) {
                    se.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    fermerConnection = true;
                } catch (ClassNotFoundException s) {
                    s.printStackTrace();
                    fermerConnection = true;
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
                    mTabPane.setSelectedIndex(2);
                    break;
                case ClientGUI.GUEST :
                    mLabelIndicatifSalleRole.setText("Vous êtes un viewer : balancez vos likes !");
                    mTabPane.setSelectedIndex(1);
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

            JLabel label = new JLabel();
            label.setIcon(null);
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

        mGridGeneratorSearchLayout = new GridLayout(2,3);
        mPaneGeneratorSearchMeme = new JPanel(mGridGeneratorSearchLayout);
        mMemeGeneratorSearchToDisplay = new ArrayList<>();

        for(int i = 0; i < 6 ; i++){

            JLabel label = new JLabel();
            label.setIcon(null);
            int finalI = i;

            label.addMouseListener(new MouseAdapter() {
                private int index = finalI;
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2 || SwingUtilities.isRightMouseButton(e)){
                        try {
                            creerMeme(index);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            mMemeGeneratorSearchToDisplay.add(label);
            mPaneGeneratorSearchMeme.add(label);

        }


    }

    public void creerMeme(int i) throws IOException{


        if(mMemeGeneratorSearchToDisplay.get(i).getIcon() != null) {
            String[] options = {"Creer Meme", "Annuler"};
            JPanel panel = new JPanel();
            panel.add(new JLabel("Upper Text :"));
            JTextField textFieldUpper = new JTextField(25);
            panel.add(textFieldUpper);

            panel.add(new JLabel("Lower Text :"));
            JTextField textFieldLower = new JTextField(25);
            panel.add(textFieldLower);

            int result = JOptionPane.showOptionDialog(null, panel, "Enter a Number",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, null);
            if (result == JOptionPane.YES_OPTION) {

                String upperText = textFieldUpper.getText();
                String lowerText = textFieldLower.getText();
                String generatorId = mResearchMemeList.get(i).getGeneratorId();
                String imgId = mResearchMemeList.get(i).getImageId();

                Meme meme = new Meme(generatorId, imgId, upperText, lowerText);
                String memeUrl = meme.getMemeURL();
                System.out.println("memeUrl : " + memeUrl);

                if(mEnJeu && mConnected){
                    out.writeUTF(CONSTANTE.ENVOYER_MEME);
                    out.writeUTF(memeUrl);
                }
            }
        }


    }

    public void envoyerVote(int i) {

        if (mConnected && mEnJeu) {

            if(mCanVote && mMemeToDisplay.get(i).getIcon() != null) {

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

                final int r = JOptionPane.showConfirmDialog(mPaneMeme, "Vote indisponible !",
                        "Erreur", JOptionPane.YES_NO_OPTION);

            }
        }
    }

}
