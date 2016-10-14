package in.sahilpatel.bulletinboard.adapters.posts.holders;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class Type3PostHolder extends PostHolder {

    private ImageView post_image_container;
    private TextView post_image_description;
    private Activity activity;
    private OnPostClickedListener listener;

    public Type3PostHolder(View itemView, Activity activity, OnPostClickedListener listener) {
        super(itemView, activity, listener);

        this.listener = listener;
        this.activity = activity;
        post_image_container = (ImageView)itemView.findViewById(R.id.post_image);
        post_image_description = (TextView)itemView.findViewById(R.id.post_image_description);
    }


    @Override
    public void bindExtra(final Post post) {

        List<String> list = post.getImages();
        Glide.with(activity).load(baseUrl+list.get(0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sample_photo)
                .dontAnimate()
                .into(post_image_container);

        post_image_description.setText(post.getContent());
        post_image_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(0,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_ONE);
            }
        });
    }
}


























