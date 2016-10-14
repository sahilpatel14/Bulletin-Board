package in.sahilpatel.bulletinboard.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.adapters.posts.holders.PostHolder;
import in.sahilpatel.bulletinboard.adapters.posts.holders.Type1PostHolder;
import in.sahilpatel.bulletinboard.adapters.posts.holders.Type4PostHolder;
import in.sahilpatel.bulletinboard.adapters.posts.holders.Type5PostHolder;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 9/28/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostHolder> {

    private Activity activity;
    private List<Post> posts;
    private OnPostClickedListener mCallback;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PostsAdapter(Activity activity, OnPostClickedListener mCallback, List<Post> posts) {
        this.activity = activity;
        this.mCallback = mCallback;
        this.posts = posts;
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View postView = null;

        switch (viewType) {
            case Post.POST_TYPE_SHARE_THOUGHTS :
                postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_type_1,parent,false);
                return new Type1PostHolder(postView,activity, mCallback);
            case Post.POST_TYPE_SHARE_IMAGES :
                postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_type_4,parent,false);
                return  new Type4PostHolder(postView,activity, mCallback);

            default:
                postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_type_5,parent,false);
                return new Type5PostHolder(postView,activity, mCallback);
        }

    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {

        Post post = posts.get(position);
        holder.bindData(post);
        holder.bindExtra(post);
    }


    @Override
    public int getItemViewType(int position) {

        String postType = posts.get(position).getPostType();
        int type = Integer.parseInt(postType);


        return type;
    }
}























