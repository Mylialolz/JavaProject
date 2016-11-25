import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.Timer;
import java.util.concurrent.Semaphore;

/**
 * Created by Antoine on 06/11/2016.
 */
public class SalleJeu{


    public static final int TIME_BOUND_P1 = 15;
    public static final int TIME_BOUND_P2 = 15;
    public static final int MAX_ROUNDS = 5;

    public static final int NB_MAX_JOUEURS = 6;
    public static final int WAITINTG_TIME = 20;


    private ArrayList<ClientManager> joueurs;
    private ArrayList<ClientManager> audience;

    private GameLoop mGameLoop = new GameLoop(this);
    private GameInfo mGameInfo = new GameInfo(this);

    private Timer tempsAvantPartie;
    private int compteur = 0;

    private ActionListener task = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(getNbPlayers()> 1){

                ++compteur;
                for(ClientManager cm : getPlayers()){
                    cm.envoyerTempsAvantDebutPartie(compteur);
                }

                for(ClientManager cm : getAudience()){
                    cm.envoyerTempsAvantDebutPartie(compteur);
                }

                final int nbPlayers = getNbPlayers();

                if(nbPlayers == NB_MAX_JOUEURS){
                    lancerPartie();
                }

                if(compteur >= WAITINTG_TIME){
                    lancerPartie();
                }
            }
            else {
                compteur = 0;

                for(ClientManager cm : getPlayers()){
                    cm.envoyerTempsAvantDebutPartie(compteur);
                }

                for(ClientManager cm : getAudience()){
                    cm.envoyerTempsAvantDebutPartie(compteur);
                }
            }

        }
    };

    public SalleJeu(){
        joueurs = new ArrayList<>();
        audience = new ArrayList<>();
        tempsAvantPartie = new Timer(850, task);
        tempsAvantPartie.start();
    }

    synchronized public int addPlayers(ClientManager cm){
        if(getNbPlayers() < 6) {
            joueurs.add(cm);
            return 0;
        }
        return 1;
    }

    synchronized public void addAudience(ClientManager cm){
        audience.add(cm);
    }

    synchronized public void removePlayers(ClientManager cm){
        joueurs.remove(cm);
    }

    synchronized public void removeAudience(ClientManager cm){
        audience.remove(cm);
    }

    synchronized public void clearPlayers(){
        joueurs.clear();
    }

    synchronized public void clearAudience(){
        audience.clear();
    }

    synchronized public ArrayList<ClientManager> getPlayers(){
        return (ArrayList<ClientManager>)joueurs.clone();
    }

    synchronized public ArrayList<ClientManager> getAudience(){
        return (ArrayList<ClientManager>)audience.clone();
    }

    synchronized public int getNbPlayers(){return joueurs.size();}

    synchronized public int getNbAudience(){return audience.size();}

    synchronized public int getGamePhase(){return mGameLoop.getIndicePhase();}

    synchronized public int getTimeInPhase(){return mGameLoop.getCompteurPhase();}

    synchronized public int getNumRound(){return mGameLoop.getNumRound();}

    synchronized public int getTimerAvantNouvellePartie(){return compteur;}

    public void lancerPartie(){
        tempsAvantPartie.stop();
        mGameLoop.demarrerPartie();
        mGameInfo.demarrerPartie();
    }

    public void arreterPartie(){

        mGameLoop.arreterPartie();
        mGameInfo.arreterPartie();

        for(ClientManager cm : joueurs){
            cm.deconnecterClientPartie();
        }

        for(ClientManager cm : audience){
            cm.deconnecterClientPartie();
        }

        tempsAvantPartie.start();
    }


}
