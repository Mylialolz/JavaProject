import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Antoine on 05/12/2016.
 */
public class VisualisationMeme {

    private JPanel mainPanel;
    private JTextField mTextFieldUpper;
    private JTextField mTextFieldLower;
    private JLabel mImage;
    private JButton mButtonCreerMeme;
    private JButton mButtonVisualiser;
    private JButton mButtonAnnuler;
    private JLabel mMessageError;
    private String mMemeUrl = null;
    private ArrayList<ResearchMemeListe> mList;
    private int index;
    private JFrame fenetre;

    private ClientGUI mClient;

    public VisualisationMeme(ArrayList<ResearchMemeListe> list, int index, ClientGUI client){
        mList = list;
        this.index = index;
        this.mClient = client;

        mButtonCreerMeme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getInputs();
                    boolean ret = envoyerMeme(mClient.isEnJeu(), mClient.isConnected(), mClient.isUploadMeme(), mClient.getOut());
                    Image memeImg = UrlHandler.getImageFromURL(mMemeUrl);
                    BufferedImage img = (BufferedImage) memeImg;
                    String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    filename += ".jpg";
                    File f = new File("Memes/"+filename);
                    if(f.exists()){
                        ImageIO.write(img, "jpg", f);
                    }
                    else{
                        File dir = new File ("Memes");
                        dir.mkdirs();
                        f = new File("Memes/"+filename);
                        ImageIO.write(img, "jpg", f);
                        }


                    if(ret)
                        fenetre.dispatchEvent(new WindowEvent(fenetre, WindowEvent.WINDOW_CLOSING));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        mButtonVisualiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    previsualiser();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        mButtonAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fenetre.dispatchEvent(new WindowEvent(fenetre, WindowEvent.WINDOW_CLOSING));
            }
        });

    }


    public JPanel getPanel(){
        return mainPanel;
    }


    public void visualiser() throws IOException{

        fenetre = new JFrame();
        fenetre.setTitle("Aperçu de votre Meme");
        fenetre.setResizable(false);
        fenetre.setSize(650,500);
        fenetre.setContentPane(getPanel());
        fenetre.setVisible(true);

        fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            }
        });

    }

    synchronized private boolean envoyerMeme(boolean enJeu, boolean connected, boolean upload, DataOutputStream out) throws IOException{
        if (enJeu && connected && upload && mMemeUrl != null) {
            out.writeUTF(CONSTANTE.ENVOYER_MEME);
            out.writeUTF(mMemeUrl);
            return true;
        }
        else {
            mMessageError.setText("Impossible d'envoyer le meme pour le moment! ");
            return false;
        }
    }



    private void getInputs() throws IOException{
        String upperText = mTextFieldUpper.getText();
        String lowerText = mTextFieldLower.getText();
        String generatorId = mList.get(index).getGeneratorId();
        String imgId = mList.get(index).getImageId();
        Meme meme = new Meme(generatorId, imgId, upperText, lowerText);
        mMemeUrl = meme.getMemeURL();
    }

     private void previsualiser()throws IOException{
        Thread t = new Thread() {
            public void run(){
                mMessageError.setText("Chargement en cours...");
                try {
                    getInputs();
                    Image memeImg = UrlHandler.getImageFromURL(mMemeUrl);
                    ImageIcon memeIcon = new ImageIcon(memeImg);
                    mImage.setIcon(memeIcon);
                    mMessageError.setText("Chargement terminé");
                } catch (IOException e) {
                    e.printStackTrace();
                    mMessageError.setText("Erreur pendant le chargement du meme :s");
                }
            }
        };
        t.start();
    }





}
