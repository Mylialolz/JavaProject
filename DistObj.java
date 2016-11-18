/** interface distribuée : DistObj.java */

import java.rmi.* ;
import java.util.ArrayList;

interface DistObj extends Remote {


    /** Ma méthode que je distribue */
    public String getMsg() throws RemoteException;

    public String lul() throws RemoteException;

    public String corentinVaFaireSapussyCeSoirPLSIncoming() throws RemoteException;

    public String IlovePussys() throws RemoteException;

    public String GetRekt() throws RemoteException;

    public String JorisJetaime() throws RemoteException;

    public ArrayList<String> listMeths() throws RemoteException;

    public String callMeth(String methName) throws  RemoteException;


}
