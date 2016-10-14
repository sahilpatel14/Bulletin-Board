package in.sahilpatel.android.libraries.customgallery.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import adapters.GalleryAdapter;
import helper.RecyclerTouchListener;
import in.sahilpatel.android.libraries.customgallery.R;
import model.Image;

public class CustomGalleryActivity extends AppCompatActivity {

    private static final String TAG = "CustomGalleryActivity";
    private final int COLUMN_COUNT  = 2;
    private List<Image> images;
    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;
    private ProgressDialog pDialog;
    private String baseURL;


    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String IMAGES = "IMAGES";
    public static final String IMAGE_INDEX = "INDEX";
    public static final String BASE_URL = "BASE_URL";

    public static final int SINGLE_IMAGE_TO_DISPLAY = 1;
    public static final int MULTIPLE_IMAGES_TO_DISPLAY = 2;

    private SlideShowFragment slideShowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        images = new ArrayList<>();
        slideShowFragment = new SlideShowFragment();

        Intent intent = getIntent();

        int reqType = intent.getIntExtra(REQUEST_TYPE,0);
        List<String> image_list = intent.getStringArrayListExtra(IMAGES);
        baseURL = intent.getStringExtra(BASE_URL);
        int index = intent.getIntExtra(IMAGE_INDEX,0);

        fetchImages(image_list);

        switch (reqType) {
            case SINGLE_IMAGE_TO_DISPLAY :
                Bundle bundle = new Bundle();
                bundle.putInt(SlideShowFragment.ARG_POSITION,index);
                slideShowFragment.setImage(images);
                slideShowFragment.setArguments(bundle);
                slideShowFragment.show(getSupportFragmentManager(),"My Slide Show");
                break;
            case MULTIPLE_IMAGES_TO_DISPLAY :
                recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
                pDialog = new ProgressDialog(this);
                mAdapter = new GalleryAdapter(getApplicationContext(),images);

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),COLUMN_COUNT);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        /**
                         * Send the position of clicked image to DialogFragment
                         *
                         */
                        SlideShowFragment slideShow = new SlideShowFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(SlideShowFragment.ARG_POSITION, position);
                        slideShow.setImage(images);
                        slideShow.setArguments(bundle);
                        slideShow.show(getSupportFragmentManager(),"My Slide Show");
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        return;
                    }
                }));
        }
    }
    private void fetchImages(List<String> img_list) {

        Image img;
        StringBuffer sb;
        for (String image : img_list) {

            sb = new StringBuffer();
            img = new Image();
            sb.append(baseURL);
            sb.append(image);
            img.setLarge(sb.toString());
            img.setMedium(sb.toString());
            img.setSmall(sb.toString());
            img.setName("Image");
            img.setTimestamp("3/10/2016");

            images.add(img);
        }
    }


}
