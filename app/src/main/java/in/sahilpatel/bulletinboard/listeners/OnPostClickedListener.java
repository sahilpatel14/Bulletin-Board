package in.sahilpatel.bulletinboard.listeners;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/28/2016.
 */
public interface OnPostClickedListener {

    int IMAGE_CLICK_TYPE_MORE = 2;
    int IMAGE_CLICK_TYPE_ONE = 1;

    void onCommentsClicked(String postId, int oldCount);
    void onThumbsUpClicked(String postId, int oldCount);
    void onImageClicked(int imageIndex, List<String> images, int clickType);
}
