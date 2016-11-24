import java.awt.*;
import java.util.ArrayList;

import com.google.gson.*;
import org.json.*;

/**
 * Created by Joris on 23/11/2016.
 */
public class Meme {

    private String memeURL;
    private String generatorID; // ID of the generator
    private String imageID; // ID of the image (contained at the end of imageURL)
    private String text0; // Top text (or bottom if text1 is empty
    private String text1; // Bottom text

    private static int pageSize = 24;
    private static int pageIndex = 0;


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

    public static ArrayList<ResearchMemeListe> researchMemes(String memeName){

        ArrayList<ResearchMemeListe> retList = null;

        if(memeName != null) {

            String req = CONSTANTE.URL_GENERATOR_SEARCH + "?q=" + memeName + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
            System.out.println("req : " + req);
            String responseFromAPI = UrlHandler.retrieveDataFromUrl(req);
            System.out.println("res : " + responseFromAPI);
            retList = new ArrayList<>();

            if (retList != null && responseFromAPI != null) {

                Gson gson = new Gson();
                JsonElement element = gson.fromJson (responseFromAPI, JsonElement.class);
                JsonObject jsonObj = element.getAsJsonObject();
                JsonArray result = jsonObj.getAsJsonArray("result");

                for(int i = 0; i < result.size(); i++){
                    JsonElement currentJsonObj = result.get(i);
                    System.out.println(currentJsonObj);
                    ResearchMemeListe data = gson.fromJson(currentJsonObj, ResearchMemeListe.class);
                    retList.add(data);
                }
            }
        }

        return retList;
    }


    private String createMeme(){

        String requestedURL = CONSTANTE.URL_INSTANCE_CREATE + "?username=" + CONSTANTE.USERNAME + "&password=" + CONSTANTE.PASSWORD + "&languageCode=" + CONSTANTE.LANGUAGE_CODE
                            + "&generatorID=" + generatorID + "&imageID=" + imageID + "&text0=" + text0 + "&text1=" + text1;
        return requestedURL;
    }
}
//TODO Lancer la requête, récupérer le JSON, Stocker l'URL du meme dans l'attribut memeURL