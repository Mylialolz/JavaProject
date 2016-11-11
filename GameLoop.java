import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Created by Antoine on 10/11/2016.
 */
public class GameLoop {

    private SalleJeu mSalle;

    private int indicePhase = 1;
    private int compteurPhase = 0;

    private int mRound = 0;
    private Timer t;

    private ActionListener taskPhase = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (getIndicePhase() == 1) { // preparation des memes
                if (getCompteurPhase() < SalleJeu.TIME_BOUND_P1) {
                    setCompteurPhase(getCompteurPhase() + 1);
                    setIndicePhase(1);
                }
                else {
                    setCompteurPhase(0);
                    setIndicePhase(2);
                }
            }

            if (getIndicePhase() == 2) { // votes pour les memes
                if (getCompteurPhase() < SalleJeu.TIME_BOUND_P2) {
                    setCompteurPhase(getCompteurPhase() + 1);
                    setIndicePhase(2);
                }
                else {
                    setCompteurPhase(0);
                    setIndicePhase(1);
                }
            }

            if(mRound >= SalleJeu.MAX_ROUNDS)
                arreterPartie();

        }
    };


    public GameLoop(SalleJeu s){
        mSalle = s;
    }

    public void demarrerPartie(){
        t = new Timer(1000, taskPhase);
        mRound = 1;
        setIndicePhase(1);
        t.start();
    }

    public void arreterPartie(){
        if(t.isRunning()){
            t.stop();
        }
    }

    synchronized public int getIndicePhase() {
        return indicePhase;
    }

    synchronized public void setIndicePhase(int indicePhase) {
        this.indicePhase = indicePhase;
    }

    synchronized public int getCompteurPhase() {
        return compteurPhase;
    }

    synchronized public void setCompteurPhase(int compteurPhase) {
        this.compteurPhase = compteurPhase;
    }

    synchronized public int getNumRound(){
        return mRound;
    }

}
