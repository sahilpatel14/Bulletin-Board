package adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import in.sahilpatel.android.libraries.customgallery.fragments.SlideFragment;
import in.sahilpatel.android.libraries.customgallery.fragments.SlideShowFragment;
import model.Image;

/**
 * Created by Administrator on 10/3/2016.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Image> images;

    public ViewPagerAdapter(FragmentManager fm, List<Image> images) {
        super(fm);
        this.images = images;
    }

    @Override
    public Fragment getItem(int position) {

        SlideFragment fragment = new SlideFragment();

        Bundle bundle = new Bundle();
        Image image = images.get(position);
        bundle.putInt(SlideFragment.ARG_COUNT,position+1);
        bundle.putInt(SlideFragment.ARG_TOTAL_IMAGES,images.size());
        bundle.putString(SlideFragment.ARG_DATE,image.getTimestamp());
        bundle.putString(SlideFragment.ARG_TITLE,image.getName());
        bundle.putSerializable(SlideFragment.ARG_URL,image.getLarge());

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {

        return images.size();
    }
}
