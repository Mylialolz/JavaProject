import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Antoine on 18/11/2016.
 */
public class ServeurRMI {


    public static void main(String[] arg) {

        try {
            LocateRegistry.createRegistry(1234);
            DistObjImpl d = new DistObjImpl();

            Naming.rebind(
                    "rmi://localhost:1234/monObj", d);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
