package in.sahilpatel.bulletinboard.fragments.posts;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.firebase.FirebasePostApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareThoughtsFragment extends Fragment {

    /**
     * Stores the current user who is logged in.
     */
    private User user;

    private static final String TAG = "ShareThoughtsFragment";
    private EditText field_thoughts;
    private ProgressDialog mProgressDialog;

    public ShareThoughtsFragment() {}
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share_thoughts, container, false);
        field_thoughts = (EditText)rootView.findViewById(R.id.field_thoughts);
        mProgressDialog = new ProgressDialog(getContext());
        return rootView;
    }


    /**
     * Check if the fields are filled properly or not. Later call the uploadPost
     * method to push it to the server.
     * @param thread
     */
    public void post(NewThread thread) {

        String msg = field_thoughts.getText().toString();
        if(msg == null || msg.isEmpty()) {
            Toast.makeText(getContext(), "Cannot leave the field blank", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadPost(msg,thread.getThread_id());

        Map<String, String> membersMap = thread.getMembersList();
        Set<String> keys = membersMap.keySet();
        String[] members = keys.toArray(new String[keys.size()]);

        String message = user.getName()+" shared some thought on thread "+thread.getThread_name()+".";
        new FirebaseUserApi().updateLastUserActivity(user.getUid(),members,message);
    }


    /**
     * Uploading the post to the server, we set the required fields
     * and also give defaults where necessary.
     * @param msg
     * @param thread_id
     */
    private void uploadPost(String msg, String thread_id) {

        mProgressDialog.setMessage("Posting...");
        mProgressDialog.show();

        final Post post = new Post();
        post.setHeading(msg);
        post.setThreadId(thread_id);
        post.setComments("0");
        post.setThumbsUp("0");
        post.setTimestamp(System.currentTimeMillis()+"");
        post.setPostType(Post.POST_TYPE_SHARE_THOUGHTS+"");
        Map<String,String> owner = new HashMap<>();
        owner.put(user.getUid(),user.getName());
        post.setOwner(owner);
        post.setPicUrl(user.getProfile_image_url());
        new FirebasePostApi().addPost(post);



        mProgressDialog.dismiss();
        Toast.makeText(getContext(), "Posted to thread", Toast.LENGTH_SHORT).show();
        field_thoughts.setText("");
    }
}
