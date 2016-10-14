package in.sahilpatel.bulletinboard.adapters.posts.holders;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.support.TimestampConversion;

/**
 * Created by Administrator on 9/28/2016.
 */
public abstract class PostHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener{

    public CircleImageView user_image_container;
    public TextView user_name_container, post_timestamp_container;
    public LinearLayout button_comments, button_thumbsUp;
    public TextView counter_thumbsUp, counter_comments;

    private static final String TAG = "PostHolder";

    private Post post;

    private ImageButton drawable_thumbsUp;
    private TextView label_thumbsUp;

    private ImageView post_options_button;

    protected final String baseUrl = "http://www.sahilpatel.in/BulletinBoard/uploads/";

    public static String url = "http://www.sahilpatel.in/BulletinBoard/uploads/";

    private Activity activity;
    private OnPostClickedListener mCallback;


    public PostHolder(View itemView, Activity activity) {

        super(itemView);

        user_image_container = (CircleImageView) itemView.findViewById(R.id.post_user_profile_photo);
        user_name_container = (TextView)itemView.findViewById(R.id.post_user_name);
        post_timestamp_container = (TextView)itemView.findViewById(R.id.post_time_stamp);

        counter_comments = (TextView)itemView.findViewById(R.id.counter_comments);
        counter_thumbsUp = (TextView)itemView.findViewById(R.id.counter_thumbsUp);

        button_comments = (LinearLayout) itemView.findViewById(R.id.button_comments);
        button_thumbsUp = (LinearLayout) itemView.findViewById(R.id.button_thumbsUp);

        drawable_thumbsUp = (ImageButton)itemView.findViewById(R.id.drawable_thumbsUp);
        label_thumbsUp = (TextView)itemView.findViewById(R.id.label_thumbsUp);

        post_options_button = (ImageView)itemView.findViewById(R.id.post_options_buttons);

        post_options_button.setOnCreateContextMenuListener(this);
        this.activity = activity;
    }

    public PostHolder(View itemView, Activity activity, OnPostClickedListener mCallback) {

        super(itemView);

        user_image_container = (CircleImageView) itemView.findViewById(R.id.post_user_profile_photo);
        user_name_container = (TextView)itemView.findViewById(R.id.post_user_name);
        post_timestamp_container = (TextView)itemView.findViewById(R.id.post_time_stamp);

        counter_comments = (TextView)itemView.findViewById(R.id.counter_comments);
        counter_thumbsUp = (TextView)itemView.findViewById(R.id.counter_thumbsUp);

        button_comments = (LinearLayout) itemView.findViewById(R.id.button_comments);
        button_thumbsUp = (LinearLayout) itemView.findViewById(R.id.button_thumbsUp);

        drawable_thumbsUp = (ImageButton)itemView.findViewById(R.id.drawable_thumbsUp);
        label_thumbsUp = (TextView)itemView.findViewById(R.id.label_thumbsUp);

        post_options_button = (ImageView)itemView.findViewById(R.id.post_options_buttons);

        post_options_button.setOnCreateContextMenuListener(this);

        this.activity = activity;

        try {
            this.mCallback = mCallback;
        }
        catch (ClassCastException e){
            throw new ClassCastException("You must implement OnPostClickedListener.");
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void bindData(Post post) {

        this.post = post;

        Map<String,String> owner = post.getOwner();
        ArrayList<String> keys = new ArrayList<>(owner.keySet());

        Log.d(TAG, "bindData: "+post.getPostId());

        user_name_container.setText(owner.get(keys.get(0)));
        post_timestamp_container.setText(TimestampConversion.getReadableTimestamp(post.getTimestamp()));
        Glide.with(activity).load(post.getPicUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .dontAnimate()
                .into(user_image_container);

        counter_thumbsUp.setText(post.getThumbsUp());
        counter_comments.setText(post.getComments());

        button_comments.setOnClickListener(onCommentsClickedListener(post.getPostId(),post.getComments()));
        button_thumbsUp.setOnClickListener(onThumbsUpClickedListener(post.getPostId(),post.getThumbsUp()));
    }

    public abstract void bindExtra(Post post);

    public View.OnClickListener onCommentsClickedListener(final String postId, final String cCount) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(cCount);
                mCallback.onCommentsClicked(postId,count);
            }
        };
    }

    public View.OnClickListener onThumbsUpClickedListener(final String postId, final String tCount) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(tCount);

                switch (clickedThumbsUp()){
                    case "clicked" : mCallback.onThumbsUpClicked(postId, count + 1);
                        break;
                    case "unClicked" : mCallback.onThumbsUpClicked(postId, count - 1);
                }
            }
        };

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        new MySharedPreferences(activity).getUserKey();
        menu.add(Menu.NONE, R.id.remove_post, Menu.NONE, "Delete Post");
    }



    private String clickedThumbsUp() {

        String tag = button_thumbsUp.getTag().toString();
        int color1 = ContextCompat.getColor(activity.getApplicationContext(),R.color.button_color);
        int color2 = ContextCompat.getColor(activity.getApplicationContext(),R.color.button_color_clicked);

        switch (tag) {
            case "clicked" :
                drawable_thumbsUp.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                label_thumbsUp.setTextColor(color1);
                button_thumbsUp.setTag("unClicked");
                return "unClicked";
            case "unClicked" :
                drawable_thumbsUp.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                label_thumbsUp.setTextColor(color2);
                button_thumbsUp.setTag("clicked");
                return "clicked";
        }
        return "unClicked";
    }
}
















