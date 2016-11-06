import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Antoine on 06/11/2016.
 */
public class SalleJeu implements Runnable {


    private ArrayList<ClientManager> joueurs;
    private ArrayList<ClientManager> audience;

    private Thread mGameLoop;

    public SalleJeu(){
        joueurs = new ArrayList<>();
        audience = new ArrayList<>();
        mGameLoop = new Thread(this);
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
        mGameLoop.start();
    }

    @Override
    public void run() {
        // boucle de jeu ici
    }
}
