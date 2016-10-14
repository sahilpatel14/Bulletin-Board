package in.sahilpatel.android.libraries.customgallery.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import in.sahilpatel.android.libraries.customgallery.R;

/**
 * Created by Administrator on 10/3/2016.
 */

public class SlideFragment extends Fragment {

    private String TAG  = SlideFragment.class.getSimpleName();
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_DATE = "ARG_DATE";
    public static final String ARG_COUNT = "ARG_COUNT";
    public static final String ARG_TOTAL_IMAGES = "TOTAL_IMAGES";
    public static final String ARG_URL = "ARG_URL";

    private TextView lblCount, lblTitle, lblDate;
    private ImageView imageView;
    private int selectedPosition = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_slide,container,false);


        lblCount = (TextView)rootView.findViewById(R.id.lbl_count);
        lblTitle = (TextView)rootView.findViewById(R.id.lbl_title);
        lblDate =(TextView)rootView.findViewById(R.id.lbl_date);
        imageView = (ImageView)rootView.findViewById(R.id.image_preview);

        Bundle args = getArguments();

        int count = args.getInt(ARG_COUNT);
        int total = args.getInt(ARG_TOTAL_IMAGES);
        String title = args.getString(ARG_TITLE);
        String date = args.getString(ARG_DATE);
        String url = args.getString(ARG_URL);

        lblCount.setText(count+"/"+total);
        lblTitle.setText(title);
        lblDate.setText(date);


        Glide.with(getContext())
                .load(url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        return rootView;
    }
}
