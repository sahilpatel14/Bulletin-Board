package in.sahilpatel.bulletinboardlayouts.posts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.sahilpatel.bulletinboardlayouts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareImageFragment extends Fragment {


    public ShareImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share_image, container, false);
    }

}
