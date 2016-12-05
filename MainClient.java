import javax.swing.*;

/**
 * Created by Antoine on 04/11/2016.
 */
public class MainClient {

    public static void main(String[] args) {

        final int port = 1234;
        final String ip = "localhost";

        //ClientGUI clientGUI = new ClientGUI(ip, port);
        ClientGUI clientGUI =  ClientGUI.getInstance(ip, port);

        JFrame frame = new JFrame("Meme Competitor");
        frame.setContentPane(clientGUI.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                clientGUI.disconnect();
            }
        });
        frame.pack();
        frame.setVisible(true);

        return;
    }

}
