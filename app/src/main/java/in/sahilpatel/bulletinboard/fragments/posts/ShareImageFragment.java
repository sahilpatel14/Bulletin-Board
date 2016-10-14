package in.sahilpatel.bulletinboard.fragments.posts;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import administrator.sahilpatel.com.imageuploader.FileUploadResult;
import administrator.sahilpatel.com.imageuploader.ImageUploader;
import administrator.sahilpatel.com.runtimepermissionvalidator.RuntimePermissionResult;
import administrator.sahilpatel.com.runtimepermissionvalidator.RuntimePermissionValidator;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.activities.CustomPhotoGalleryActivity;
import in.sahilpatel.bulletinboard.firebase.FirebasePostApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareImageFragment extends Fragment {

    private static final String TAG = "ShareImageFragment";
    private static final String IMAGE_UPLOAD_URL = "http://sahilpatel.in/BulletinBoard/upload.php";
    private final int PICK_IMAGE_MULTIPLE = 1;
    private User user;

    private ProgressDialog mProgressDialog;
    private EditText fieldImageDescription;
    private ArrayList<String> imagesPathList;
    private RecyclerView selectionRecyclerView;
    private RuntimePermissionValidator runtimePermissionValidator;
    private ImageAdapter imageAdapter;
    private ScrollView scrollView;

    public ShareImageFragment() {}


    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share_image, container, false);

        fieldImageDescription = (EditText)rootView.findViewById(R.id.field_add_image_description);
        mProgressDialog = new ProgressDialog(getContext());

        scrollView = (ScrollView)rootView.findViewById(R.id.scroll_view);

        imagesPathList = new ArrayList<>();
        imageAdapter = new ImageAdapter();
        runtimePermissionValidator = new RuntimePermissionValidator((AppCompatActivity) getActivity());

        selectionRecyclerView = (RecyclerView)rootView.findViewById(R.id.selection_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext().getApplicationContext(),3);
        selectionRecyclerView.setLayoutManager(layoutManager);
        selectionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        selectionRecyclerView.setAdapter(imageAdapter);

        /**
         * We don't want user to enter into the field manually.
         */
