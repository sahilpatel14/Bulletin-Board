package in.sahilpatel.bulletinboard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelableNewThread;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelablePost;
import in.sahilpatel.bulletinboard.services.NotificationListenerService;
import in.sahilpatel.bulletinboard.services.PostsDownloadService;
import in.sahilpatel.bulletinboard.services.ThreadDownloadService;
import in.sahilpatel.bulletinboard.services.UserDownloadService;
import in.sahilpatel.bulletinboard.support.HelpTextDisplay;
import pl.tajchert.sample.DotsTextView;

public class LauncherActivity extends AppCompatActivity implements HelpTextDisplay.HelpTextCallback{

    private final String TAG = LauncherActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private BroadcastReceiver postsReceiver,threadsReceiver,userReceiver;

    private TextView help_text;
    HelpTextDisplay helpTextDisplay;

    private Animation in;
    private Animation out;

    private ArrayList<ParcelableNewThread> threads;
    private ArrayList<ParcelablePost> posts;
    private User self;

    public static final int REQUEST_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //startActivity(new Intent(this,NotificationActivity.class));

        setContentView(R.layout.layout_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        helpTextDisplay = new HelpTextDisplay(this);

        help_text = (TextView)findViewById(R.id.field_help_text);

        View parentLayout = findViewById(R.id.splash_screen);

        final DotsTextView dots = (DotsTextView) findViewById(R.id.dots);
        dots.start();

        if (!isNetworkAvailable()) {
            startSnackBar(parentLayout, "Internet connection is required to use this application.");
        } else {
            launchChecks();
        }

        in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);

        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);

        broadcastReceivers();
    }


    private void broadcastReceivers() {

        threadsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    int resultCode = bundle.getInt(ThreadDownloadService.RESULT);
                    if (resultCode == RESULT_OK) {

                        Log.d(TAG, "onReceive: " + "posts downloaded");
                        ArrayList<ParcelableNewThread> parcelableNewThreads = bundle.getParcelableArrayList(ThreadDownloadService.PARCELABLE_THREADS);
                        LauncherActivity.this.threads = parcelableNewThreads;
                        String[] thread_ids = new String[parcelableNewThreads.size()];
                        stopService(new Intent(LauncherActivity.this,ThreadDownloadService.class));
                        int i = 0;
                        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {
                            NewThread thread = parcelableNewThread.getThread();
                            thread_ids[i] = thread.getThread_id();
                            i++;
                        }
                        startPostsDownload(thread_ids);
                    }
                }
            }
        };

        postsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    int resultCode = bundle.getInt(PostsDownloadService.RESULT);
                    if (resultCode == RESULT_OK) {

                        posts = new ArrayList<>();
                        Log.d(TAG, "onReceive: " + "posts downloaded");
                        ArrayList<ParcelablePost> parcelablePosts = bundle.getParcelableArrayList(PostsDownloadService.PARCELABLE_POSTS);

                        for (ParcelablePost parcelablePost : parcelablePosts) {
                            posts.add(parcelablePost);
                        }
                        Log.d(TAG, "onReceive: " + parcelablePosts.size());
                        String user_key = new MySharedPreferences(LauncherActivity.this).getUserKey();
                        startNotificationListenerService(user_key);
                        helpTextDisplay.stop();
                        openHomeActivity();
                        finish();
                    }
                }
            }
        };

        userReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    int resultCode = bundle.getInt(PostsDownloadService.RESULT);
                    if (resultCode == RESULT_OK) {

                        Log.d(TAG, "onReceive: " + "posts downloaded");
                        ArrayList<User> parcelableUsers = bundle.getParcelableArrayList(UserDownloadService.PARCELABLE_USERS);

                        if (parcelableUsers.size()!= 0){
                            self = parcelableUsers.get(0);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(threadsReceiver, new IntentFilter(ThreadDownloadService.NOTIFICATION));
        registerReceiver(postsReceiver, new IntentFilter(PostsDownloadService.NOTIFICATION));
        registerReceiver(userReceiver, new IntentFilter(UserDownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(threadsReceiver);
        unregisterReceiver(postsReceiver);
        unregisterReceiver(userReceiver);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN && resultCode == RESULT_OK) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            launchChecks();
        }
    }

    @Override
    public void nextHelpText(int index) {

        switch (index) {
            case 0 :
                displayHelpText(R.string.help_text_1);
                break;
            case 1 :
                displayHelpText(R.string.help_text_2);
                break;
            case 2 :
                displayHelpText(R.string.help_text_3);
                break;
            case 3 :
                displayHelpText(R.string.help_text_4);
                break;
            case 4 :
                displayHelpText(R.string.help_text_5);
        }
    }

    private void displayHelpText(final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                help_text.startAnimation(out);

                out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        help_text.setText(getResources().getString(id));
                        help_text.startAnimation(in);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

//                help_text.startAnimation(in);
            }
        });
    }

    private void launchChecks() {


        helpTextDisplay.start();

        if (mFirebaseUser == null) {
            //  User not logged in goto SignInActivity
            Log.d(TAG, "onCreate: " + " user not logged in");
            startActivityForResult(new Intent(this, SignInActivity.class), REQUEST_SIGN_IN);
        } else {
            //  User has already logged in. Check if uid exists
            Log.d(TAG, "onCreate: " + " user logged in");

            //startPostsDownload();

            String user_key = new MySharedPreferences(LauncherActivity.this).getUserKey();
            startThreadsDownload(user_key);
            startUserDownload(user_key);
        }
    }

    private void openHomeActivity() {

        Intent intent = new Intent(this,HomeActivity.class);
        intent.putParcelableArrayListExtra(HomeActivity.POSTS,posts);
        intent.putParcelableArrayListExtra(HomeActivity.THREADS,threads);
        intent.putExtra(HomeActivity.USER,self);

        startActivity(intent);
    }

    private void startSnackBar(View rootView, String message) {

        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void startUserDownload(String user_id) {
        Intent intent = new Intent(this, UserDownloadService.class);
        intent.putExtra(UserDownloadService.USER_ID, user_id);
        intent.putExtra(UserDownloadService.REQUEST_TYPE, UserDownloadService.REQUEST_TYPE_DOWNLOAD_SELF);
        startService(intent);
        Log.d(TAG, "startUserDownload: " + " User download service started.");
    }

    private void startThreadsDownload(String user_id) {
        Intent intent = new Intent(this, ThreadDownloadService.class);
        intent.putExtra(ThreadDownloadService.USER_ID, user_id);
        intent.putExtra(ThreadDownloadService.REQUEST_TYPE, ThreadDownloadService.DOWNLOAD_THREAD_METADATA);
        startService(intent);
        Log.d(TAG, "startThreadsDownload: " + " Thread download service started.");


    }
    private void startPostsDownload(String[] thread_ids) {

        Intent intent = new Intent(this, PostsDownloadService.class);
        intent.putExtra(PostsDownloadService.THREAD_IDS, thread_ids);
        startService(intent);
        Log.d(TAG, "startPostsDownload: " + "Service started.");
    }

    private void startNotificationListenerService(String user_key) {

        Intent intent = new Intent(this, NotificationListenerService.class);
        intent.putExtra(NotificationListenerService.USER_ID,user_key);
        startService(intent);
        Log.d(TAG, "startNotificationListenerService: "+"service started");
    }
}





















