package in.sahilpatel.bulletinboard.fragments;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;



import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.fragments.posts.ShareArticleFragment;
import in.sahilpatel.bulletinboard.fragments.posts.ShareImageFragment;
import in.sahilpatel.bulletinboard.fragments.posts.ShareThoughtsFragment;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.User;


/**
 * This window opens a Dialog fragment which contains three different types
 * of child fragments. These three fragments represents three different type
 * of posts that can be posted. User can choose what kind of post he wants,
 * that particular fragment pops up. The layout for each fragment is unique to
 * the kind of post it provides. However, the cancel or post operation is triggered
 * by a button on this dialog fragment.
 */
public class AddPostFragment extends DialogFragment {

    /**
     * Thread is the name of thread(Group) that started this dialog fragment.
     * We need the thread id in order to send post to server.
     * We use a setter rather than constructor to set this thread because fragments
     * should always be called with their default constructors.
     */
    private static final String TAG = "AddPostFragment";
    private NewThread thread;
    private User user;

    public void setThread(NewThread thread) {
        this.thread = thread;
    }

    public void setUser(User user) { this.user = user;}

    /**
     * A Unique identifier for different types of PostFragments.
     * We will use it to send post request.
     */
    public static final String FRAGMENT_TYPE_SHARE_THOUGHTS = "SHARE_THOUGHTS";
    public static final String FRAGMENT_TYPE_SHARE_IMAGES = "SHARE_IMAGES";
    public static final String FRAGMENT_TYPE_SHARE_ARTICLE = "SHARE_ARTICLE";

    /**
     * Clicking these would load a particular fragment.
     * Same with TextViews. Both of them are combined to form a
     * Linear Layout which is then treated as a Button. Here we need
     * two to define them to change their colour when user clicks on them.
     */
    private ImageButton img_share_thoughts;
    private ImageButton img_post_image;
    private ImageButton img_post_article;

    private TextView label_share_thoughts;
    private TextView label_post_image;
    private TextView label_post_article;

