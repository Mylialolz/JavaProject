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

        Stream<String> lines = Files.lines(Paths.get(themeFile));
        final String _line1 = lines.skip(0).findFirst().get();
        System.out.println("Ligne 1 :" + _line1);
        final int ligneNum = r.nextInt(Integer.parseInt(_line1));
        final String _line = lines.skip(ligneNum-1).findFirst().get();
        ret = _line;

        return ret;
    }

}
