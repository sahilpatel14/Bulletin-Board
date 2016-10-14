package in.sahilpatel.bulletinboard.backup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String profile_image_url;
    private String name;
    private String uid;
    private String provider;
    private Map<String,String> last_activity;
    private Map<String,String> owned_threads;
    private Map<String,String> moderator_threads;
    private Map<String,String> threads;


    public User() {}

    public User(String name, String profile_image_url, String uid,
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
        this.last_activity = activity;
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
}
