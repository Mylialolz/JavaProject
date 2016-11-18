import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Antoine on 18/11/2016.
 */
public class RMIClient implements Remote {


    public RMIClient() {

    }

    public static void main(String[] args) {

        try {

            DistObj dObj = (DistObj) Naming.lookup("rmi://192.168.128.72:1234/monObj");
            System.out.println("Message : " + dObj.getMsg());

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



}
