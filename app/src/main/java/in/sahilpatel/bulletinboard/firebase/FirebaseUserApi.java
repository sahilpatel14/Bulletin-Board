package in.sahilpatel.bulletinboard.firebase;


import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import in.sahilpatel.bulletinboard.fragments.AddMembersFragment;
import in.sahilpatel.bulletinboard.model.User;

public class FirebaseUserApi {

    /**
     * This class would interact with usersObject on Firebase. Any interaction
     * between Firebase Users object and client must happen through this class.
     * The activity or fragment requesting data must provide the unique id for the
     * user to get model.
     */

    private String TAG = FirebaseUserApi.class.getSimpleName();
    public static final String DEMO_THREAD_KEY = "-KU21POFaZdF6d322erQ";
    /**
     * Generating url for users object. FirebaseApi.getRef returns the new
     * url.
     */
    private final String url = "/users";
    private final Firebase mRef = FirebaseApi.getRef(url);

    public FirebaseUserApi() {}

    /**
     * Adds a new user to Firebase users object. It will only be executed once.
     * Only for a unique uid.
     * @param user
     * @return FireBase generated user id.
     */
    public String addUser(User user) {
        Firebase newRef = mRef.push();
        newRef.setValue(user);
        Log.d(TAG, "addUser: key : "+newRef.getKey());

        String key = newRef.getKey();
        user.setUid(key);
        new FirebaseThreadApi().addMember(DEMO_THREAD_KEY,user);
        return key;
    }

    /**
     * Checks if user already exists or not. IF so, it will notify in callback
     * @param user
     * @param mListener
     */
    public void userExists(User user,ValueEventListener mListener){
        Query query = mRef.orderByChild("uid").equalTo(user.getUid()).limitToFirst(1);
        query.addListenerForSingleValueEvent(mListener);
    }

    /**
     * Takes the key and one callback and returns the User POJO. this object
     * will contain all the data related to user. Callback is necessary because
     * Networking fetch calls on Firebase are Asynchronous.
     * @param key
     * @param mCallback
     */
    public void getCurrentUser(final String key, final OnUserDataObtained mCallback){

        if (mCallback == null) {
            throw new ClassCastException("You must implement the interface. Fetching user is asynchronous");
        }

        Query query = mRef.child("/"+key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mCallback.onSuccess(user);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mCallback.onFailure("Firebase User Api. Fetching user failed",firebaseError);
            }
        });
    }

    public void getThreadNames(String user_key, ValueEventListener mListener) {
        Query tRef = mRef.child("/"+user_key+"/threads").orderByValue();

        Log.d(TAG, "getThreadNames: "+tRef.toString());

        tRef.addListenerForSingleValueEvent(mListener);
    }

    public void getOwnedThreadNames(String user_key, ValueEventListener mListener) {
        Firebase tRef = mRef.child("/"+user_key+"/owned_threads");

        Log.d(TAG, "getThreadNames: "+tRef.toString());

        tRef.addListenerForSingleValueEvent(mListener);
    }

    public void getProbableGroupMembers(ValueEventListener mListener) {
        mRef.addListenerForSingleValueEvent(mListener);
    }

    /**
     * Add thread info that is recently created to user's object. User owned thread.
     * We will pass two string values. thread name and thread id.
     */
    public void addOwnedThread(final String key,final String thread_name,final String thread_id) {
        String childString = "/"+key+"/owned_threads/"+thread_id;
        Firebase newRef = mRef.child(childString);
        String kkey = newRef.getKey();
        newRef.setValue(thread_name);
        Log.d(TAG, "addOwnedThread: key : "+kkey);
    }

    /**
     * Adds thread into moderator object of User. These are the threads which the user is
     * moderator to.
     */
    public void addModeratedThread(final String key, String thread_name, String thread_id) {
        String childString = "/"+key+"/moderator_threads/"+thread_id;
        Firebase newRef = mRef.child(childString);
        newRef.setValue(thread_name);
        String message = "You have been promoted as moderator of group "+thread_name+".";
        updateLastUserActivity(key,message,User.LAST_ACTIVITY_STATUS_UNSEEN);
        Log.d(TAG, "addModeratedThread: key : "+newRef.getKey());
    }

    /**
     * Adds thread into threads object of User. These are the threads which the
     * user is member of.
     */
    public void addMemberThread(final String key, String thread_name, String thread_id) {
        Firebase newRef = mRef.child("/"+key+"/threads/"+thread_id);
        newRef.setValue(thread_name);

        String message = "You have been added to group "+thread_name+".";
        updateLastUserActivity(key,message,User.LAST_ACTIVITY_STATUS_UNSEEN);
        Log.d(TAG, "addMemberThread: key : "+newRef.getKey());
    }

    public void getUserImage(String user_key, ValueEventListener listener){

        Firebase newRef = mRef.child("/"+user_key+"/profile_image_url/");
        newRef.addValueEventListener(listener);
    }

    public void getUser(String user_key, ValueEventListener listener){

        Firebase newRef = mRef.child("/"+user_key);
        newRef.addValueEventListener(listener);
    }

    public void exitThread(String user_key,String thread_key, Firebase.CompletionListener listener){

        Firebase newRef = mRef.child("/"+user_key+"/threads/"+thread_key);
        newRef.removeValue(listener);
    }

    public void updateLastUserActivity(final List<User> users, String message) {

        for (User user : users) {
            updateLastUserActivity(user.getUid(),message,User.LAST_ACTIVITY_STATUS_UNSEEN);
        }
    }

    public void getLastActivity(final String user_id, ValueEventListener listener){

        String childUrl = "/"+user_id+"/last_activity";
        Firebase newRef = mRef.child(childUrl);
        Log.d(TAG, "getLastActivity: "+newRef.getPath());
        newRef.addValueEventListener(listener);
    }

    public void updateLastUserActivity(final String owner,final String[] users, String message) {

        for (String user : users) {
            if (!user.equals(owner))
                updateLastUserActivity(user,message,User.LAST_ACTIVITY_STATUS_UNSEEN);
        }
    }

    public void updateLastUserActivity(final String key,String message,String status){
        String childUrl = "/"+key+"/last_activity";
        Map<String,Object> last_activity = new HashMap<>();
        last_activity.put("timestamp",System.currentTimeMillis()+"");
        last_activity.put("message",message);
        last_activity.put("status",status);

        Log.d(TAG, "updateLastUserActivity: "+"happening");
        Firebase newRef = mRef.child(childUrl);
        newRef.updateChildren(last_activity);
    }

    public void updateLastUserActivityStatus(String user_key, String status) {
        String childUrl = "/"+user_key+"/last_activity/status";
        Firebase newRef = mRef.child(childUrl);
        newRef.setValue(status);
    }
}
