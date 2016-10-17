package in.sahilpatel.bulletinboard.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.activities.LauncherActivity;
import in.sahilpatel.bulletinboard.activities.NewThreadActivity;
import in.sahilpatel.bulletinboard.adapters.ViewPagerAdapter;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.firebase.OnUserDataObtained;
import in.sahilpatel.bulletinboard.fragments.AddPostFragment;
import in.sahilpatel.bulletinboard.fragments.SharePostFragment;
import in.sahilpatel.bulletinboard.fragments.ThreadInformationFragment;
import in.sahilpatel.bulletinboard.listeners.OnSharePostListener;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelableNewThread;
import in.sahilpatel.bulletinboard.model.parcelable.ParcelablePost;
import in.sahilpatel.bulletinboard.services.ThreadDownloadService;
import in.sahilpatel.bulletinboard.services.UserDownloadService;
import in.sahilpatel.bulletinboard.support.TimestampConversion;

/**
 * This activity will only be called once the user has successfully authenticated
 * with firebase. Further checks for authentication are not necessary. If mFirebaseUser
 * object still returns null, check LauncherActivity and OldSignInActivity.
 */

public class HomeActivity extends AppCompatActivity implements OnSharePostListener {

    public static final String POSTS = "POSTS";
    public static final String THREADS = "THREADS";
    private final String TAG = HomeActivity.class.getSimpleName();


    private FirebaseUserApi mFirebaseUserApi;
    private CircleImageView field_avatar;
    private TextView field_user_name;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;

    private boolean sharePostFlag;

    private List<NewThread> threads;
    private List<User> users;
    private List<String> tabTitles;

    private Map<String, List<Post>> posts;
    private User self;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    private BroadcastReceiver threadsReceiver, usersReceiver;

    private SharePostFragment sharePostFragment;
    private Menu navigation_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        List<ParcelableNewThread> parcelableNewThreads = intent.getParcelableArrayListExtra(THREADS);
        List<ParcelablePost> parcelablePosts = intent.getParcelableArrayListExtra(POSTS);

        threads = new ArrayList<>();
        posts = new HashMap<>();
        users = new ArrayList<>();
        self = new User();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        sharePostFlag = false;
        sharePostFragment = new SharePostFragment();


        mFirebaseUserApi = new FirebaseUserApi();
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, myToolbar,
                R.string.app_name, R.string.app_name
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_person_black_36dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View headerView = mNavigationView.inflateHeaderView(R.layout.main_navigation_header);

        field_avatar = (CircleImageView) headerView.findViewById(R.id.avatar);
        field_user_name = (TextView) headerView.findViewById(R.id.userName);

        /**
         * Init for ViewPager
         */
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        List<Post> allPosts = new ArrayList<>();
        tabTitles = new ArrayList<>();
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {
            threads.add(parcelableNewThread.getThread());
        }

        for (ParcelablePost parcelablePost : parcelablePosts) {
            allPosts.add(parcelablePost.getPost());
        }

        for (NewThread thread : threads) {
            String thread_id = thread.getThread_id();
            List<Post> threadPost = getPostsFor(allPosts, thread_id);
            posts.put(thread.getThread_id(), threadPost);
        }

