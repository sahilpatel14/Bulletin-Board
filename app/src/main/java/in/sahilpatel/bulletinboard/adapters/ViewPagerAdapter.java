package in.sahilpatel.bulletinboard.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.sahilpatel.bulletinboard.fragments.EmptyThreadFragment;
import in.sahilpatel.bulletinboard.fragments.ThreadFragment;
import in.sahilpatel.bulletinboard.listeners.OnSharePostListener;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    private static final String TAG = "ViewPagerAdapter";
    List<NewThread> thread_data = new ArrayList<>();
    Map<String, List<Post>> posts = new HashMap<>();
    List<String> tabTitles = new ArrayList<>();
    private User user;
    OnSharePostListener mListener;


    public ViewPagerAdapter(FragmentManager fm, OnSharePostListener listener) {
        super(fm);
        mListener = listener;
    }

    public void setThreads(List<NewThread> thread_data) {
        this.thread_data = thread_data;
    }

    public void setTabTitles(List<String> tab_title) {
        this.tabTitles = tab_title;
    }

    public void setPosts(Map<String, List<Post>> posts) { this.posts = posts; }

    public void setUser(User user) { this.user = user; }

    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "getItem: "+"calling fragment at position "+position);
        NewThread thread = thread_data.get(position);
        return ThreadFragment.newInstance(thread, posts.get(thread.getThread_id()),user ,mListener);
    }

    @Override
    public int getCount() {
        return thread_data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }


}
