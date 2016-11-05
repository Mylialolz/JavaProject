import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Antoine on 18/10/2016.
 */
public class Clock implements Runnable {


    private String mNom;
    private Thread mThread;
    private int mValue;

    private DateFormat mDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Clock(String nom, int value){
        this.mNom = nom;
        this.mValue = value;
        mThread = new Thread(this);
        mThread.start();
    }


    @Override
    public void run() {
        int i = 0;
        if(mThread != null) {
            while (i < mValue && mThread != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDF.setTimeZone(TimeZone.getTimeZone("Europe/"+this.mNom));
                System.out.println("Date and time in :" + this.mNom + " " + mDF.format(new Date()));
                ++i;
            }
        }
        else {
            System.out.println("Fin thread.");
        }
        return;
    }


    public void stopClock(){
        mThread = null;
        return;
    }

}
