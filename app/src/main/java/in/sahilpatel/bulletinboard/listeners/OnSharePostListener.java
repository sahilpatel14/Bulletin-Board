package in.sahilpatel.bulletinboard.listeners;

import android.view.View;

import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 10/6/2016.
 */

public interface OnSharePostListener {

    void onPostSelected(Post post, View view);
    void onPostUnSelected(Post post);
}
