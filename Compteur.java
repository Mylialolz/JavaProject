/**
 * Created by Antoine on 18/10/2016.
 */
public class Compteur implements Runnable {

    private int mValue;
    private int mId;
    private Thread mThread;


    public Compteur(int id, int value){
        this.mId = id;
        this.mValue = value;
        mThread = new Thread(this);
        mThread.start();
    }


    @Override
    public void run() {
        int i = 0;
        while(i < mValue){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Compteur " + this.mId + ", valeur compteur = " + i);
            ++i;
        }
    }



}
