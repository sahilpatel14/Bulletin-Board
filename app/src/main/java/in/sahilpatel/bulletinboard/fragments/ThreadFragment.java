package in.sahilpatel.bulletinboard.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapters.GalleryAdapter;
import helper.RecyclerTouchListener;
import in.sahilpatel.android.libraries.customgallery.fragments.CustomGalleryActivity;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.adapters.PostsAdapter;
import in.sahilpatel.bulletinboard.adapters.posts.holders.PostHolder;
import in.sahilpatel.bulletinboard.firebase.FirebasePostApi;
import in.sahilpatel.bulletinboard.listeners.OnPostClickedListener;
import in.sahilpatel.bulletinboard.listeners.OnSharePostListener;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThreadFragment extends Fragment implements OnPostClickedListener{

    /**
     * This class represents every individual thread in the system.
     * The model for threads is provided to this class and it shows that
     * data using recyclerView.
     *
     * The class objects are only created by ViewPagerAdapter. Therefore the
     * getInstance method is used to create these methods.
     *
     * All the callbacks for each and every post is forwarded by the adapter
     * to this class. All calls to serverApi for post is only done through this class.
     */

    private static final String TAG = "ThreadFragment";
    private List<Post> posts;                       //  All posts for one particular thread
    private NewThread thread;                       //  the model thread, this class is showcasing
    private User user;                              //  logged in user
    private int clickedPosition = 0;                //  determines which post was clicked by the user.

    private boolean loading = true;                 //  loading new data
    int pastVisibleItems, visibleItemCount,
            totalItemCount;                         //  checks if user has scrolled to the end of recyclerView.

    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private LinearLayoutManager layoutManager;
    private OnSharePostListener mListener;

    public ThreadFragment() {}
    public void setThread(NewThread thread) {
        this.thread = thread;
    }

    /**
     * Called by the ViewPagerAdapter to create new instances of this class.
     * Must pass the following objects to create this new thread.
     * @param thread, from where model will be received for this class.
     * @param posts,  all the posts for the thread.
     * @param user  the user who is requesting,
     * @param listener when user wants to share a post on other platforms
     * @return ThreadFragment object
     */
    public static ThreadFragment newInstance(NewThread thread,
                                             List<Post> posts,
                                             User user,
                                             OnSharePostListener listener) {
        ThreadFragment f = new ThreadFragment();
        f.thread = thread;
        f.mListener = listener;
        f.posts = posts;
        f.user = user;
        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_thread, container, false);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView)rootView.findViewById(R.id.post_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        postsAdapter = new PostsAdapter(getActivity(),this,posts);
        recyclerView.setAdapter(postsAdapter);

        setListeners();

        Collections.sort(posts, comparator);
        postsAdapter.notifyDataSetChanged();

        return rootView;
    }

    private void setListeners() {

        /**
         * triggered when the user selects one particular post.
         * It captures both click and long click. A global clickedPosition
         * integer variable is set the position value.
         * In case a normal click, we send the clicked view and post to sharePostListener.
         */
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        clickedPosition = position;
                        mListener.onPostSelected(posts.get(position), view);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        clickedPosition = position;
                    }
                }));


        /**
         * Called when the user reaches end of recyclerView while scrolling the list.
         * the call is only made once, we check that using the loading boolean.
         * When the user reaches the end of list, we start downloading the remaining
         * posts from the server.
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                        {
                            loading = false;
                            Toast.makeText(getContext(), "Loading more posts...", Toast.LENGTH_SHORT).show();

                            /**
                             * Network call to download additional posts.
                             */
                            new FirebasePostApi().getAllPosts(thread.getThread_id(), new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for(DataSnapshot shot : dataSnapshot.getChildren()) {
                                        Post post = shot.getValue(Post.class);
                                        post.setPostId(shot.getKey());

                                        /**
                                         * Only add posts if they are not already
                                         * present in the list.
                                         */
                                        if (!posts.contains(post)){
                                            posts.add(post);
                                        }
                                    }
                                    /**
                                     * Sorting the fetched posts based on their timestamp.
                                     */
                                    Collections.sort(posts, comparator);
                                    postsAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {}
                            });
                        }
                    }
                }
            }
        });


        /**
         * This listener tracks addition of new posts into the list. Also, any
         * change to the posts will be tracked by this listener.
         */
        new FirebasePostApi().getPosts(thread.getThread_id(), new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostId(dataSnapshot.getKey());

                /**
                 * Add post if it is not already present in the
                 * list
                 */
                if (!posts.contains(post)) {
                    posts.add(post);
                    postsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostId(dataSnapshot.getKey());

                /**
                 * Find the changed post in the list and replace it
                 * with the new post.
                 */
                for (Post p : posts) {
                    if (post.equals(p)){
                        int i = posts.indexOf(p);
                        posts.set(i,post);
                        postsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * A simple comparator object to sort posts based
     * on the timestamp.
     */
    private Comparator<Post> comparator = new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            Long t1 = Long.parseLong(post1.getTimestamp());
            Long t2 = Long.parseLong(post2.getTimestamp());

            return t2.compareTo(t1);
        }
    };

    /**
     * Registers the context menu.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(recyclerView);
    }

       @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.post_settings_menu, menu);
    }

    /**
     * Hide the post that was long pressed by the user.
     * TODO fix it, it has some major bug.
     * @param item, that was clicked
     * @return always true
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (clickedPosition >= posts.size())
            return false;

        Post post = posts.get(clickedPosition);
        switch (item.getItemId()) {
            case R.id.hide_post :
                Toast.makeText(getContext(), "hiding post "+post.getHeading(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onContextItemSelected: "+posts.get(clickedPosition).getHeading());
                Log.d(TAG, "onContextItemSelected: "+thread.getThread_id());
                //posts.remove(clickedPosition);

                postsAdapter.notifyDataSetChanged();
                return true;
            case R.id.remove_post :
                Toast.makeText(getContext(), "removing post "+post.getPostId(), Toast.LENGTH_SHORT).show();
                return true;

            default:
                return  super.onContextItemSelected(item);
        }
    }

    /**
     * Called when the user clicks on comments icon on the posts tile.
     * we pass the post, user and count value to the comments window.
     * @param postId, of the clicked post
     * @param count, no of comments made.
     */
    @Override
    public void onCommentsClicked(String postId, int count) {
        Toast.makeText(getContext(), "Clicked on comments", Toast.LENGTH_SHORT).show();

        CommentsFragment commentsFragment = new CommentsFragment();
        Post clickedPost = posts.get(0);
        for (Post post : posts) {
            if (post.getPostId().equals(postId)) {
                clickedPost = post;
            }
        }
        commentsFragment.setPost(clickedPost);
        commentsFragment.setUser(user);
        commentsFragment.setThread(thread);
        commentsFragment.show(getChildFragmentManager(),"Comments Fragment");
    }

    /**
     * Called by PostsHolder when thumbsUp icon is clicked.
     * we call the PostApi and increment the thumbsUp Counter.
     * @param postId, of the clicked psot
     * @param count, no. of thumbs up.
     */
    @Override
    public void onThumbsUpClicked(String postId, int count) {
        Toast.makeText(getContext(), "thumbs up", Toast.LENGTH_SHORT).show();
        new FirebasePostApi().thumbsUp(postId,count+"");
    }


    /**
     * Called when the user clicks on a Post having images. We check if he clicked
     * on last image or any other image. With that we know if we need to show
     * a grid of images or just the image part.
     * We call the CustomGalleryActivity with this data to show the images.
     * @param imageIndex, the image that was clicked
     * @param images, all the images in that post
     * @param clickType, Display single image or multiple images.
     */
    @Override
    public void onImageClicked(int imageIndex, List<String> images, int clickType) {

        String baseUrl = PostHolder.url;

        Log.d(TAG, "onImageClicked: "+imageIndex);
        Intent intent = new Intent(getActivity(),CustomGalleryActivity.class);
        ArrayList<String> list = (ArrayList<String>) images;

        switch (clickType){
            case IMAGE_CLICK_TYPE_ONE :
                intent.putExtra(CustomGalleryActivity.REQUEST_TYPE,CustomGalleryActivity.SINGLE_IMAGE_TO_DISPLAY);
                break;
            case IMAGE_CLICK_TYPE_MORE :
                intent.putExtra(CustomGalleryActivity.REQUEST_TYPE,CustomGalleryActivity.MULTIPLE_IMAGES_TO_DISPLAY);
        }

        intent.putStringArrayListExtra(CustomGalleryActivity.IMAGES,list);
        intent.putExtra(CustomGalleryActivity.IMAGE_INDEX,imageIndex);
        intent.putExtra(CustomGalleryActivity.BASE_URL,baseUrl);

        startActivity(intent);
    }
}
