package in.sahilpatel.bulletinboard.adapters.posts.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class Type4PostHolder extends PostHolder {

    private Activity activity;
    private TextView field_image_description;
    private ImageView post_image_solo,post_image_2,post_image_3, post_image_4;
    private TextView label_more_images;
    private LinearLayout other_images_container;
    private RelativeLayout post_image_4_container;

    private OnPostClickedListener listener;

    public Type4PostHolder(View itemView, Activity activity, OnPostClickedListener listener) {
        super(itemView, activity, listener);

        this.activity = activity;
        this.listener = listener;

        field_image_description = (TextView)itemView.findViewById(R.id.post_image_description);

        post_image_solo = (ImageView)itemView.findViewById(R.id.post_image_solo);
        post_image_2 = (ImageView)itemView.findViewById(R.id.post_image_2);
        post_image_3 = (ImageView)itemView.findViewById(R.id.post_image_3);
        post_image_4 = (ImageView)itemView.findViewById(R.id.post_image_4);

        label_more_images = (TextView)itemView.findViewById(R.id.label_more_images);
        other_images_container = (LinearLayout)itemView.findViewById(R.id.other_images);
        post_image_4_container = (RelativeLayout)itemView.findViewById(R.id.post_image_4_container);
    }

    @Override
    public void bindExtra(final Post post) {

        List<String> images = post.getImages();
        field_image_description.setText(post.getContent());

        int image_count = images.size();

        switch (image_count) {
            case 0 :
                hideUnnecessaryImageViews(0);
                break;

            case 1 :
                hideUnnecessaryImageViews(1);
                displayImage(post_image_solo,images.get(0));
                break;

            case 2 :
                hideUnnecessaryImageViews(2);
                displayImage(post_image_2,images.get(0));
                displayImage(post_image_3,images.get(1));
                break;

            case 3 :
                hideUnnecessaryImageViews(3);
                displayImage(post_image_solo,images.get(0));
                displayImage(post_image_2,images.get(1));
                displayImage(post_image_3,images.get(2));
                break;

            default :
                hideUnnecessaryImageViews(image_count);
                displayImage(post_image_solo,images.get(0));
                displayImage(post_image_2,images.get(1));
                displayImage(post_image_3,images.get(2));
                displayImage(post_image_4, images.get(3));
        }

        post_image_solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(0,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_ONE);
            }
        });

        post_image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(1,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_ONE);
            }
        });

        post_image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(2,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_ONE);
            }
        });

        post_image_4_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(3,post.getImages(),OnPostClickedListener.IMAGE_CLICK_TYPE_MORE);

            }
        });
    }


    private void hideUnnecessaryImageViews(int count) {

        switch (count) {
            case 0:
                other_images_container.setVisibility(View.GONE);
                post_image_solo.setVisibility(View.GONE);
                break;

            case 1:
                other_images_container.setVisibility(View.GONE);
                break;

            case 2 :
                post_image_3.setVisibility(View.GONE);
                post_image_4_container.setVisibility(View.GONE);
                break;

            case 3 :
                label_more_images.setVisibility(View.VISIBLE);
                post_image_4_container.setVisibility(View.GONE);
                break;

            default:
                label_more_images.setVisibility(View.VISIBLE);
        }

    }

    private void displayImage(ImageView container, String image) {
        Glide.with(activity).load(baseUrl+image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sample_photo)
                .dontAnimate()
                .into(container);
    }
}
