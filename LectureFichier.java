import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;

/**
 * Created by Antoine on 18/10/2016.
 */
public class LectureFichier {

    public LectureFichier(){

    }


    public static void lireFichierParOctet(String chemin) {

        FileInputStream file = null;

        try {
            file = new FileInputStream(chemin);
            int oct = 0;
            do {

                oct = file.read();
                //char c = (char)(oct & 0xFF);
                //System.out.print("" + c);

            } while (oct != -1);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }


}