    /**
     * Our fragments. They will be loaded  when user clicks a particular "button"
     */
    private ShareThoughtsFragment shareThoughtsFragment;
    private ShareImageFragment shareImageFragment;
    private ShareArticleFragment shareArticleFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.layout_add_post, container, false);

        init(rootView);
        setOnClickListeners(rootView);
        return rootView;
    }

    /**
     * Initializing fragments, and the buttons that would cause them to
     * load.
     * @param rootView
     */
    private void init(final View rootView) {

        img_share_thoughts = (ImageButton)rootView.findViewById(R.id.button_img_share_thoughts);
        img_post_image = (ImageButton)rootView.findViewById(R.id.button_img_post_image);
        img_post_article = (ImageButton)rootView.findViewById(R.id.button_img_post_article);

        label_share_thoughts = (TextView)rootView.findViewById(R.id.button_label_share_thoughts);
        label_post_image = (TextView)rootView.findViewById(R.id.button_label_post_image);
        label_post_article  = (TextView) rootView.findViewById(R.id.button_label_post_article);

        shareArticleFragment = new ShareArticleFragment();
        shareImageFragment = new ShareImageFragment();
        shareThoughtsFragment = new ShareThoughtsFragment();

        shareThoughtsFragment.setUser(user);
        shareImageFragment.setUser(user);
        shareArticleFragment.setUser(user);
    }

    /**
     * Here we set the Click Listeners that are required by the Window.
     * Some of them are just to give a particular design to window, Some are
     * present to trigger operation like Post and Cancel.
     * @param rootView
     */
    private void setOnClickListeners(final View rootView) {

        /**
         * Initially we are putting adding some fragment. Only once,
         * when the user opens the window.
         */
        final FrameLayout fragment_container = (FrameLayout) rootView.findViewById(R.id.add_post_fragment_container);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.add_post_fragment_container,shareThoughtsFragment);
        fragment_container.setTag(FRAGMENT_TYPE_SHARE_THOUGHTS);
        transaction.commit();


        /**
         * User clicked post article button
         */
        rootView.findViewById(R.id.button_post_article).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"inside click post_article");
                String tag = fragment_container.getTag().toString();
                    if (tag.equals(FRAGMENT_TYPE_SHARE_ARTICLE)){
                        Log.d(TAG, "onClick: "+"returning from shareThoughts");
                        return;
                    }
                    AddPostFragment.this.updatePostButtonState(FRAGMENT_TYPE_SHARE_ARTICLE,tag);
                    swapFragment(fragment_container,shareArticleFragment,FRAGMENT_TYPE_SHARE_ARTICLE);

            }
        });

        /**
         * User clicked post_image_button
         */
        rootView.findViewById(R.id.button_post_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"inside click post_image");
                String tag = fragment_container.getTag().toString();
                if(tag.equals(FRAGMENT_TYPE_SHARE_IMAGES)) {
                    Log.d(TAG, "onClick: "+"returning from post image");
                    return;
                }
                AddPostFragment.this.updatePostButtonState(FRAGMENT_TYPE_SHARE_IMAGES,tag);
                swapFragment(fragment_container,shareImageFragment,FRAGMENT_TYPE_SHARE_IMAGES);
            }
        });

        /**
         * User clicked share thoughts button
         */
        rootView.findViewById(R.id.button_share_thoughts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"inside click post_thoughts");
                String tag = fragment_container.getTag().toString();
                if(tag.equals(FRAGMENT_TYPE_SHARE_THOUGHTS)) {
                    Log.d(TAG, "onClick: "+"returning from shareThoughts");
                    return;
                }
                AddPostFragment.this.updatePostButtonState(FRAGMENT_TYPE_SHARE_THOUGHTS,tag);
                swapFragment(fragment_container,shareThoughtsFragment,FRAGMENT_TYPE_SHARE_THOUGHTS);
            }
        });


        /**
         * Cancel button. Close the dialog fragment
         */
        rootView.findViewById(R.id.button_add_post_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        /**
         * Post clicked, Firstly determine which fragment is currently
         * being displayed on the window. Then call the post method on
         * the object of that particular fragment. The request will then
         * be handled by that particular fragment.
         */
        rootView.findViewById(R.id.button_add_post_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = fragment_container.getTag().toString();

                switch(tag) {
                    case FRAGMENT_TYPE_SHARE_THOUGHTS :
                                        shareThoughtsFragment.post(thread); break;
                    case FRAGMENT_TYPE_SHARE_IMAGES :
                                        shareImageFragment.post(thread); break;
                    case FRAGMENT_TYPE_SHARE_ARTICLE :
                                        shareArticleFragment.post(thread);
                }
            }
        });
    }

    /**
     * This is a helper method that swaps two Fragments from the Frame Layout container.
     * It also changes the Tag based on swapped fragment.
     * @param fragment_container,
     * @param fragment
     * @param tag
     */
    private void swapFragment(final FrameLayout fragment_container,Fragment fragment,String tag) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.add_post_fragment_container,fragment);
        fragment_container.setTag(tag);
        transaction.commit();
    }


    /**
     * Again a helper method for design. This method is called when the user clicks
     * on a particular button representing type of post.
     * The method changes colour of icon and textView in the button to black
     * and colour in other two buttons to blue.
     * @param clickedFragmentTag
     * @param currentFragmentTag
     */
    private void updatePostButtonState(String clickedFragmentTag,String currentFragmentTag) {

        switch (clickedFragmentTag) {
            case FRAGMENT_TYPE_SHARE_THOUGHTS :
                img_share_thoughts.setImageResource(R.drawable.ic_person_pin_black_24dp);
                label_share_thoughts.setTypeface(null, Typeface.BOLD);
                break;

            case FRAGMENT_TYPE_SHARE_IMAGES :
                img_post_image.setImageResource(R.drawable.ic_photo_size_select_actual_black_24dp);
                label_post_image.setTypeface(null, Typeface.BOLD);
                break;

            case FRAGMENT_TYPE_SHARE_ARTICLE :
                img_post_article.setImageResource(R.drawable.ic_create_black_24dp);
                label_post_article.setTypeface(null, Typeface.BOLD);
                break;
        }

        switch (currentFragmentTag) {
            case FRAGMENT_TYPE_SHARE_THOUGHTS :
                img_share_thoughts.setImageResource(R.drawable.ic_person_pin_blue_24dp);
                label_share_thoughts.setTypeface(null, Typeface.NORMAL);
                break;

            case FRAGMENT_TYPE_SHARE_IMAGES :
                img_post_image.setImageResource(R.drawable.ic_photo_size_select_actual_blue_24dp);
                label_post_image.setTypeface(null, Typeface.NORMAL);
                break;

            case FRAGMENT_TYPE_SHARE_ARTICLE :
                img_post_article.setImageResource(R.drawable.ic_create_blue_24dp);
                label_post_article.setTypeface(null, Typeface.NORMAL);
                break;
        }

    }
}
