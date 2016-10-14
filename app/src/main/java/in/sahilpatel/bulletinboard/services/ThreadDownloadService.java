package in.sahilpatel.bulletinboard.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.sahilpatel.bulletinboard.firebase.FirebaseThreadApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelableNewThread;

/**
 * Created by Administrator on 9/30/2016.
 */
public class ThreadDownloadService extends IntentService {
    
    public int result = Activity.RESULT_OK;
    public static final String USER_ID = "USER_ID";
    public static final String THREAD_IDS = "THREAD_IDS";
    public static final String RESULT = "RESULT";
    public static final String PARCELABLE_THREADS = "PARCELABLE_POSTS";
    public static final String NOTIFICATION = "service thread download receiver";
    private static final String TAG = "ThreadDownloadService";
    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String DOWNLOAD_THREAD_METADATA = "METADATA";
    public static final String DOWNLOAD_THREAD_ALL = "ALL_DATA";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String SERVICE_NAME = ThreadDownloadService.class.getSimpleName();


    private ArrayList<ParcelableNewThread> threads;

    public ThreadDownloadService() {
        super("ThreadDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String request_type = intent.getStringExtra(REQUEST_TYPE);
        threads = new ArrayList<>();
        String user_id = intent.getStringExtra(USER_ID);
        switch (request_type) {
            case DOWNLOAD_THREAD_METADATA :
                    threadDownloadMetaData(user_id);
                                        break;
            case DOWNLOAD_THREAD_ALL :
                String thread_ids[] = intent.getStringArrayExtra(THREAD_IDS);
                    threadDownloadAll(thread_ids);
        }
    }

    private void threadDownloadAll(String thread_ids[]) {

        if (thread_ids.length == 0){
            result = Activity.RESULT_OK;
            publishResults(result);
            return;
        }

        final String last_thread_id = thread_ids[thread_ids.length-1];
        threads = new ArrayList<>();

        for (final String thread_id : thread_ids) {
            new FirebaseThreadApi().getThread(thread_id, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    NewThread newThread = dataSnapshot.getValue(NewThread.class);
                    newThread.setThread_id(dataSnapshot.getKey());
                    threads.add(new ParcelableNewThread(newThread));

                    result = Activity.RESULT_OK;
                    if (thread_id.equals(last_thread_id)) {
                        publishResults(result);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    result = Activity.RESULT_CANCELED;
                    publishResults(result);
                }
            });
        }
    }

    private void threadDownloadMetaData(String user_id) {

        final Map<String,String> map_list = new HashMap<>();
        new FirebaseUserApi().getThreadNames(user_id,new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    String name = shot.getValue(String.class);
                    NewThread newThread = new NewThread(shot.getKey(),name);

                    threads.add(new ParcelableNewThread(newThread));

                }
                new MySharedPreferences(getApplicationContext()).saveThreadNames(map_list);
                result = Activity.RESULT_OK;
                publishResults(result);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                result = Activity.RESULT_CANCELED;
                publishResults(result);
            }
        });

    }

    private void publishResults(int result) {

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT,result);
        intent.putExtra(PARCELABLE_THREADS,threads);
        intent.putExtra(SERVICE_TYPE,SERVICE_NAME);
        sendBroadcast(intent);
        this.stopSelf();
    }
}
