package in.sahilpatel.android.libraries.customgallery.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import adapters.ViewPagerAdapter;
import in.sahilpatel.android.libraries.customgallery.R;
import model.Image;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlideShowFragment extends DialogFragment {


    public static final String ARG_POSITION = "ARG_POSITION";
    private String TAG  = SlideShowFragment.class.getSimpleName();
    private int selectedPosition;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<Image> images;

    public SlideShowFragment() {
        // Required empty public constructor
    }

    /* Initialize image list before calling the show method */
    public void setImage(List<Image> images) {
        this.images = images;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_slide_show, container, false);

        selectedPosition = getArguments().getInt(ARG_POSITION);
        viewPager = (ViewPager)rootView.findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(getChildFragmentManager(),images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPosition);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().finish();
    }
}
