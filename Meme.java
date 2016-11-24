import java.awt.*;
import java.util.ArrayList;
import com.google.gson.*;

/**
 * Created by Joris on 23/11/2016.
 */
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.*;

public class Meme {

    private String memeURL;
    private String generatorID; // ID of the generator
    private String imageID; // ID of the image (contained at the end of imageURL)
    private String text0; // Top text (or bottom if text1 is empty
    private String text1; // Bottom text

    private static int pageSize = 6;


    public Meme(String genID, String imaID, String txt0, String txt1){

        generatorID = genID;
        imageID = imaID;
        text0 = txt0;
        text1 = txt1;
        String request = createMeme();
        getMeme(request);
    }

    public Meme(String genID, String imaID, String txt0){

        generatorID = genID;
        imageID = imaID;
        text0 = txt0;
        text1 = null;
        String request = createMeme();
        getMeme(request);

    }

    public static ArrayList<ResearchMemeListe> researchMemes(String memeName, int page){

        ArrayList<ResearchMemeListe> retList = null;

        if(memeName != null) {

            String req = CONSTANTE.URL_GENERATOR_SEARCH + "?q=" + memeName + "&pageIndex=" + page + "&pageSize=" + pageSize;
            //System.out.println("req : " + req);
            String responseFromAPI = UrlHandler.retrieveDataFromUrl(req);
            //System.out.println("res : " + responseFromAPI);
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

    private void getMeme(String request){

       String strJSON = UrlHandler.retrieveDataFromUrl(request);
        Gson gson = new Gson();
        JsonElement element = gson.fromJson (strJSON, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        JsonArray result = jsonObj.getAsJsonArray("result");
        CreatedMeme meme =  gson.fromJson(result, CreatedMeme.class);
        memeURL = meme.getInstanceImageUrl();
    }
}
//TODO Lancer la requête, récupérer le JSON, Stocker l'URL du meme dans l'attribut memeURL