        initNavigationView();
        initDrawer();
        initThreads();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = mViewPager.getCurrentItem();
                String name = threads.get(pos).getThread_name();
                Log.d(TAG, "onClick: " + name);
                AddPostFragment fragment = new AddPostFragment();
                fragment.setThread(threads.get(pos));
                fragment.show(getSupportFragmentManager(), "Add Post");
            }
        });

        String[] thread_ids = new String[parcelableNewThreads.size()];
        int i = 0;
        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {

            NewThread thread = parcelableNewThread.getThread();
            thread_ids[i] = thread.getThread_id();
            i++;
        }


        startThreadsDownload(thread_ids);
        threadsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();

                if (bundle != null) {

                    int resultCode = bundle.getInt(ThreadDownloadService.RESULT);

                    if (resultCode == RESULT_OK) {
                        threads = new ArrayList<>();
                        ArrayList<ParcelableNewThread> parcelableNewThreads = bundle.getParcelableArrayList(ThreadDownloadService.PARCELABLE_THREADS);
                        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {
                            NewThread newThread = parcelableNewThread.getThread();
                            threads.add(newThread);
                        }
                        mViewPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        startAllUsersDownload();
        usersReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    int resultCode = bundle.getInt(UserDownloadService.RESULT);

                    if (resultCode == RESULT_OK) {
                        users = new ArrayList<>();
                        ArrayList<User> parcelableUsers = bundle.getParcelableArrayList(UserDownloadService.PARCELABLE_USERS);
                        for (User u : parcelableUsers) {
                            users.add(u);
                        }
                    }

                    showLastActivity();
                }
            }
        };
    }


    private void init() {

    }

    private void initializeReceivers() {

    }

    private List<Post> getPostsFor(List<Post> allPosts, String thread_id) {

        List<Post> threadPosts = new ArrayList<>();
        for (Post post : allPosts) {

            if (post.getThreadId().equals(thread_id)) {
                threadPosts.add(post);
            }
        }

        return threadPosts;
    }

    private void initNavigationView() {

        this.navigation_menu = mNavigationView.getMenu();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Snackbar.make(mDrawerLayout, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.label_logout:
                        logoutUser();
                        break;
                    case R.id.label_create_thread:
                        openCreateThreadWindow();
                }
                return true;
            }
        });
    }

    private void initDrawer() {


        String user_key = new MySharedPreferences(this).getUserKey();
        mFirebaseUserApi.getCurrentUser(user_key, new OnUserDataObtained() {
            @Override
            public void onSuccess(User user) {
                HomeActivity.this.field_user_name.setText(user.getName());
                Glide.with(HomeActivity.this).load(user.getProfile_image_url()).into(field_avatar);
            }

            @Override
            public void onFailure(String message, FirebaseError error) {
                Log.d(TAG, "onFailure: " + message + " Firebase says " + error.getMessage());
            }
        });

        mFirebaseUserApi.getCurrentUser(user_key, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                HomeActivity.this.self = user;

                Map<String, String> threads = user.getThreads();

                List<String> thread_ids = new ArrayList<>(threads.keySet());

                for (String thread_id : thread_ids) {
                    NewThread newThread = new NewThread(thread_id,threads.get(thread_id));
                    if (!(HomeActivity.this.threads.contains(newThread))){
                        HomeActivity.this.threads.add(newThread);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void initThreads() {

        initializeFragmentsAndTitles();
        mViewPagerAdapter.setThreads(threads);
        mViewPagerAdapter.setTabTitles(tabTitles);
        mViewPagerAdapter.setPosts(posts);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void showLastActivity() {

        String user_id = new MySharedPreferences(this).getUserKey();

        for (User user : users) {
            if (user.getUid().equals(user_id)) {
                self = user;
            }
        }

        String message = self.getLast_activity().get("message");
        String timeStamp = self.getLast_activity().get("timestamp");


        Snackbar.make(mViewPager, message + "\n - " + TimestampConversion.getReadableTimestamp(timeStamp), Snackbar.LENGTH_LONG)
                .show();

        ((TextView) findViewById(R.id.field_last_activity)).setText(message + "\n-" + TimestampConversion.getReadableTimestamp(timeStamp));

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(threadsReceiver, new IntentFilter(ThreadDownloadService.NOTIFICATION));
        registerReceiver(usersReceiver, new IntentFilter(UserDownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(threadsReceiver);
        unregisterReceiver(usersReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_thread_information:
                this.openThreadInfoWindow();
                return true;
            case R.id.button_share_post:


                if (sharePostFlag == false) {
                    findViewById(R.id.view_pager).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.view_pager_background_selected_color, null));
                    Toast.makeText(HomeActivity.this, "Select the post to share.", Toast.LENGTH_SHORT).show();
                    sharePostFlag = true;
                } else {
                    findViewById(R.id.view_pager).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                    sharePostFlag = false;
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeFragmentsAndTitles() {


//        threads = new MySharedPreferences(this).getThreadNames();

        MenuItem menuItem = navigation_menu.findItem(R.id.manage_threads);
        SubMenu subMenu = menuItem.getSubMenu();
        MenuItem item;
        for (NewThread t : threads) {
            tabTitles.add(t.getThread_name());
            item = subMenu.add(t.getThread_id());
            item.setTitle(t.getThread_name());
            item.setIcon(R.drawable.ic_group_black_24dp);
        }


        mViewPagerAdapter.notifyDataSetChanged();
    }

    public void logoutUser() {
        new MySharedPreferences(this).deleteSavedData();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LauncherActivity.class));
    }

    private void openCreateThreadWindow() {

//        NewThreadFragment newThreadFragment = new NewThreadFragment();
//        newThreadFragment.show(getSupportFragmentManager(),"Add new thread");

        Intent intent = new Intent(this, NewThreadActivity.class);
        startActivity(intent);
    }

    private void openSharePostWindow() {

        if (sharePostFlag == false) {

        } else {
            sharePostFragment.show(getSupportFragmentManager(), "Share Post");
        }


//        findViewById(R.id.post_footer).setBackgroundColor(
//                ResourcesCompat.getColor(getResources(),R.color.view_pager_background_selected_color,null)
//        );
        //sharePostFragment.show(getSupportFragmentManager(), "Share Post");
    }

    private void openThreadInfoWindow() {

        ThreadInformationFragment fragment = new ThreadInformationFragment();
        int pos = mViewPager.getCurrentItem();
        NewThread thread = threads.get(pos);
        fragment.setThread(thread);
        fragment.setAllUsers(users);
        fragment.show(getSupportFragmentManager(), "Thread information");

    }

    private void startThreadsDownload(String thread_ids[]) {
        Intent intent = new Intent(this, ThreadDownloadService.class);
        intent.putExtra(ThreadDownloadService.THREAD_IDS, thread_ids);
        intent.putExtra(ThreadDownloadService.REQUEST_TYPE, ThreadDownloadService.DOWNLOAD_THREAD_ALL);
        startService(intent);
        Log.d(TAG, "startThreadsDownload: " + " Thread download service started.");
    }

    private void startAllUsersDownload() {
        Intent intent = new Intent(this, UserDownloadService.class);
        intent.putExtra(UserDownloadService.REQUEST_TYPE, UserDownloadService.REQUEST_TYPE_DOWNLOAD_ALL);
        startService(intent);
        Log.d(TAG, "startUserDownload: " + "User download service started");
    }

    @Override
    public void onPostSelected(Post post, View view) {

        sharePostFragment.setPost(post);
        sharePostFragment.setView(view);
        openSharePostWindow();
    }

    @Override
    public void onPostUnSelected(Post post) {

    }
}


























