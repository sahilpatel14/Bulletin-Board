package in.sahilpatel.bulletinboard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import in.sahilpatel.bulletinboard.adapters.ViewPagerAdapter;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.fragments.AboutFragment;
import in.sahilpatel.bulletinboard.fragments.AddPostFragment;
import in.sahilpatel.bulletinboard.fragments.InviteFragment;
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
    public static final String USER = "USER";

    private final String TAG = HomeActivity.class.getSimpleName();

    /**
     * To show user name, user image and most recent
     * activity on the Drawer.
     */
    private CircleImageView field_avatar;
    private TextView field_user_name;
    private TextView field_recent_activity;

    /**
     * View pager, its adapter and the Tab layout
     * that shows Thread names
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;

    /**
     * Flag to tell if the user has selected a post or not
     * to be shared elsewhere.
     */
    private boolean sharePostFlag;

    /**
     * Stores data for the app. Threads, users etc. Also the
     * data of logged in user is stored separately. Posts are
     * saved in a Map with thread id as key.
     */
    private List<NewThread> threads;
    private ArrayList<User> users;
    private List<String> tabTitles;
    private Map<String, List<Post>> posts;
    private User self;

    /**
     * NavigationView, menu and drawer.
     */
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Menu navigation_menu;

    /**
     * Receives thread and user data when it's finished downloading from
     * the service.
     */
    private BroadcastReceiver threadsReceiver, usersReceiver;

    /**
     * Windows that can be opened from this activity, only the fragments.
     */
    private SharePostFragment sharePostFragment;
    private ThreadInformationFragment threadInformationFragment;
    private AddPostFragment addPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setupAppData();             //   initializes the Lists with data.


        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        sharePostFlag = false;
        sharePostFragment = new SharePostFragment();
        threadInformationFragment = new ThreadInformationFragment();
        addPostFragment = new AddPostFragment();


        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_person_black_36dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, myToolbar,
                R.string.app_name, R.string.app_name
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        View headerView = mNavigationView.inflateHeaderView(R.layout.main_navigation_header);
        field_avatar = (CircleImageView) headerView.findViewById(R.id.avatar);
        field_user_name = (TextView) headerView.findViewById(R.id.userName);
        field_recent_activity = (TextView) headerView.findViewById(R.id.field_last_activity);

        /**
         * Init for ViewPager
         */
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        tabTitles = new ArrayList<>();
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);


        initNavigationView();
        initThreads();
        startThreadsDownload();
        startAllUsersDownload();
        initializeReceivers();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPostWindow();
            }
        });
    }


    /**
     * Intent for this activity contains parcelable data that we can use to
     * perform other operations. This data is passed by the Launcher Activity which
     * is the only activity that calls this activity.
     *
     */
    private void setupAppData() {

        threads = new ArrayList<>();
        posts = new HashMap<>();
        users = new ArrayList<>();
        self = new User();
        Intent intent = getIntent();

        /**
         * No data present.
         */
        if (intent == null) {
            finish();
        }

        List<ParcelableNewThread> parcelableNewThreads = intent.getParcelableArrayListExtra(THREADS);
        List<ParcelablePost> parcelablePosts = intent.getParcelableArrayListExtra(POSTS);
        self = intent.getParcelableExtra(USER);
        /**
         * Retrieving threads out of parcelableThread wrapper.
         */
        List<Post> allPosts = new ArrayList<>();
        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {
            threads.add(parcelableNewThread.getThread());
        }

        /**
         * Retrieving posts out of parcelablePost wrapper.
         */
        for (ParcelablePost parcelablePost : parcelablePosts) {
            allPosts.add(parcelablePost.getPost());
        }

        /**
         * Here we store every post in a map where key is the thread it.
         */
        for (NewThread thread : threads) {
            String thread_id = thread.getThread_id();
            List<Post> threadPost = getPostsFor(allPosts, thread_id);
            posts.put(thread.getThread_id(), threadPost);
        }
    }

    /**
     * These receivers return full thread data object for all the threads
     * that the user is linked to along with a list of all the users that
     * have signed up to the app.
     */
    private void initializeReceivers() {

        threadsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int resultCode = bundle.getInt(ThreadDownloadService.RESULT);
                    if (resultCode == RESULT_OK) {
                        threads = new ArrayList<>();
                        ArrayList<ParcelableNewThread> parcelableNewThreads =
                                bundle.getParcelableArrayList(ThreadDownloadService.PARCELABLE_THREADS);
                        for (ParcelableNewThread parcelableNewThread : parcelableNewThreads) {
                            NewThread newThread = parcelableNewThread.getThread();
                            threads.add(newThread);
                        }
                        /**
                         * Notify ViewPager about the new objects, it's not required though.
                         */
                        mViewPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

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
                    /**
                     * Now we have full data about current user, display it on NavigationDrawer.
                     */
                    showLastActivity();
                }
            }
        };
    }


    /**
     * sets up the NavigationView Menu. When a particular button is clicked, an action is taken.
     * This is done by this method.
     */
    private void initNavigationView() {

        this.navigation_menu = mNavigationView.getMenu();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.label_logout:
                        logoutUser();
                        break;
                    case R.id.label_create_thread:
                        openCreateThreadWindow();
                        break;
                    case R.id.label_about_this_app :
                        openAboutFragmentWindow();
                        break;
                    case R.id.label_send_invite :
                        openInviteFragmentWindow();
                        break;
                    default:
                        openViewPagerWindow(menuItem.getTitle()+"");
                }
                return true;
            }
        });
    }


    /**
     * sets up the View pager. Initializes it
     * with threads and tab titles.
     * Also adds extra menu items to drawer for every owned thread.
     */
    private void initThreads() {

        MenuItem menuItem = navigation_menu.findItem(R.id.manage_threads);
        SubMenu subMenu = menuItem.getSubMenu();
        MenuItem item;

        int i = 0;
        for (NewThread t : threads) {
            tabTitles.add(t.getThread_name());
            item = subMenu.add(t.getThread_id());
            item.setTitle(t.getThread_name());
            item.setIcon(R.drawable.ic_group_black_24dp);
            i++;
        }

        mViewPagerAdapter.notifyDataSetChanged();
        mViewPagerAdapter.setThreads(threads);
        mViewPagerAdapter.setTabTitles(tabTitles);
        mViewPagerAdapter.setPosts(posts);
        mViewPagerAdapter.setUser(self);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    /**
     * registering all the broadcast receivers here. Will be called
     * when the activity starts.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(threadsReceiver, new IntentFilter(ThreadDownloadService.NOTIFICATION));
        registerReceiver(usersReceiver, new IntentFilter(UserDownloadService.NOTIFICATION));
    }


    /**
     * deregister all broadcast receivers here.
     * More efficient this way.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(threadsReceiver);
        unregisterReceiver(usersReceiver);
    }

    /**
     * Inflating out own toolbar menu here. menu contains the information
     * and share button.
     * @param menu, received by the call.
     * @return, always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Called when the user clicks on one particular option item. The particular item of
     * clicked MenuItem is checked with the id's on our toolbar, specific action is then
     * taken.
     * @param item, that is clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /**
             *  Open the thread info window for the current thread on screen.
             */
            case R.id.button_thread_information :   this.openThreadInfoWindow();
                return true;

            /**
             *  Open the share post window.
             *  We are also changing background colour here. This allows user to choose
             *  a post that he/she wants to share.
             */
            case R.id.button_share_post:
                if (!sharePostFlag) {
                    findViewById(R.id.view_pager).setBackgroundColor(
                            ResourcesCompat.getColor(
                                    getResources(),
                                    R.color.view_pager_background_selected_color,
                                    null));
                    Toast.makeText(HomeActivity.this, "Select the post to share.", Toast.LENGTH_SHORT).show();
                    sharePostFlag = true;
                }
                else {
                    findViewById(R.id.view_pager).setBackgroundColor(
                            ResourcesCompat.getColor(getResources(),
                                    android.R.color.white,
                                    null));
                    sharePostFlag = false;
                }
                return true;
            /**
             * If any other id, let the super method handle it.
             */
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Called when user clicks on a particular post.
     * This listener is used for sharing post functionality.
     * @param post, the post where the user clicked.
     * @param view, associated with the post.
     */
    @Override
    public void onPostSelected(Post post, View view) {

        sharePostFragment.setPost(post);
        sharePostFragment.setView(view);
        openSharePostWindow();
    }

    /**
     * Called when the user clicks on a selected post. Not
     * being used for now.
     * @param post, that was clicked.
     */
    @Override
    public void onPostUnSelected(Post post) {

    }

    /**
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * Helper methods. I don't want to be bugged with these methods.
     * They do their job right. That's it
     */


    /**
     * Returns a list of All posts for a particular thread.
     * @param allPosts, List containing all the posts, the sample space
     * @param thread_id, the id of the thread.
     * @return  list of posts for that thread.
     */
    private List<Post> getPostsFor(List<Post> allPosts, String thread_id) {

        List<Post> threadPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getThreadId().equals(thread_id)) {
                threadPosts.add(post);
            }
        }
        return threadPosts;
    }


    /**
     * Shows the Last Activity that is saved on the user pojo for the
     * current user. the message is shown in two places.
     * 1. on a SnackBar for a small duration.
     * 2. on Drawer, right below the user image.
     */
    private void showLastActivity() {


        String user_id = new MySharedPreferences(this).getUserKey();
        for (User user : users) {
            if (user.getUid().equals(user_id)) {
                self = user;
            }
        }
        mViewPagerAdapter.setUser(self);


        new FirebaseUserApi().getCurrentUser(user_id, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                HomeActivity.this.self = user;

                Map<String, String> threads = user.getThreads();

                List<String> thread_ids = new ArrayList<>(threads.keySet());

                boolean flag = false;
                for (String thread_id : thread_ids) {
                    NewThread newThread = new NewThread(thread_id,threads.get(thread_id));
                    if (!(HomeActivity.this.threads.contains(newThread))){
                        flag = true;
                        HomeActivity.this.threads.add(newThread);
                    }
                }
                if (flag == true) {
                    startThreadsDownload();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        String message = self.getLast_activity().get("message");
        String timeStamp = self.getLast_activity().get("timestamp");
        String status = self.getLast_activity().get("status");

        if (status.equals(User.LAST_ACTIVITY_STATUS_UNSEEN)) {
            Snackbar.make(mViewPager, message + "\n - " + TimestampConversion.getReadableTimestamp(timeStamp), Snackbar.LENGTH_LONG).show();
            new FirebaseUserApi().updateLastUserActivityStatus(self.getUid(),User.LAST_ACTIVITY_STATUS_SEEN);
        }
        field_recent_activity.setText(message + " - " + TimestampConversion.getReadableTimestamp(timeStamp));

        field_user_name.setText(self.getName());
        Glide.with(HomeActivity.this).load(self.getProfile_image_url()).into(field_avatar);
    }

    /**
     * Logs out the user. deletes that data associated with login
     * from sharedPreferences file and returns the user back to
     * Launcher activity.
     */
    public void logoutUser() {
        new MySharedPreferences(this).deleteSavedData();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LauncherActivity.class));
    }


    /**
     *  Called when the user clicks on shared post window. If a post has not
     *  been selected by the user, does nothing
     *  else loads up sharePostDialog.
     */
    private void openSharePostWindow() {

        if (threads == null ||threads.size() == 0){
            Toast.makeText(this, "No threads here.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sharePostFlag) {
            sharePostFragment.show(getSupportFragmentManager(), "Share Post");
        }
    }

    /**
     * Opens the fragmentDialog that adds new post to the thread
     */
    private void openAddPostWindow() {

        int pos = mViewPager.getCurrentItem();

        if (threads == null ||threads.size() == 0){
            Toast.makeText(this, "No threads here.", Toast.LENGTH_SHORT).show();
            return;
        }

        addPostFragment.setThread(threads.get(pos));
        addPostFragment.setUser(self);
        addPostFragment.show(getSupportFragmentManager(), "Add Post");

    }

    /**
     * Opens up the Thread Information window. The window requires full Thread object
     * and a list containing all the users of the app.
     * We need to pass these two before calling the show() on ThreadInformationFragment.
     */
    private void openThreadInfoWindow() {

        threadInformationFragment = new ThreadInformationFragment();
        int pos = mViewPager.getCurrentItem();
        Log.d(TAG, "openThreadInfoWindow: "+pos);

        if (threads == null ||threads.size() == 0){
            Toast.makeText(this, "No threads here.", Toast.LENGTH_SHORT).show();
            return;
        }

        NewThread thread = threads.get(pos);
        threadInformationFragment.setThread(thread);
        threadInformationFragment.setAllUsers(users);
        threadInformationFragment.show(getSupportFragmentManager(), "Thread information");

    }

    /**
     * Opens the createNewThreadActivity. It is a separate activity.
     * Does not require any other data for now.
     */
    private void openCreateThreadWindow() {

        Intent intent = new Intent(this, NewThreadActivity.class);
        intent.putExtra(NewThreadActivity.USER,self);
        intent.putParcelableArrayListExtra(NewThreadActivity.ALL_USERS,users);
        startActivity(intent);
    }

    /**
     * sets the viewPager to the thread selected by user.
     * @param name, name of the thread.
     */
    private void openViewPagerWindow(String name) {

        int i = 0;
        for (NewThread thread : threads){
            if (thread.getThread_name().equals(name)){
                break;
            }
            i++;
        }
        mViewPager.setCurrentItem(i);
    }

    /**
     * Opens a dialog fragment from where user can Invite friends.
     */
    private void openInviteFragmentWindow(){
        InviteFragment fragment = new InviteFragment();
        fragment.show(getSupportFragmentManager(), "invite friends window");
    }

    /**
     * A small description about the app. version etc.
     */
    private void openAboutFragmentWindow() {
        AboutFragment fragment = new AboutFragment();
        fragment.show(getSupportFragmentManager(), "About the app window");
    }

    /**
     * ++++++++++++++++++++++++++++++++ SERVICES ++++++++++++++++++++++++++++++++++++++++++++++++
     *
     */

    /**
     * Starts a service to download all the users present in the app. This would help
     * in making the app run faster as all the data is already available.
     */
    private void startThreadsDownload() {

        /**
         * Fetching thread id's of all the associated threads.
         */
        String thread_ids[] = new String[threads.size()];
        int i = 0;
        for (NewThread thread : threads) {
            thread_ids[i] = thread.getThread_id();
            i++;
        }

        Intent intent = new Intent(this, ThreadDownloadService.class);
        intent.putExtra(ThreadDownloadService.THREAD_IDS, thread_ids);
        intent.putExtra(ThreadDownloadService.REQUEST_TYPE, ThreadDownloadService.DOWNLOAD_THREAD_ALL);
        startService(intent);
        Log.d(TAG, "startThreadsDownload: " + " Thread download service started.");
    }

    /**
     * Fetching all the users signed up in the app.
     */
    private void startAllUsersDownload() {
        Intent intent = new Intent(this, UserDownloadService.class);
        intent.putExtra(UserDownloadService.REQUEST_TYPE, UserDownloadService.REQUEST_TYPE_DOWNLOAD_ALL);
        startService(intent);
        Log.d(TAG, "startUserDownload: " + "User download service started");
    }

}


























