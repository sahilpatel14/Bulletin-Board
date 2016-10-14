package in.sahilpatel.bulletinboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.model.Comment;
import in.sahilpatel.bulletinboard.support.TimestampConversion;

/**
 * Created by Administrator on 10/3/2016.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>{


    private List<Comment> comments;
    private Context context;

    public CommentsAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout,parent,false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {

        Comment comment = comments.get(position);
        holder.field_user_name.setText(comment.getUser_name());
        holder.field_content.setText(comment.getContent());
        holder.field_timestamp.setText(TimestampConversion.getReadableTimestamp(comment.getTimestamp()));

        Glide.with(context).load(comment.getPic_url())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.container_user_image);
    }

    final class CommentHolder extends RecyclerView.ViewHolder {

        private CircleImageView container_user_image;
        private TextView field_user_name;
        private TextView field_content;
        private TextView field_timestamp;

        public CommentHolder(View itemView) {
            super(itemView);

            container_user_image = (CircleImageView)itemView.findViewById(R.id.comment_user_image);
            field_user_name = (TextView)itemView.findViewById(R.id.comment_user_name);
            field_content = (TextView)itemView.findViewById(R.id.comment_content);
            field_timestamp = (TextView)itemView.findViewById(R.id.comment_timestamp);
        }


    }
}





















