package in.sahilpatel.bulletinboard.support;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 10/7/2016.
 */

public class HelpTextDisplay extends TimerTask{

    private boolean started;
    private Handler handler;
    private HelpTextCallback callback;

    private static final int INTERVAL = 5500;

    private Timer timer;
    private static final String TAG = "HelpTextDisplay";

    private int counter;

    public HelpTextDisplay(HelpTextCallback callback) {

        started = false;
        handler = new Handler();
        counter = 1;
        this.callback = callback;
    }

    @Override
    public void run() {

        int index = counter%5;
        Log.d(TAG, "run: "+index);
        callback.nextHelpText(index);
        counter++;
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }

    public void start() {

        if (timer != null)
            return;
        timer = new Timer();
        timer.scheduleAtFixedRate(this,0,INTERVAL);
    }

    public interface HelpTextCallback{
        void nextHelpText(int index);
    }
}


