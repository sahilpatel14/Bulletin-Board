package in.sahilpatel.bulletinboard.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.adapters.CommentsAdapter;
import in.sahilpatel.bulletinboard.firebase.FirebaseCommentApi;
import in.sahilpatel.bulletinboard.firebase.FirebasePostApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.firebase.OnUserDataObtained;
import in.sahilpatel.bulletinboard.model.Comment;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

/**
 * Created by Administrator on 10/3/2016.
 */

public class CommentsFragment extends DialogFragment {

    /**
     * Opens up the Comments window for every post. All the comments
     * are fetched directly from this fragment. We only pass the post
     * and thread object to it.
     *
     */

    private static final String TAG = "CommentsFragment";
    private EditText field_comments;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    private List<Comment> comments;                             //  List of all the comments
    private int count;                                          //  No of comments made so far.
    private Post post;                                          //  The post for which the comments we're made.
    private NewThread thread;                                   //  The parent thread
    private User user;                                          //  The logged in user


    public CommentsFragment() {

    }


    public void setPost(Post post) {
        this.post = post;
        this.count = Integer.parseInt(post.getComments());
    }
    public void setThread(NewThread thread) {
        this.thread = thread;
    }

    public void setUser(User user) { this.user = user; }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.comments_dialog_layout, container, false);

        field_comments = (EditText) rootView.findViewById(R.id.field_comment);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(comments,getContext());
        recyclerView.setAdapter(adapter);


        fetchComments();


        /**
         * Called when the user clicks on post comment button.
         */
        rootView.findViewById(R.id.button_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });


        /**
         * This section tries to scroll the recycler down when the layout of
         * DialogFragment changes. This enables the user to see the last made
         * comment directly when he clicks to write a comment.
         */
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (bottom < oldBottom) {
                    recyclerView.smoothScrollToPosition(comments.size());
                }
            }
        });

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        return dialog;
    }

    /**
     * Fetches all the comments for this particular post.
     * It also updates the list if new comments are made.
     * The newly created comments are automatically added
     * at the end of the screen.
     */
    private void fetchComments() {

        new FirebaseCommentApi().getComments(post.getPostId(), new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {

                Comment comment = snapshot.getValue(Comment.class);
                comment.setComment_id(snapshot.getKey());
                comments.add(comment);

                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(comments.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //  Comments can't be updated
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //  Comments cant be deleted or removed.
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    /**
     * This method first validates the input, then it posts the comment
     * on the server. Updates recent activity message for the owner of that
     * post and also increments the comments counter.
     */
    private void addComment() {

        final String content = field_comments.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Can not leave the field blank!", Toast.LENGTH_SHORT).show();
            return;
        }


                /**
                 * Creating the comment object and filling all data.
                 * Sending it to server.
                 */
                Comment comment = new Comment();
                comment.setUser_name(user.getName());
                comment.setPic_url(user.getProfile_image_url());
                comment.setTimestamp(System.currentTimeMillis()+"");
                comment.setContent(content);
                comment.setPost_id(post.getPostId());

                new FirebaseCommentApi().addComment(comment,count);

                /**
                 * After commenting we clear the comments field.
                 * It will be visible on recyclerView automatically.
                 */
                Toast.makeText(getContext(), "Comment Posted.", Toast.LENGTH_SHORT).show();
                field_comments.setText("");

                /**
                 * Updating the comments count for the post. The comment was
                 * posted successfully.
                 */

                new FirebasePostApi().comments(post.getPostId(),(count+1)+"");

                /**
                 * Updating recent activity message for the post owner. All the three
                 * operations mentioned above and below are done three separate objects.
                 * Taken care by three different apis.
                 */
                String message = user.getName()+" commented on a post you shared on "+thread.getThread_name()+" thread.";
                String status = User.LAST_ACTIVITY_STATUS_UNSEEN;

                Map<String,String> owner = post.getOwner();
                ArrayList<String> keys = new ArrayList<>(owner.keySet());

                if (user.getUid().equals(keys.get(0))) {
                    message = "you commented on a post that you shared on "+thread.getThread_name()+" thread.";
                    status = User.LAST_ACTIVITY_STATUS_SEEN;
                }
                new FirebaseUserApi().updateLastUserActivity(keys.get(0),message,status);


    }
}


























