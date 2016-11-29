import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Joris on 25/11/2016.
 */
public class FSM {


   private final static int TIMER_PHASE_1 = 15;
    private final static int TIMER_PHASE_2 = 15;
   private int nbRoundPlayed;
   private int currentState; // Indicates the current state of the FSM

    private int compteur = 0;


    private SalleJeu mSalle = null;
    private Timer t;

    private ActionListener task = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            compteur++;
            infosTimer(mSalle.getAudience());
            infosTimer(mSalle.getPlayers());

            if(compteur > TIMER_PHASE_1 && currentState == 0) {
                Machine();
                compteur = 0;
            }
            if(compteur > TIMER_PHASE_2 && currentState == 1){
                Machine();
                compteur = 0;
            }

        }

    };

   public FSM(SalleJeu salle){
       currentState = 0;
       nbRoundPlayed = 0;
       mSalle = salle;
       t = new Timer(1000, task);
   }

    public void arreterPartie(){
        if(t.isRunning()){
            t.stop();
            mSalle.arreterPartie();
        }
    }

    public void demarrerPartie(){
        if(!t.isRunning()){
            t.start();
        }
    }

   private int Machine(){
       int timerOrder = currentState;
       switch(currentState){
           case 0 : //STATE POST MEME
               /*CALL THE FUNCTION WHICH HANDLES THE ROUND*/
               currentState = 1;
               nouvellePhaseVote(true);
               System.out.println("case 0");
               break;
           case 1 : //STATE VOTE
               System.out.println("case 1s");
               nbRoundPlayed ++;
               if(nbRoundPlayed == 6){
                   currentState = 3;
               }
               else{
                   currentState = 1;
               }
               nouvellePhaseVote(false);
               donneesJeu(nbRoundPlayed);
               break;
           case 3 :// STATE END
               arreterPartie();
               break;
           }
       return timerOrder; // A retirer si on met le timer dans la FSM
   }

    private void nouvellePhaseVote(boolean etat){
        try {
            for(ClientManager cm : mSalle.getPlayers())
                cm.nouveauVote(etat);
            for(ClientManager cm : mSalle.getAudience())
                cm.nouveauVote(etat);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void donneesJeu(int nbRound) {
        for(ClientManager cm : mSalle.getAudience()){
            cm.envoyerTableauScores();
            cm.envoyerScorePerso();
            cm.envoyerNbRound(nbRound);
        }

        for(ClientManager cm : mSalle.getPlayers()){
            cm.envoyerTableauScores();
            cm.envoyerScorePerso();
            cm.envoyerNbRound(nbRound);
        }
    }

    private void infosTimer(ArrayList<ClientManager> j) {
        for (ClientManager cm : j) {
            cm.envoyerNumeroPhase(currentState+1);
            if (currentState == 0)
                cm.envoyerTimerPhase(compteur, TIMER_PHASE_1);
            else
                cm.envoyerTimerPhase(compteur, TIMER_PHASE_2);
        }
    }


}



