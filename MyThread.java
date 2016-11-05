/**
 * Created by Antoine on 18/10/2016.
 */
public class MyThread extends Thread implements Runnable {

    private int mDuree;
    private int mId;

    public MyThread(int id, int priorite, int duree){
        this.mDuree = duree;
        this.mId = id;
        this.setPriority(priorite);
    }

    @Override
    public void run() {
        int i = 0;
        while(i < mDuree) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++i;
            System.out.print("Thread " + mId + ",t_id :" + this.getId() + ", priorite : " + this.getPriority() + "\n");
        }
    }


}
