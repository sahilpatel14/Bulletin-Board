package in.sahilpatel.bulletinboard.adapters.posts.holders;

import android.app.Activity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.blogc.android.views.ExpandableTextView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class Type2PostHolder extends PostHolder {

    private TextView field_post_heading;
    private TextView field_post_subHeading;
    private ExpandableTextView field_post_content;
    private LinearLayout button_read_more;
    private TextView label_read_more;

    public Type2PostHolder(View itemView, Activity activity, OnPostClickedListener listener) {
        super(itemView, activity, listener);

        field_post_heading = (TextView)itemView.findViewById(R.id.post_heading);
        field_post_subHeading = (TextView)itemView.findViewById(R.id.post_subHeading);
        field_post_content = (ExpandableTextView)itemView.findViewById(R.id.post_content);
        button_read_more = (LinearLayout)itemView.findViewById(R.id.button_read_more);
        label_read_more = (TextView)itemView.findViewById(R.id.label_read_more);
    }


    @Override
    public void bindExtra(Post post) {

        field_post_heading.setText(post.getHeading());
        field_post_subHeading.setText(post.getSubHeading());
        field_post_content.setText(post.getContent());

        button_read_more.setOnClickListener(readMoreClickListener());
    }


    private View.OnClickListener readMoreClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                field_post_content.setInterpolator(new OvershootInterpolator());
                field_post_content.toggle();
                if(field_post_content.isExpanded()) {
                    label_read_more.setText(R.string.label_collapse);
                }
                else {
                    label_read_more.setText(R.string.label_read_more);
                }
            }
        };
    }
}





















