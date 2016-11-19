import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.invoke.SerializedLambda;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Antoine on 04/11/2016.
 */
public class ClientManager implements Runnable {

    private Socket mSocket;
    private int stopThread;
    private Thread mThread;
    private Server mServeur;

    private SalleJeu mSalleJeu = null;

    private boolean mEnJeu = false;
    private int mCodePlayer = -1;

    private int mId;

    private Joueur mJoueur;
    private DataInputStream in;
    private DataOutputStream out;
    ObjectOutputStream oos;

    private boolean mCanVote = false;
    private BufferedImage mBufferedImage = null;

    public ClientManager(Server s, Socket e, int id){

        mServeur = s;
        mSocket = e;
        mId = id;

        stopThread = 0;

        try {
            in = new DataInputStream(mSocket.getInputStream());
            out = new DataOutputStream(mSocket.getOutputStream());
            oos = new ObjectOutputStream(mSocket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        mThread = new Thread(this);
        mThread.start();

    }


    @Override
    public void run() {


        while(stopThread != 1){
            handleRequests();
        }

        handleClose();

    }

    public void envoyerListeJoueursConnectes(){

        try {

            out.writeUTF(CONSTANTE.NOUVELLE_LISTE);
            ArrayList<Joueur> list = (ArrayList<Joueur>)mServeur.getListJoueurs().clone();
            oos.writeObject(list);

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    public void envoyerTableauScores(){

        if(mEnJeu == true && mSalleJeu != null) {

            ArrayList<ClientManager> list = null;
            ArrayList<String> finalList = new ArrayList<>();

            try {
                out.writeUTF(CONSTANTE.TABLEAU_SCORES);

                list = mSalleJeu.getPlayers();
                for(ClientManager cm : list){
                    String s = "" + cm.getJoueur().getPseudo() + " " + cm.getJoueur().getScore();
                    finalList.add(s);
                }
                oos.writeObject(finalList);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void envoyerScorePerso(){

        if(mEnJeu == true && mSalleJeu != null) {
            try {

                out.writeUTF(CONSTANTE.CLIENT_PERSO_SCORE);
                out.writeUTF("" + mJoueur.getScore());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void distribuerMessageChat(String s, int id){

        if(id != mId) {

            try {
                out.writeUTF(CONSTANTE.CLIENT_SERVER_CHAT);
                out.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void envoyerNumeroPhase(int phase){
        if(mEnJeu == true && mSalleJeu != null) {
            try {

                out.writeUTF(CONSTANTE.PHASE_JEU);
                out.writeUTF("" +  phase);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void envoyerTimerPhase(int v){
        if(mEnJeu == true && mSalleJeu != null) {
            try {

                out.writeUTF(CONSTANTE.TIMER_PHASE_JEU);
                out.writeUTF("" +  v);

                if(mSalleJeu.getGamePhase() == 1){
                    out.writeUTF("" + SalleJeu.TIME_BOUND_P1);
                }

                if(mSalleJeu.getGamePhase() == 2){
                    out.writeUTF("" + SalleJeu.TIME_BOUND_P2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void envoyerNbRound(int v){
        if(mEnJeu == true && mSalleJeu != null) {
            try {

                out.writeUTF(CONSTANTE.NB_ROUND_RESTANTS);
                out.writeUTF("" +  v);
                out.writeUTF("" + SalleJeu.MAX_ROUNDS);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void envoyerTempsAvantDebutPartie(int v){
        if(mEnJeu == true && mSalleJeu != null) {
            try {

                out.writeUTF(CONSTANTE.TEMPS_AVANT_PROCHAINE_PARTIE);

                final int compteur = mSalleJeu.getTimerAvantNouvellePartie();
                final int nbPlayers = mSalleJeu.getNbPlayers();

                if(nbPlayers > 1) {
                    out.writeUTF("Temps avant début de la partie : " + compteur + "/" + SalleJeu.WAITINTG_TIME + ". Nombre de joueurs : " + nbPlayers + "/" + SalleJeu.NB_MAX_JOUEURS);
                }
                else {
                    out.writeUTF("Erreur : Pas assez de joueurs pour commencer la partie !");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequests() {

        try {

            String data = in.readUTF();
            System.out.print("id_req:" + data + "\n");
            switch(data){
                default:break;

                case CONSTANTE.CLOSE_CONNECTION :
                    stopThread = 1;
                    out.writeUTF("ok");
                    break;

                case CONSTANTE.CLIENT_SERVER_PSEUDO:
                    String pseudo = in.readUTF();
                    mJoueur = new Joueur(pseudo);
                    System.out.println("Joueur " + pseudo + " est connecté.");
                    mServeur.getListJoueurs().add(mJoueur);
                    mServeur.newPlayer();
                    break;

                case CONSTANTE.CLIENT_SERVER_CHAT :
                    String m = in.readUTF();
                    String message = mJoueur.getPseudo() + ": " + m;
                    mServeur.newMessage(message, mId);
                    break;

                case CONSTANTE.CLIENT_SERVER_SALLE_JEU :
                    connecterPartie();
                    break;

                case CONSTANTE.QUITTER_PARTIE :
                    deconnecterClientPartie();
                    break;
                case CONSTANTE.ENVOYER_UPVOTE :
                    ClientManager cm = mSalleJeu.getPlayers().get(Integer.parseInt(in.readUTF()));
                    cm.getJoueur().setScore(cm.getJoueur().getScore() + 1);
                    nouveauVote(false);
                    break;
                case CONSTANTE.ENVOYER_MEME :
                    System.out.println("recpetion meme");
                    recevoirMeme();
                    diffuserMeme();
                    break;
            }

        } catch (java.net.SocketException se) {
            //stopThread = 1;
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //stopThread = 1;
        }
    }

    public void recevoirMeme() throws IOException {

        System.out.println("Called");
        mBufferedImage = ImageIO.read(in);
        String save = "./joueur_" + mId + ".png";
        System.out.println("blocked");

        File outputfile = new File(save);
        ImageIO.write(mBufferedImage, "png", outputfile);

        System.out.print("Reception meme");
        if (mBufferedImage != null) {
            System.out.println("Meme non null");
        }


    }

    private void diffusion(BufferedImage meme, int joueur) throws IOException{

        out.writeUTF(CONSTANTE.DIFFUSION_MEME);
        out.writeInt(joueur);
        ImageIO.write(meme, "png", out);

    }

    synchronized public void diffuserMeme() throws IOException{

        ArrayList<ClientManager> audience = mSalleJeu.getAudience();
        ArrayList<ClientManager> joueur = mSalleJeu.getPlayers();

        final int index = joueur.indexOf(this);

        if(mBufferedImage != null) {
            for (ClientManager cm : audience) {
                cm.diffusion(mBufferedImage, index);
            }

            for (ClientManager cm : joueur) {
                cm.diffusion(mBufferedImage, index);
            }
        }
        else {
            System.out.println("Image meme null");
        }

    }

    private void connecterPartie() throws IOException {

        if(mEnJeu == false ) {

            mSalleJeu = mServeur.getJeu();

            if (mSalleJeu != null) {
                int code = in.read();
                switch (code) {
                    case ClientGUI.GUEST:
                        mJoueur.setScore(-1);
                        mSalleJeu.addAudience(this);
                        mCodePlayer = ClientGUI.GUEST;
                        break;
                    case ClientGUI.MASTER:
                        mJoueur.setScore(0);
                        mSalleJeu.addPlayers(this);
                        mCodePlayer = ClientGUI.MASTER;
                        break;
                    default:
                        mSalleJeu = null;
                        break;
                }
            }

            if (mSalleJeu != null) {

                out.writeUTF(CONSTANTE.VALIDATION_PRESENT_PARTIE);
                out.writeBoolean(true);

                mEnJeu = true;
                mCanVote = true;
                mServeur.newMemberInGame();
            }
        }

    }

    public void updateCountersOfAudienceAndPlayersInGame(){

        if(mEnJeu == true) {

            try {
                out.writeUTF(CONSTANTE.CLIENT_SERVER_NOUVEAU_AUDIENCE_NOUVEAU_JOUEUR);
                ArrayList<String> list = new ArrayList<>();

                final int nbAudience = mSalleJeu.getNbAudience();
                final int nbPlayers = mSalleJeu.getNbPlayers();

                list.add("" + nbAudience + " viewers connecté(s).");
                list.add("" + nbPlayers + " joueurs connecté(s).");

                oos.writeObject(list);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void handleClose() {
        try {


            deconnecterClientPartie();


            out.close();
            in.close();
            oos.close();

            mSocket.close();

            mServeur.getListClients().remove(mSocket);
            mServeur.getListJoueurs().remove(mJoueur);
            mServeur.getListManagers().remove(this);

            System.out.println("Un client s'est déconnecté.");
            System.out.println("Il reste " + mServeur.getListClients().size() + " client(s) sur le serveur.");

            mServeur.newPlayer();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deconnecterClientPartie() {

        try {
            out.writeUTF(CONSTANTE.VALIDATION_PRESENT_PARTIE);
            out.writeBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mCodePlayer != -1 && mCodePlayer == ClientGUI.GUEST)
            mSalleJeu.removeAudience(this);
        if(mCodePlayer != -1 && mCodePlayer == ClientGUI.MASTER)
            mSalleJeu.removePlayers(this);

        mEnJeu = false;
        mCanVote = false;
        mCodePlayer = -1;
        mSalleJeu = null;
        mServeur.newMemberInGame();
    }

    public Joueur getJoueur(){
        return mJoueur;
    }

    public void nouveauVote (boolean etat) throws IOException{
        mCanVote = etat;
        out.writeUTF(CONSTANTE.VALIDATION_UPVOTE);
        out.writeBoolean(mCanVote);
    }

}