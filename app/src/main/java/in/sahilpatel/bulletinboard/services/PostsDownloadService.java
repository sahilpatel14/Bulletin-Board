package in.sahilpatel.bulletinboard.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.sahilpatel.bulletinboard.firebase.FirebasePostApi;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelableNewThread;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelablePost;
import in.sahilpatel.bulletinboard.support.SandboxManager;


public class PostsDownloadService extends IntentService {

    public int result = Activity.RESULT_OK;
    public static final String THREAD_IDS= "THREAD_IDS";
    public static final String NOTIFICATION = "service receiver";
    private static final String TAG = "PostsDownloadService";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String PARCELABLE_POSTS = "PARCELABLE_POSTS";
    public static final String SERVICE_NAME = PostsDownloadService.class.getSimpleName();
    public static final String RESULT = "RESULT";

    public PostsDownloadService() {
        super("Post Downloader");
    }
    ArrayList<ParcelablePost> parcelablePosts;
    Map<String, Boolean > checks = new HashMap<>();

    @Override
    protected void onHandleIntent(Intent intent) {



        final String[] thread_ids = intent.getStringArrayExtra(THREAD_IDS);

//        final SandboxManager manager =
//                new SandboxManager.Builder(getApplicationContext()).directory("posts").build();

        parcelablePosts = new ArrayList<>();

        for (String thread_id : thread_ids) {
            checks.put(thread_id,false);
        }

        if (thread_ids.length == 0) {
            result = Activity.RESULT_OK;
            publishResults(result);
        }

        for(final String thread_id : thread_ids) {
            new FirebasePostApi().getPosts(thread_id, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot == null || !dataSnapshot.exists()){
                        Log.d(TAG, "onDataChange: "+"no posts.");
                        checks.put(thread_id,true);
                    }

                    List<Post> posts = new ArrayList<>();
                    for(DataSnapshot shot : dataSnapshot.getChildren()) {
                        Post post = shot.getValue(Post.class);
                        post.setPostId(shot.getKey());
                        posts.add(post);
                        ParcelablePost parcelablePost = new ParcelablePost(post);
                        parcelablePosts.add(parcelablePost);
                    }
//                    String filename = "/posts_"+thread_id+".json";
//                    Log.d(TAG, "onDataChange: saving posts to "+filename);
//                    manager.savePOJOS(filename,posts);
                    result = Activity.RESULT_OK;

                    if (allPostsDownloaded(thread_ids)) {
                        publishResults(result);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    result = Activity.RESULT_CANCELED;
                    publishResults(result);
                }
            });
        }

    }

    private boolean allPostsDownloaded(String [] thread_ids) {

        for (String thread_id : thread_ids) {
            for (ParcelablePost parcelablePost : parcelablePosts ){
                Post post = parcelablePost.getPost();
                if (post.getThreadId().equals(thread_id)){
                    checks.put(thread_id,true);
                    break;
                }
            }
        }

        for(String thread_id : thread_ids) {
            if (checks.get(thread_id) == false)
                return false;
        }
        return true;
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT,result);
        intent.putExtra(PARCELABLE_POSTS,parcelablePosts);
        intent.putExtra(SERVICE_TYPE,SERVICE_NAME);
        sendBroadcast(intent);
        this.stopSelf();
    }
}
