package in.sahilpatel.bulletinboard.adapters.posts.holders;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class
Type1PostHolder extends PostHolder {

    public TextView field_share_thoughts;


    public Type1PostHolder(View postView, Activity activity) {
        super(postView, activity);

        field_share_thoughts = (TextView)postView.findViewById(R.id.field_thoughts);
    }

    public Type1PostHolder(View postView, Activity activity, OnPostClickedListener mCallback) {
        super(postView, activity, mCallback);

        field_share_thoughts = (TextView)postView.findViewById(R.id.field_thoughts);
    }

    @Override
    public void bindExtra(Post post) {
        field_share_thoughts.setText(post.getHeading());
    }
}
