import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Antoine on 04/11/2016.
 */
public class ClientManager implements Runnable {

    private Socket mSocket;
    private int stopThread;
    private Thread mThread;
    private Server mServeur;

    private int mId;

    private Joueur mJoueur;
    private DataInputStream in;
    private DataOutputStream out;
    ObjectOutputStream oos;

    public ClientManager(Server s, Socket e, int id){

        mServeur = s;
        mSocket = e;
        mId = id;

        stopThread = 0;

        try {
            in = new DataInputStream(mSocket.getInputStream());
            out = new DataOutputStream(mSocket.getOutputStream());
            oos = new ObjectOutputStream(mSocket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        mThread = new Thread(this);
        mThread.start();

    }


    @Override
    public void run() {


        while(stopThread != 1){
            handleRequests();
        }

        handleClose();

    }


    public void envoyerListeJoueursConnectes(){

        try {

            out.writeUTF(CONSTANTE.NOUVELLE_LISTE);
            //System.out.println(mServeur.getListJoueurs().size());
            ArrayList<Joueur> list = (ArrayList<Joueur>)mServeur.getListJoueurs().clone();
            oos.writeObject(list);

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


    public void distribuerMessageChat(String s, int id){

        if(id != mId) {

            try {
                out.writeUTF(CONSTANTE.CLIENT_SERVER_CHAT);
                out.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    private void handleRequests() {

        try {

            String data = in.readUTF();
            System.out.print("id_req:" + data + "\n");
            switch(data){
                default:break;

                case CONSTANTE.CLOSE_CONNECTION :
                    stopThread = 1;
                    out.writeUTF("ok");
                    break;

                case CONSTANTE.CLIENT_SERVER_PSEUDO:
                    String pseudo = in.readUTF();
                    mJoueur = new Joueur(pseudo);
                    System.out.println("Joueur " + pseudo + " est connecté.");
                    mServeur.getListJoueurs().add(mJoueur);
                    mServeur.newPlayer();
                    break;

                case CONSTANTE.CLIENT_SERVER_CHAT :
                    String m = in.readUTF();
                    String message = mJoueur.getPseudo() + ": " + m;
                    mServeur.newMessage(message, mId);
                    break;


            }

        } catch (java.net.SocketException se) {
            stopThread = 1;
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            stopThread = 1;
        }
    }


    private void handleClose() {
        try {
            out.close();
            in.close();
            oos.close();

            mSocket.close();

            mServeur.getListClients().remove(mSocket);
            mServeur.getListJoueurs().remove(mJoueur);
            mServeur.getListManagers().remove(this);

            System.out.println("Un client s'est déconnecté.");
            System.out.println("Il reste " + mServeur.getListClients().size() + " client(s) sur le serveur.");

            mServeur.newPlayer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*if(!data.matches("stop")) {
                if (data.matches("image")) {
                    BufferedImage bufferedImage = ImageIO.read(mSocket.getInputStream());
                    File outputfile = new File("C:/Users/Antoine/Desktop/reception.png");
                    ImageIO.write(bufferedImage, "png", outputfile);
                }
                System.out.println("Donnees : " + data);
                out.writeUTF("ok");
            }*/

}
