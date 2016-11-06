import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Antoine on 04/11/2016.
 *
 */
public class Server {


    public static final int NEW_PLAYER = 1;
    public static final int NEW_CHAT_MESSAGE = 2;

    private ServerSocket mServerSocket;
    private int mCodeStopServeur = 0;
    private ArrayList<Socket> mListClient;
    private ArrayList<Joueur> mListJoueurs;
    private ArrayList<ClientManager> mManagers;

    private SalleJeu mSalleJeu;

    private int compteur = 0;

    public Server(){
        mListClient = new ArrayList<>();
        mListJoueurs = new ArrayList<>();
        mManagers = new ArrayList<>();
        mSalleJeu = new SalleJeu();
    }

    private int lancerServeur(int port){
        try {
            mServerSocket = new ServerSocket(port);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }


    public void demarrerServeur(int port){

        System.out.println("Démarrage du serveur...");

        if(this.lancerServeur(port) == 0) {
            System.out.println("Démarrage reussi.");
            try {
                while (mCodeStopServeur == 0) {

                    Socket socket = mServerSocket.accept();
                    mListClient.add(socket);
                    System.out.println("" + mListClient.size() + " client(s) connecté(s).");

                    ClientManager threadServer = new ClientManager(this, socket, compteur);
                    compteur++;
                    mManagers.add(threadServer);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Le démarrage a échoué.");
        }
    }


    public void arreterServeur(){
        mCodeStopServeur = 1;
    }

    synchronized public ArrayList<Socket> getListClients(){return mListClient;}

    synchronized public ArrayList<Joueur> getListJoueurs(){return mListJoueurs;}

    synchronized public ArrayList<ClientManager> getListManagers(){return mManagers;}


    synchronized public void newPlayer(){
        for(ClientManager cm : mManagers){
            cm.envoyerListeJoueursConnectes();
        }
    }

    synchronized public void newMessage(String s, int id){
        System.out.println("Distribution message...");
        for(ClientManager cm : mManagers){
            cm.distribuerMessageChat(s, id);
        }
    }




}
