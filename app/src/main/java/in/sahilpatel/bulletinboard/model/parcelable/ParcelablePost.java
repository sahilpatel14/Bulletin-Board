package in.sahilpatel.bulletinboard.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.sahilpatel.bulletinboard.model.Post;

/**
 * Created by Administrator on 10/6/2016.
 */

public class ParcelablePost implements Parcelable {

    private String postType;
    private String postId;
    private Map<String,String> owner;
    private String picUrl;
    private String thumbsUp;
    private String comments;
    private String timestamp;
    private String threadId;

    private String heading;
    private String subHeading;
    private String content;
    private List<String> images;


    public ParcelablePost(Post post) {

        postType = post.getPostType();
        postId = post.getPostId();
        owner = post.getOwner();
        picUrl = post.getPicUrl();
        thumbsUp = post.getThumbsUp();
        comments = post.getComments();
        timestamp = post.getTimestamp();
        threadId = post.getThreadId();
        heading = post.getHeading();
        subHeading = post.getSubHeading();
        content = post.getContent();
        images = post.getImages();
    }

    protected ParcelablePost(Parcel parcel) {

        postType = parcel.readString();
        postId = parcel.readString();

        owner = new HashMap<>();
        owner = parcel.readHashMap(null);

        picUrl = parcel.readString();
        thumbsUp = parcel.readString();
        comments = parcel.readString();
        timestamp = parcel.readString();
        threadId = parcel.readString();

        heading = parcel.readString();
        subHeading = parcel.readString();
        content = parcel.readString();
        images = new ArrayList<>();
        parcel.readList(images,null);
    }

    public Post getPost() {
        Post post = new Post();
        post.setPostType(postType);
        post.setPostId(postId);
        post.setOwner(owner);
        post.setPicUrl(picUrl);
        post.setThumbsUp(thumbsUp);
        post.setComments(comments);
        post.setTimestamp(timestamp);
        post.setThreadId(threadId);

        post.setHeading(heading);
        post.setSubHeading(subHeading);
        post.setContent(content);
        post.setImages(images);

        return post;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(postType);
        dest.writeString(postId);
        dest.writeMap(owner);
        dest.writeString(picUrl);
        dest.writeString(thumbsUp);
        dest.writeString(comments);
        dest.writeString(timestamp);
        dest.writeString(threadId);
        dest.writeString(heading);
        dest.writeString(subHeading);
        dest.writeString(content);
        dest.writeList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ParcelablePost> CREATOR
            = new Parcelable.Creator<ParcelablePost>(){

        @Override
        public ParcelablePost createFromParcel(Parcel parcel) {
            return new ParcelablePost(parcel);
        }

        @Override
        public ParcelablePost[] newArray(int size) {
            return new ParcelablePost[size];
        }
    };
}
