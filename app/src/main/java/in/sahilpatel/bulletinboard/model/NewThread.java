package in.sahilpatel.bulletinboard.model;

import android.app.ProgressDialog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewThread {

    private String thread_id;
    private String thread_name;
    private String image_url;
    private Map<String,String> last_activity;
    private String description;
    private Map<String,String> membersList;
    private Map<String,String> ownerList;
    private Map<String,String> moderatorList;
    private Policy policy;

    public NewThread() {}

    public NewThread(String thread_name,
                     Map<String, String> last_activity, String image_url, String description,
                     Policy policy,
                     Map<String,String> ownerList, Map<String,String> moderatorList, Map<String,String> membersList) {
        this.thread_name = thread_name;
        this.policy = policy;
        this.ownerList = ownerList;
        this.moderatorList = moderatorList;
        this.membersList = membersList;
        this.last_activity = last_activity;
        this.image_url = image_url;
        this.description = description;
    }

    public NewThread(String thread_id,String thread_name) {
        this.thread_id = thread_id;
        this.thread_name = thread_name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Map<String, String> getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(Map<String, String> last_activity) {
        this.last_activity = last_activity;
    }

    public Map<String, String> getMembersList() {
        return membersList;
    }

    public void setMembersList(Map<String, String> membersList) {
        this.membersList = membersList;
    }

    public Map<String, String> getModeratorList() {
        return moderatorList;
    }

    public void setModeratorList(Map<String, String> moderatorList) {
        this.moderatorList = moderatorList;
    }

    public Map<String, String> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(Map<String, String> ownerList) {
        this.ownerList = ownerList;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getThread_name() {
        return thread_name;
    }

    public void setThread_name(String thread_name) {
        this.thread_name = thread_name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Policy{

        public static final String TYPE_MODERATORS = "MODERATORS";
        public static final String TYPE_EVERYONE = "EVERYONE";
        public static final String TYPE_OWNERS = "OWNER";
        public static final String TYPE_OWNER_AND_MODERATORS = "OWNER+MODERATORS";

        private String add_members;
        private String add_post;
        private String edit_post;

        public Policy() {
        }

        public Policy(String add_members, String add_post, String edit_post) {
            this.add_members = add_members;
            this.add_post = add_post;
            this.edit_post = edit_post;
        }

        public String getAdd_members() {
            return add_members;
        }

        public void setAdd_members(String add_members) {
            this.add_members = add_members;
        }

        public String getEdit_post() {
            return edit_post;
        }

        public void setEdit_post(String edit_post) {
            this.edit_post = edit_post;
        }

        public String getAdd_post() {
            return add_post;
        }

        public void setAdd_post(String add_post) {
            this.add_post = add_post;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Member {

        private String id;
        private String name;
        private String profile_pic_url;

        public Member(){}

        public Member(String name) {
            this.name = name;
        }

        public Member(String id, String name, String profile_pic_url) {
            this.id = id;
            this.name = name;
            this.profile_pic_url = profile_pic_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfile_pic_url() {
            return profile_pic_url;
        }

        public void setProfile_pic_url(String profile_pic_url) {
            this.profile_pic_url = profile_pic_url;
        }
    }
}
