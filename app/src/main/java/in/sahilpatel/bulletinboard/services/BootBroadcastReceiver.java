package in.sahilpatel.bulletinboard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;

/**
 * Created by Administrator on 10/13/2016.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent startServiceIntent = new Intent(context, NotificationListenerService.class);

            String user_key = new MySharedPreferences(context).getUserKey();

            if (user_key.equals("No"))
            startServiceIntent.putExtra(NotificationListenerService.USER_ID,user_key);
            context.startService(startServiceIntent);
        }
    }
}
