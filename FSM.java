/**
 * Created by Joris on 25/11/2016.
 */
public class FSM {

   private int nbRoundPlayed;

   private int currentState; // Indicates the current state of the FSM

   public void FSM(){
       currentState = 0;
       nbRoundPlayed = 0;
   }

   private int Machine(){
       int timerOrder = currentState;
       switch(currentState){
           case 0 : //STATE ROUND
               nbRoundPlayed ++;
               if(nbRoundPlayed == 6){
                   currentState = 2;
                   /*CALL THE FUNCTION WHICH HANDLES THE ROUND*/
               }
               else{
                   currentState = 1;
                   /*CALL THE FUNCTION WHICH HANDLES THE ROUND*/
               }
               break;
           case 1 : //STATE DISPLAY ROUND SCORES
               currentState = 0;
               /*CALL THE FUNCTION WHICH DISPLAY THE ROUND SCORES*/
               break;
           case 2 : // STATE DISPLAY GAME SCORES
               break;
           }
       return timerOrder; // A retirer si on met le timer dans la FSM
   }


}



