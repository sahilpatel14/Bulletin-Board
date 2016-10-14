package in.sahilpatel.bulletinboard.firebase;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/14/2016.
 */
public class FirebasePostApi {

    private String TAG = FirebasePostApi.class.getSimpleName();

    private final String url = "/posts";
    private final Firebase mRef = FirebaseApi.getRef(url);

    public FirebasePostApi(){}

    public String addPost(Post post) {

        Firebase newRef = mRef.push();
        newRef.setValue(post);
        Log.d(TAG, "addPost: key : "+newRef.getKey());
        return newRef.getKey();
    }

    public void getPosts(String thread_id, ValueEventListener listener) {

        Query query = mRef.orderByChild("threadId").equalTo(thread_id).limitToLast(10);
        query.addListenerForSingleValueEvent(listener);
    }

    public void getPosts(String thread_id, ChildEventListener listener) {

        Query query = mRef.orderByChild("threadId").equalTo(thread_id).limitToLast(10);
        query.addChildEventListener(listener);
    }

    public void getAllPosts(String thread_id, ValueEventListener listener) {
        Query query = mRef.orderByChild("threadId").equalTo(thread_id);
        query.addListenerForSingleValueEvent(listener);
    }

    public void thumbsUp(String post_id,String newValue) {
        Firebase fRef = mRef.child(post_id+"/thumbsUp");
        fRef.setValue(newValue);
    }

    public void comments(String post_id,String newValue) {
        Firebase fRef = mRef.child(post_id+"/comments");
        fRef.setValue(newValue);
    }
}
