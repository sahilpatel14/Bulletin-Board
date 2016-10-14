package in.sahilpatel.bulletinboard.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable{

    public static final String LAST_ACTIVITY_STATUS_SEEN = "1";
    public static final String LAST_ACTIVITY_STATUS_UNSEEN = "0";

    private String profile_image_url;
    private String name;
    private String uid;
    private String provider;
    private Map<String,String> last_activity;
    private Map<String,String> owned_threads;
    private Map<String,String> moderator_threads;
    private Map<String,String> threads;


    public User() {}

    public User(String name,String profile_image_url, String uid,
                Map<String,String> last_activity, String provider,
                Map<String, String> owned_threads, Map<String, String> threads) {
        this.last_activity = last_activity;
        this.name = name;
        this.owned_threads = owned_threads;
        this.profile_image_url = profile_image_url;
        this.provider = provider;
        this.threads = threads;
        this.uid = uid;
    }

    /**
     * Called when a new user is created.
     * @param name
     * @param profile_image_url
     * @param uid
     * @param provider
     */
    public User(String name, String profile_image_url, String uid, String provider) {
        this.name = name;
        this.profile_image_url = profile_image_url;
        this.provider = provider;
        this.uid = uid;

        Map<String,String> activity = new HashMap<>();
        activity.put("timestamp",System.currentTimeMillis()+"");
        activity.put("message","Signed into the app.");
        activity.put("status",LAST_ACTIVITY_STATUS_SEEN);
        this.last_activity = activity;
    }

    public User(Parcel parcel) {

        profile_image_url = parcel.readString();
        name = parcel.readString();
        uid = parcel.readString();
        provider = parcel.readString();
        last_activity = new HashMap<>();
        last_activity = parcel.readHashMap(null);

        owned_threads = new HashMap<>();
        owned_threads = parcel.readHashMap(null);

        moderator_threads = new HashMap<>();
        moderator_threads = parcel.readHashMap(null);

        threads = new HashMap<>();
        threads = parcel.readHashMap(null);
    }

    public Map<String, String> getModerator_threads() {
        return moderator_threads;
    }

    public void setModerator_threads(Map<String, String> moderator_threads) {
        this.moderator_threads = moderator_threads;
    }

    public Map<String, String> getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(Map<String, String> last_activity) {
        this.last_activity = last_activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getOwned_threads() {
        return owned_threads;
    }

    public void setOwned_threads(Map<String, String> owned_threads) {
        this.owned_threads = owned_threads;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public Map<String, String> getThreads() {
        return threads;
    }

    public void setThreads(Map<String, String> threads) {
        this.threads = threads;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User)obj;
        return this.uid.equals(user.getUid());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(profile_image_url);
        parcel.writeString(name);
        parcel.writeString(uid);
        parcel.writeString(provider);
        parcel.writeMap(last_activity);
        parcel.writeMap(owned_threads);
        parcel.writeMap(moderator_threads);
        parcel.writeMap(threads);
    }

    final static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){

        @Override
        public User createFromParcel(Parcel parcel) {

            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}


























