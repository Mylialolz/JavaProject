import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by Antoine on 06/11/2016.
 */
public class SalleJeu{


    private ArrayList<ClientManager> joueurs;
    private ArrayList<ClientManager> audience;

    private boolean mStopGameInfo;
    private boolean mStopGameLoop;

    private GameLoop mGameLoop = new GameLoop(this);
    private GameInfo mGameInfo = new GameInfo(this);

    public SalleJeu(){
        joueurs = new ArrayList<>();
        audience = new ArrayList<>();
    }

    synchronized public void addPlayers(ClientManager cm){
        joueurs.add(cm);
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


    public void lancerPartie(){
        mGameLoop.demarrerPartie();
        mGameInfo.demarrerPartie();
    }

    public void arreterPartie(){
        mGameLoop.arreterPartie();
        mGameInfo.arreterPartie();
        clearAudience();
        clearPlayers();
    }


}
