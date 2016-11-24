import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.*;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Created by Antoine on 18/10/2016.
 */
public class UrlHandler {

    public UrlHandler(){
    }

    public static String retrieveDataFromUrl(String chemin){

        URL page;
        InputStream is = null;
        String ret = "";

        try{

            page = new URL(chemin);
            is = page.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            if(in != null) {
                int oct = 0;
                while (true) {

                    oct = in.read();

                    if (oct != -1)
                        ret += (char) (oct & 0xFF);

                    else if (oct == -1)
                        break;
                }
            }
            else {
                ret = "";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            ret = "";
        } catch (IOException e) {
            e.printStackTrace();
            ret = "";
        }

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static String getJson(String url){
        String ret = retrieveDataFromUrl(url);
        if(!ret.matches("") || ret != null){

        }
        else {
            ret = null;
        }
        return ret;
    }


    public static Image getImageFromURL(String url) throws IOException {

        Image image = null;
        URL finalUrl = new URL(url);

        if (url != null && finalUrl != null) {
            image = ImageIO.read(finalUrl);
        }

        return image;
    }


}
