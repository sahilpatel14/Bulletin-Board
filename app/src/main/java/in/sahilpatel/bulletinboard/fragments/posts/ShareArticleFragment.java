package in.sahilpatel.bulletinboard.fragments.posts;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.FirebaseError;

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
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.firebase.OnUserDataObtained;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareArticleFragment extends Fragment {

    private static final String TAG = "ShareArticleFragment";
    private static final String IMAGE_UPLOAD_URL = "http://sahilpatel.in/BulletinBoard/upload.php";
    public static final int PICK_IMAGE_REQUEST = 2;

    private ProgressDialog mProgressDialog;
    private EditText field_heading;
    private EditText field_subHeading;
    private EditText field_content;
    private ImageView field_selected_image;
    private RuntimePermissionValidator runtimePermissionValidator;

    private ScrollView scrollView;

    private User user;
    List<String> imagePathList;

    public ShareArticleFragment() {}


    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_share_article, container, false);

        imagePathList = new ArrayList<>();
        runtimePermissionValidator = new RuntimePermissionValidator((AppCompatActivity) getActivity());

        scrollView = (ScrollView)rootView.findViewById(R.id.scroll_view);
        field_heading = (EditText)rootView.findViewById(R.id.field_article_heading);
        field_subHeading = (EditText)rootView.findViewById(R.id.field_article_subheading);
        field_content = (EditText)rootView.findViewById(R.id.field_article_content);
        field_selected_image = (ImageView)rootView.findViewById(R.id.field_selected_image);

        mProgressDialog = new ProgressDialog(getContext());

        rootView.findViewById(R.id.button_article_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePathList = new ArrayList<>();
                chooseImageFromGallery();
            }
        });

        (rootView.findViewById(R.id.article_heading_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusView(field_heading);
            }
        });

        field_subHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusView(field_subHeading);
            }
        });

        field_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+"getting click event.");
                focusView(field_content);
            }
        });


        return rootView;
    }


    /**
     * returns the selected image back to us. We will store the url for that
     * image in a global List.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if(requestCode == PICK_IMAGE_REQUEST){
                String[] imagesPath = data.getStringExtra("data").split("\\|");

                for (int i=0;i<imagesPath.length;i++){
                    imagePathList.add(imagesPath[i]);
                }
                Bitmap yourBitmap = BitmapFactory.decodeFile(imagePathList.get(0));
                int nh = (int) ( yourBitmap.getHeight() * (512.0 / yourBitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(yourBitmap, 512, nh, true);
                field_selected_image.setImageBitmap(scaled);
            }
        }

    }

    /**
     * Here we first check if we have permission to access external storage,
     * later we call the activity to choose image from the device.
     * The result is received in onActivityResult()
     */
    private void chooseImageFromGallery() {
        /**
         * Asking for runtime permission. If not permitted, call this
         * method again from a callback.
         */
        if (!runtimePermissionValidator.mayRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
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
                        Log.d(TAG, "onInternalError: " + error);
                    }
                }
        )) {
            return;
        }

        Intent intent = new Intent(getContext(),CustomPhotoGalleryActivity.class);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    /**
     * We first validate if the input is correct or not. Later, we call uploadPost
     * that would do the actual uploading.
     * @param thread
     */
    public void post(NewThread thread) {

        String heading = field_heading.getText().toString();

        if(heading == null || heading.isEmpty()) {
            Toast.makeText(getContext(), "Cannot leave heading field blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imagePathList.isEmpty()){
           imagePathList.add("");

        }

        String subheading = field_subHeading.getText().toString();
        if(heading == null) {
            heading = "";
        }

        String content = field_content.getText().toString();
        if(content == null) {
            content = "";
        }

        uploadPost(heading,subheading,content,thread.getThread_id(),imagePathList.get(0));

        Map<String, String> membersMap = thread.getMembersList();
        Set<String> keys = membersMap.keySet();
        String[] members = keys.toArray(new String[keys.size()]);

        String message = user.getName()+" posted an article on thread "+thread.getThread_name()+".";
        new FirebaseUserApi().updateLastUserActivity(user.getUid(),members,message);
    }


    /**
     * Here we take all the data entered by the user, the validated one and
     * use it create the post object. This post object and the images that
     * the user has selected is uploaded to the server.
     * @param heading
     * @param subHeading
     * @param content
     * @param thread_id
     * @param image_path
     */
    private void uploadPost(String heading, String subHeading, String content
            , String thread_id,String image_path) {

        mProgressDialog.setMessage("Posting..");
        mProgressDialog.show();

        String post_type = Post.POST_TYPE_SHARE_ARTICLE+"";
        if(subHeading.equals("")) {
            post_type = Post.POST_TYPE_HEADING_CONTENT+"";
        }

        if (content.equals("")) {
            post_type = Post.POST_TYPE_HEADING_CONTENT+"";
        }

        if(image_path.equals("")) {
            post_type = Post.POST_TYPE_HEADING_SUBHEADING_CONTENT+"";
        }

        final Post post = new Post();

        if(!image_path.equals("")){
            String img_name = uploadImage(image_path);
            List<String> list = new ArrayList<>();
            list.add(img_name);
            post.setImages(list);
        }

        post.setContent(content);
        post.setHeading(heading);
        post.setSubHeading(subHeading);
        post.setThreadId(thread_id);
        post.setComments("0");
        post.setThumbsUp("0");
        post.setTimestamp(System.currentTimeMillis()+"");
        post.setPostType(post_type);
        Map<String,String> owner = new HashMap<>();
        owner.put(user.getUid(),user.getName());
        post.setOwner(owner);
        post.setPicUrl(user.getProfile_image_url());
        new FirebasePostApi().addPost(post);
        Toast.makeText(getContext(), "Posted to thread", Toast.LENGTH_SHORT).show();
        field_heading.setText("");
        field_subHeading.setText("");
        field_content.setText("");
        field_selected_image.setImageBitmap(null);

    }

    /**
     * Uploads the image to the server. That's pretty much it. It would keep running
     * until the images are uploaded.
     * @param path
     * @return
     */
    public String uploadImage(String path) {

        mProgressDialog.setMessage("Uploading image..");
        ImageUploader imageUploader = new ImageUploader(IMAGE_UPLOAD_URL);
        String extension = path.substring(path.lastIndexOf("."));
        String fileName = System.currentTimeMillis()+extension;

        Log.d(TAG, "requesting uploadImage: "+fileName);
        imageUploader.uploadImage(path, fileName, new FileUploadResult() {
            @Override
            public void onSuccess(JSONArray result) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {

            }
        });

        return fileName;
    }

    private void focusView(final EditText field) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, field.getTop());

//                int vLeft = view.getLeft();
//                int vRight = view.getRight();
//                int sWidth = scroll.getWidth();
//                scroll.smoothScrollTo(((vLeft + vRight - sWidth) / 2), 0);
            }
        });
    }

}


