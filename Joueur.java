import java.io.Serializable;

/**
 * Created by Antoine on 04/11/2016.
 */
public class Joueur implements Serializable {

    private String mPseudo;

    public Joueur(String pseudo){
        mPseudo = pseudo;
    }


    public String getPseudo(){
        return mPseudo;
    }

}

