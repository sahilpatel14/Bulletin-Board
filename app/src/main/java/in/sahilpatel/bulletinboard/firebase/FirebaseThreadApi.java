package in.sahilpatel.bulletinboard.firebase;


import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.User;

public class FirebaseThreadApi {

    private String TAG = FirebaseThreadApi.class.getSimpleName();

    private final String url = "/threads";
    private final Firebase mRef = FirebaseApi.getRef(url);

    public FirebaseThreadApi(){}


    public String addThread(NewThread thread) {

        Firebase newRef = mRef.push();
        newRef.setValue(thread);
        Log.d(TAG, "addThread: key : "+newRef.getKey());
        return newRef.getKey();
    }


    public void getThread(String thread_id, ValueEventListener listener) {

        Query query = mRef.child("/"+thread_id);
        query.addValueEventListener(listener);
    }

    public void addMember(String thread_id, User user) {
        Firebase fRef = mRef.child(thread_id+"/"+"membersList/"+user.getUid());
        fRef.setValue(user.getName());
    }

//    public void sendNotificationToMembers(final String post_id,String post_owner_name, final String thread_id) {
//
//
//
//        Firebase newRef = mRef.child("/"+key+"/threads/"+thread_id);
//        newRef.setValue(thread_name);
//
//        String message = "You have been added to group "+thread_name+".";
//        updateLastUserActivity(key,message);
//        Log.d(TAG, "addMemberThread: key : "+newRef.getKey());
//    }
}
