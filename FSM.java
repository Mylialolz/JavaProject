import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Joris on 25/11/2016.
 */
public class FSM {


   private final static int TIMER_PHASE_1 = 90;
    private final static int TIMER_PHASE_2 = 35;
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
                nouvellePhaseVote(true);
                donneesJeu(nbRoundPlayed);
                nouvellePhaseEnvoieMeme(false);
            }
            if(compteur > TIMER_PHASE_2 && currentState == 1){
                Machine();
                compteur = 0;
                nouvellePhaseVote(false);
                donneesJeu(nbRoundPlayed);
                nouvellePhaseEnvoieMeme(true);
                nouveauTheme();
            }

        }

    };

   public FSM(SalleJeu salle){
       currentState = 0;
       nbRoundPlayed = 1;
       mSalle = salle;
       t = new Timer(1000, task);
       nouvellePhaseEnvoieMeme(true);
   }

    public void arreterPartie(){
        if(t.isRunning()){
            t.stop();
            mSalle.arreterPartie();
        }
    }

    public void demarrerPartie(){
        if(!t.isRunning()){
            compteur = 0;
            nbRoundPlayed = 1;
            nouvellePhaseEnvoieMeme(true);
            nouveauTheme();
            t.start();
        }
    }

   private int Machine(){
       int timerOrder = currentState;
       switch(currentState){
           case 0 : //STATE POST MEME
               /*CALL THE FUNCTION WHICH HANDLES THE ROUND*/
               currentState = 1;
               System.out.println("case 0");
               break;
           case 1 : //STATE VOTE
               System.out.println("case 1s");
               nbRoundPlayed ++;
               if(nbRoundPlayed == 6){
                   currentState = 3;
                   arreterPartie();
               }
               else{
                   currentState = 0;
               }
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

    private void nouveauTheme(){
        try {
            String theme = Theme.generateTheme();
            for(ClientManager cm : mSalle.getPlayers())
                cm.diffusionTheme(theme);
            for(ClientManager cm : mSalle.getAudience())
                cm.diffusionTheme(theme);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nouvellePhaseEnvoieMeme(boolean etat){
        try {
            for(ClientManager cm : mSalle.getPlayers())
                cm.nouveauMeme(etat);
            for(ClientManager cm : mSalle.getAudience())
                cm.nouveauMeme(false);

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



