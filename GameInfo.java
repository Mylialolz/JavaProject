import java.util.concurrent.Semaphore;

/**
 * Created by Antoine on 10/11/2016.
 */
public class GameInfo implements Runnable {

    private SalleJeu mSalle;

    private Thread mThread;
    private boolean stop = true;

    public GameInfo(SalleJeu s) {
        mSalle = s;
        mThread = new Thread(this);
    }

    public void demarrerPartie() {
        stop = false;
        mThread.start();
    }

    public void arreterPartie() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {

                for(ClientManager cm : mSalle.getAudience()){
                    cm.envoyerTableauScores();
                    cm.envoyerScorePerso();
                }

                for(ClientManager cm : mSalle.getPlayers()){
                    cm.envoyerTableauScores();
                    cm.envoyerScorePerso();
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