//        rootView.findViewById(R.id.field_selected_images).setOnKeyListener(null);

        rootView.findViewById(R.id.button_choose_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        fieldImageDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusView();
            }
        });

        return rootView;
    }

    private void chooseImageFromGallery() {
        /**
         * Asking for runtime permission. If not permitted, call this
         * method again from a callback.
         */
        if(!runtimePermissionValidator.mayRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                new RuntimePermissionResult() {
                    @Override
                    public void onPermissionGranted(String permission_type) {
                        //  permission granted, call this method again.
                        chooseImageFromGallery();
                    }

                    @Override
                    public void onPermissionDenied(String permission_type) {
                        //  do nothing for now. we can remove that particular
                        //  functionality.
                    }

                    @Override
                    public void onInternalError(String error) {
                        Log.d(TAG, "onInternalError: "+error);
                    }
                }
        )) {
            return;
        }

        Intent intent = new Intent(getContext(),CustomPhotoGalleryActivity.class);
        startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
    }


    /**
     * we are calling CustomPhotoGalleryActivity, the selected images will be returned
     * in this method. We will split the data returned and save it in a list. The image
     * Adapter will then use those paths to show selected images.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                String[] imagesPath = data.getStringExtra("data").split("\\|");

                for (String image:imagesPath) {
                    imagesPathList.add(image);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }
    }



    /**
     * Here we fetch data from fields, make necessary validations and pass the
     * data to create the post object out of it.
     * Called by parent Fragment
     * @param thread, thread that would get this post
     */
    public void post(NewThread thread) {

        String msg = fieldImageDescription.getText().toString();

        if(msg.isEmpty()) {
            Toast.makeText(getContext(), "Cannot leave the field blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imagesPathList.isEmpty()){
            Toast.makeText(getContext(), "Please select at least one image.", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.setMessage("Uploading images...");
        mProgressDialog.show();

        uploadPost(msg,thread.getThread_id());


        Map<String, String> membersMap = thread.getMembersList();
        Set<String> keys = membersMap.keySet();
        String[] members = keys.toArray(new String[keys.size()]);

        String message = user.getName()+" posted images on thread "+thread.getThread_name()+".";
        new FirebaseUserApi().updateLastUserActivity(user.getUid(),members,message);
    }

    /**
     * After all the necessary checks, we will call this method. Here we will start image upload,
     * make the post object and upload everything on the server.
     * @param msg, the descriptive message typed by the user.
     * @param thread_id, the id of thread.
     */
    private void uploadPost(String msg, String thread_id) {

        List<String> names = uploadImage(imagesPathList);
        final Post post = new Post();

        post.setContent(msg);
        post.setThreadId(thread_id);
        post.setComments("0");
        post.setThumbsUp("0");
        post.setTimestamp(System.currentTimeMillis()+"");
        post.setPostType(Post.POST_TYPE_SHARE_IMAGES+"");
        post.setImages(names);
        Map<String,String> owner = new HashMap<>();
        owner.put(user.getUid(),user.getName());
        post.setOwner(owner);
        post.setPicUrl(user.getProfile_image_url());
        new FirebasePostApi().addPost(post);

        Toast.makeText(getContext(), "Posted to thread", Toast.LENGTH_SHORT).show();
        fieldImageDescription.setText("");
        imagesPathList = new ArrayList<>();
        imageAdapter.notifyDataSetChanged();
    }


    /**
     * Uploads all the images present in the path list to the server. returns the names
     * of images in return. The upload process will keep executing in background.
     * @param paths, path to images
     * return, name of images assigned before uploading.
     */
    public List<String> uploadImage(List<String> paths) {

        ImageUploader imageUploader = new ImageUploader(IMAGE_UPLOAD_URL);
        List<String> names = new ArrayList<>();
        String extension;
        String fileName;

        /**
         * Separating image names from the full path. The images will be uploaded
         * with this name. Also, the same name will be returned.
         */
        for(String path : paths) {
            extension = path.substring(path.lastIndexOf("."));
            fileName = System.currentTimeMillis()+extension;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            names.add(fileName);
        }

        /**
         * Setting the images for upload. a long process. Will not block the UI thread.
         */
        imageUploader.uploadMultipleImages(paths, names, new FileUploadResult() {
            @Override
            public void onSuccess(JSONArray result) {
                Toast.makeText(getContext(), "Images uploaded successfully.", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "An error occurred while uploading the images.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+message);
                mProgressDialog.dismiss();
            }
        });
        return names;
    }

    private void focusView() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, fieldImageDescription.getTop());

//                int vLeft = view.getLeft();
//                int vRight = view.getRight();
//                int sWidth = scroll.getWidth();
//                scroll.smoothScrollTo(((vLeft + vRight - sWidth) / 2), 0);
            }
        });
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

        ImageAdapter() {}

        /**
         *
         * Creates a new View and returns it as our ViewHolder.
         * Inflates a view inside the parent RecyclerView using the layout
         * of the item and its parent.
         *
         * Once created, it returns that view wrapped inside our ViewHolder
         *
         */
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_selection_item,parent,false);
            return new MyViewHolder(itemView);
        }

        /**
         * The method is passed the viewHolder where we want to display the data
         * and index of data that we need to display.
         *
         * Get the image to be displayed.
         * display it in ViewHolder's imageView.
         * @param holder, our ViewHolder
         * @param position, of the data.
         */
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.imgThumb.setId(position);
            try {
                Bitmap yourBitmap = BitmapFactory.decodeFile(imagesPathList.get(position));
                int nh = (int) ( yourBitmap.getHeight() * (512.0 / yourBitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(yourBitmap, 512, nh, true);
                holder.imgThumb.setImageBitmap(scaled);

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imagesPathList.size();
        }

        /**
         * ViewHolder class, created for every view that is created in onCreateViewHolder
         * We initialize the View where we intent to put image and that's it.
         */

        class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView imgThumb;

            MyViewHolder(View view) {
                super(view);
                imgThumb = (ImageView)view.findViewById(R.id.imgThumb);
            }
        }
    }

}
