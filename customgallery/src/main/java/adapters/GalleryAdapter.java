package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import in.sahilpatel.android.libraries.customgallery.R;
import model.Image;

/**
 * Created by Administrator on 10/3/2016.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>{

    private List<Image> images;             //  Stores all the images to be displayed
    private Context context;                //  Who created the adapter

    public GalleryAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }



    @Override
    public int getItemCount() {
        return images.size();
    }



    /**
     *
     * Creates a new View and returns it as our ViewHolder.
     * Inflates a view inside the parent RecyclerView using the layout
     * of the item and its parent.
     *
     * Once created, it returns that view wrapped inside our ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    /**
     * The method is passed the viewHolder where we want to display the data
     * and index of data that we need to display.
     *
     * Get the image to be displayed.
     * display it in ViewHolder's imageView.
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Image image = images.get(position);
        Glide.with(context).load(image.getMedium())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
    }

    /**
     * ViewHolder class, created for every view that is created in onCreateViewHolder
     * We initialize the View where we intent to put image and that's it.
     */

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
        }
    }


    /**
     * This interface would make catching clicks and long clicks pretty
     * easy. Our recycleView.OnClickListener would call these inteface method in case of
     * an event
     * and then we can manage ClickEvents by just implementing this interface..
     */
    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
