import com.sun.deploy.util.SessionState;

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
            if (indicePhase == 1) {

                if (compteurPhase < 90) indicePhase = 1;
                else indicePhase = 2;

            }

            if (indicePhase == 2) {
                if (compteurPhase < 45) indicePhase = 2;
                else indicePhase = 1;
            }

            compteurPhase++;


            if(mRound > 5){
                t.stop();
            }


        }
    };


    public GameLoop(SalleJeu s){
        mSalle = s;
    }

    public void demarrerPartie(){
        t = new Timer(1000, taskPhase);
        mRound = 1;
        indicePhase = 1;
        t.start();
    }

    public void arreterPartie(){
        if(t.isRunning()){
            t.stop();
        }
    }

}
