/**
 * Created by Antoine on 04/11/2016.
 */
public class MainClient {

    public static void main(String[] args) {

        final int port = 1234;
        final String ip = "localhost";

        Client client = new Client(ip, port, "Antoine");

        return;
    }

}
