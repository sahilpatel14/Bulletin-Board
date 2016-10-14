package in.sahilpatel.bulletinboard.firebase;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import in.sahilpatel.bulletinboard.model.Comment;

/**
 * Created by Administrator on 10/3/2016.
 */

public class FirebaseCommentApi {

    private String TAG = FirebaseCommentApi.class.getSimpleName();

    private final String url = "/comments";
    private final Firebase mRef = FirebaseApi.getRef(url);

    public FirebaseCommentApi(){}

    public String addComment(Comment comment, int count) {

        Firebase newRef = mRef.push();
        newRef.setValue(comment);
        Log.d(TAG, "addPost: key : "+newRef.getKey());
        new FirebasePostApi().comments(comment.getPost_id(),(count+1)+"");

        return newRef.getKey();
    }


    public void getComments(String post_id, ChildEventListener listener) {

        Query query = mRef.orderByChild("post_id").equalTo(post_id);
        query.addChildEventListener(listener);
    }
}
