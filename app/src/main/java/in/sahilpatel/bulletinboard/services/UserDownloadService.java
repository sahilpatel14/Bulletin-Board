package in.sahilpatel.bulletinboard.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;

/**
 * Created by Administrator on 10/7/2016.
 */

public class    UserDownloadService extends IntentService {

    private static final String TAG = "UserDownloadService";

    public int result = Activity.RESULT_OK;
    public static final String USER_ID = "USER_ID";
    public static final String USER_IDS = "USER_IDS";
    public static final String NOTIFICATION = "user service receiver";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String SERVICE_NAME = UserDownloadService.class.getSimpleName();
    public static final String PARCELABLE_USERS = "PARCELABLE_USERS";
    public static final String RESULT = "RESULT";

    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String REQUEST_TYPE_DOWNLOAD_SELF = "SELF";
    public static final String REQUEST_TYPE_DOWNLOAD_SOME = "SOME";
    public static final String REQUEST_TYPE_DOWNLOAD_ALL = "ALL";


    private ArrayList<User> users;

    public UserDownloadService() {
        super(SERVICE_NAME);
        users = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String request_type = intent.getStringExtra(REQUEST_TYPE);


        switch (request_type) {
            case REQUEST_TYPE_DOWNLOAD_SELF :
                String user_id = intent.getStringExtra(USER_ID);
                downloadUsers(new String[]{user_id});
                break;

            case REQUEST_TYPE_DOWNLOAD_SOME :
                String user_ids[] = intent.getStringArrayExtra(USER_IDS);
                downloadUsers(user_ids);
                break;
            case REQUEST_TYPE_DOWNLOAD_ALL :
                downloadAllUsers();
        }
    }


    private void downloadAllUsers() {

        new FirebaseUserApi().getProbableGroupMembers(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    user.setUid(snapshot.getKey());
                    users.add(user);
                }
                result = Activity.RESULT_OK;
                publishResults(result);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void downloadUsers(String user_ids[]) {

        final String last_user_id = user_ids[user_ids.length - 1];

        for (final String user_id : user_ids) {
            new FirebaseUserApi().getUser(user_id, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    user.setUid(dataSnapshot.getKey());
                    users.add(user);

                    result = Activity.RESULT_OK;
                    if (user_id.equals(last_user_id)) {
                        publishResults(result);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void publishResults(int result){

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT,result);
        intent.putExtra(PARCELABLE_USERS,users);
        intent.putExtra(SERVICE_TYPE,SERVICE_NAME);
        sendBroadcast(intent);
        this.stopSelf();
    }
}




























