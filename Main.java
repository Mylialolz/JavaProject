import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Antoine on 18/10/2016.
 *
 */
public class Main {

    public static void main(String[] args) {


        final int port = 1234;
        final String ip = "localhost";

        Server server = Server.getInstance();
        server.demarrerServeur(port);

        server.arreterServeur();

        return;
    }

}
