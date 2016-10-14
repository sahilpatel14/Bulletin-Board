package in.sahilpatel.bulletinboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 9/14/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

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


    public static final int POST_TYPE_SHARE_THOUGHTS = 1;
    public static final int POST_TYPE_SHARE_IMAGES = 2;
    public static final int POST_TYPE_SHARE_ARTICLE = 3;
    public static final int POST_TYPE_HEADING_CONTENT = 4;
    public static final int POST_TYPE_HEADING_SUBHEADING_CONTENT = 5;

    public Post() {}

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Map<String, String> getOwner() {
        return owner;
    }

    public void setOwner(Map<String, String> owner) {
        this.owner = owner;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {

        Post post = (Post)obj;
        return postId.equals(post.getPostId());
    }


}
