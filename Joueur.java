import java.io.Serializable;

/**
 * Created by Antoine on 04/11/2016.
 */
public class Joueur implements Serializable {

    private String mPseudo;
    private int mScore;


    public Joueur(String pseudo){
        mPseudo = pseudo;
        mScore = -1;
    }

    public Joueur(Joueur joueur) {
        mPseudo = joueur.getPseudo();
        mScore = joueur.getScore();
    }

    public String getPseudo(){
        return mPseudo;
    }

    public int getScore(){return mScore;}

    public void setScore(int s){mScore = s;}

}

