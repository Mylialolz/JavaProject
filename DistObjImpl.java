/** DistObjImpl.java */
import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.* ;
import java.rmi.server.*;
import java.util.ArrayList;

public class DistObjImpl extends UnicastRemoteObject
        implements DistObj {

    /** constructeur obligatoire pour l’exception */
    public DistObjImpl() throws RemoteException {}
    /** l’implémentation de ma méthode distribuée */

    @Override
    public ArrayList<String> listMeths() {
        System.out.println("Called");
        ArrayList<String> list = new ArrayList<>();
        Class c = this.getClass();
        for (Method method : c.getDeclaredMethods()) {
                list.add(method.getName());
        }
        return list;
    }

    @Override
    public String callMeth(String methName) {

        if(!methName.matches("callMeth") && !methName.matches("listMeths")) {
            System.out.println("Invoked");
            Class c = this.getClass();
            Object obj = null;
            try {
                obj = c.newInstance();
                Method meth = c.getMethod(methName, null);
                String s = (String)meth.invoke(obj, null);
                return s;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMsg() {
        return "Kikoo Lolz la tantouse !";
    }

    @Override
    public String lul() throws RemoteException {
        return "uluru";
    }

    @Override
    public String corentinVaFaireSapussyCeSoirPLSIncoming() throws RemoteException {
        return "Corentin il fait le fuck boi mais je vais le mettre en pls sur overwatch ce soir !!!";
    }

    @Override
    public String IlovePussys() throws RemoteException {
        return "meow";
    }

    @Override
    public String GetRekt() throws RemoteException {
        return "get rekt uluru";
    }

    @Override
    public String JorisJetaime() throws RemoteException {
        return "antoine + joris = <3 (#nohomo lul)";
    }
}
