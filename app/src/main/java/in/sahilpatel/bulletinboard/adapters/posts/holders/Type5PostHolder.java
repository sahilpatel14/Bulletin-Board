package in.sahilpatel.bulletinboard.adapters.posts.holders;

import android.app.Activity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class Type5PostHolder extends PostHolder {

    private TextView field_post_heading;
    private TextView field_post_subHeading;
    private ExpandableTextView field_post_content;
    private LinearLayout button_read_more;
    private TextView label_read_more;

    private ImageView post_image;
    private RelativeLayout post_image_container;
    private Activity activity;
    private OnPostClickedListener listener;


    public Type5PostHolder(View itemView, Activity activity, OnPostClickedListener listener) {
        super(itemView, activity, listener);

        this.activity = activity;
        this.listener = listener;

        field_post_heading = (TextView)itemView.findViewById(R.id.post_heading);
        field_post_subHeading = (TextView)itemView.findViewById(R.id.post_subHeading);
        field_post_content = (ExpandableTextView)itemView.findViewById(R.id.post_content);

        post_image = (ImageView)itemView.findViewById(R.id.post_image);

        button_read_more = (LinearLayout)itemView.findViewById(R.id.button_read_more);
        label_read_more = (TextView)itemView.findViewById(R.id.label_read_more);
        post_image_container = (RelativeLayout)itemView.findViewById(R.id.container_post_image);


    }

    @Override
    public void bindExtra(final Post post) {

        manageViews(post);

        field_post_heading.setText(post.getHeading());
        field_post_subHeading.setText(post.getSubHeading());
        field_post_content.setText(post.getContent());

        List<String> list = post.getImages();


        if (list != null && list.size() > 0) {
            Glide.with(activity).load(baseUrl+list.get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.sample_photo)
                    .dontAnimate()
                    .into(post_image);
        }

        button_read_more.setOnClickListener(readMoreClickListener());
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(0,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_ONE);
            }
        });
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

    private void manageViews(Post post) {

        if (post.getSubHeading() == null || post.getSubHeading().isEmpty()) {
            field_post_subHeading.setVisibility(View.GONE);
        }

        if (post.getContent() == null || post.getContent().isEmpty()) {
            field_post_content.setVisibility(View.GONE);
        }

        if (post.getImages() == null) {
            post_image_container.setVisibility(View.GONE);
            return;
        }

        if(post.getImages().isEmpty()){
            post_image_container.setVisibility(View.GONE);
        }
    }
}





















