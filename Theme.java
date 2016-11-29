import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by Antoine on 25/11/2016.
 */
public class Theme {

    private static String themeFile = "./theme.txt";


    public static String generateTheme() throws IOException {

        String ret = null;
        Random r = new Random();



        FileInputStream fs= new FileInputStream(themeFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fs));
        int index = r.nextInt(Integer.valueOf(br.readLine()));
        //System.out.println("Theme index: " + index);
        for(int i = 0; i < index; ++i)
            br.readLine();

        ret = br.readLine();
        //System.out.println("Theme: " + ret);

        return ret;
    }

}
