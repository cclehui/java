import com.sun.javafx.binding.StringFormatter;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/11.
 */
public class Temp {

    public static void main(String[] args) {

        Timer timer = new Timer();

        TimerTask task1 = new Mytask("111111111");
        TimerTask task2 = new Mytask("222222222");

        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        System.out.println("start xxxxxxxxx:\t"  + curTime);

      timer.scheduleAtFixedRate(task1, 1000, 2000);
//        timer.schedule(task2, 1000, 2000);
    }
}

class Mytask extends TimerTask {

    protected String tag;
    protected int count = 1;

    Mytask(String tag){
        this.tag = tag;
    }

    public void run() {

        String curTime = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss").format(System.currentTimeMillis());
        System.out.println(curTime + "\t" + tag);

        try {
            if (count == 1) {
                Thread.sleep(10000);
            } else {
                Thread.sleep(1000);
            }

            count++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        curTime = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss").format(System.currentTimeMillis());
        System.out.println(curTime + "\t" + tag);

    }
}
