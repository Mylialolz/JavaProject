/**
 * Created by Joris on 23/11/2016.
 */
public class Meme {

    private String memeURL;
    private String generatorID; // ID of the generator
    private String imageID; // ID of the image (contained at the end of imageURL)
    private String text0; // Top text (or bottom if text1 is empty
    private String text1; // Bottom text


    public Meme(String genID, String imaID, String txt0, String txt1){

        generatorID = genID;
        imageID = imaID;
        text0 = txt0;
        text1 = txt1;
        String request = createMeme();
    }

    public Meme(String genID, String imaID, String txt0){

        generatorID = genID;
        imageID = imaID;
        text0 = txt0;
        text1 = null;
        String request = createMeme();

    }

    private String createMeme(){

        String requestedURL = CONSTANTE.URL_INSTANCE_CREATE + "?username=" + CONSTANTE.USERNAME + "&password=" + CONSTANTE.PASSWORD + "&languageCode=" + CONSTANTE.LANGUAGE_CODE
                            + "&generatorID=" + generatorID + "&imageID=" + imageID + "&text0=" + text0 + "&text1=" + text1;
        return requestedURL;
    }
}
//TODO Lancer la requête, récupérer le JSON, Stocker l'URL du meme dans l'attribut memeURL