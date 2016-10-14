package in.sahilpatel.bulletinboard.backup;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.activities.LauncherActivity;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.support.TimestampConversion;


/**
 * Created by Administrator on 10/13/2016.
 */

public class NotificationListenerService extends IntentService {

    private static final String TAG = "NotificationService";
    public static final String USER_ID = "USER_ID";
    public static final String SERVICE_NAME = NotificationListenerService.class.getSimpleName();

    public static final int mId = 0;
    private int notif_count = 0;

    public NotificationListenerService() {
        super("NotificationListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent: "+"Starting the service.");

        String user_id = intent.getStringExtra(USER_ID);
        getNotification(user_id);
    }


    private void getNotification(String user_id) {

        new FirebaseUserApi().getLastActivity(user_id, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    return;
                }

                String message = dataSnapshot.child("message").getValue(String.class);
                String timestamp = dataSnapshot.child("timestamp").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);

                if (status.equals(User.LAST_ACTIVITY_STATUS_UNSEEN)){
                    setNotification(message, timestamp);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void setNotification(String message, String timestamp) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.ic_whiteboard_small);
        notif_count++;
        if (notif_count == 1){
            mBuilder.setContentTitle("Bulletin Board");
        }
        else{
            mBuilder.setContentTitle("Bulletin Board ("+notif_count+")");
        }

        String ts = TimestampConversion.getReadableTimestamp(timestamp);
        mBuilder.setContentText(message+" - "+ts);
        Intent resultIntent = new Intent(this, LauncherActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LauncherActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat c = NotificationManagerCompat.from(this);
        Notification notification = mBuilder.build();
        startForeground(mId,notification);
        c.notify(mId,notification);
    }
}


