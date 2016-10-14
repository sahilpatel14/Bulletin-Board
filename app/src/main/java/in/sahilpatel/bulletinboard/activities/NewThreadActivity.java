package in.sahilpatel.bulletinboard.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import administrator.sahilpatel.com.imageuploader.ImageUploader;
import administrator.sahilpatel.com.runtimepermissionvalidator.RuntimePermissionResult;
import administrator.sahilpatel.com.runtimepermissionvalidator.RuntimePermissionValidator;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.firebase.FirebaseThreadApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.fragments.AddMembersFragment;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.listeners.OnMemberSelectedListener;
import in.sahilpatel.bulletinboard.model.User;


/**
 * This Fragment handles Creation of new thread. It also calls AddMemberFragment to add new members,
 * moderators.
 */
public class NewThreadActivity extends AppCompatActivity {

    /**
     * Variables required to send Image Request, Tag for add members and add moderators
     * sharePostFragment. Tag for debugging.
     */
    public static final String TAG = NewThreadActivity.class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST = 14;
    public static final String ADD_MEMBERS_TAG = "ADD_MEMBERS_TAG";
    public static final String ADD_MODERATORS_TAG = "ADD_MODERATORS_TAG";
    public static final String ALL_USERS = "ALL_USERS";
    public static final String USER = "USER";
    private static final String IMAGE_UPLOAD_URL = "http://sahilpatel.in/BulletinBoard/upload.php";
    private static final String DEFAULT_IMAGE = "201608291024251735552805.jpg";
    /**
     * These are the fields which are filled by code, not by logic, therefore
     * they must have scope throughout the class. The will be initialized as
     * soon as View is inflated.
     */
    private EditText imageUrlEditText;
    private EditText addMembersField;
    private EditText addModeratorsField;
    private String img_url;
    private String img_path;
    
    /**
     * Checkboxes, a hack to allow radio button kind of behaviour from checkboxes.
     */
    private CheckBox all_post;
    private CheckBox moderators_post;
    private CheckBox all_add;
    private CheckBox moderators_add;
    private CheckBox owner_edit;
    private CheckBox moderators_edit;


    /**
     * Contains name and description of data.
     */
    private EditText nameField;
    private EditText descriptionField;
    /**
     * Stores all the added members and all the added moderators. Required for user
     * friendly project of AddMemberFragment.
     */
    private List<User> memberList;
    private List<User> moderatorList;
    private User user;
    private List<User> allUsers;

    /**
     * To select members and moderators. mFirebaseUserApi to get all probable members list
     * and to add data to user objects in fire base.
     */
    private AddMembersFragment membersFragment;
    private AddMembersFragment moderatorsFragment;
    private ProgressDialog mProgressDialog;
    private RuntimePermissionValidator runtimePermissionValidator;

    /**
     * We define what kind of Dialog window we need. Also we initialize the two
     * lists, the two add member fragments and FirebaseUserApi.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Bulletin Board");

        memberList = new ArrayList<>();
        moderatorList = new ArrayList<>();

        membersFragment = new AddMembersFragment();
        moderatorsFragment = new AddMembersFragment();
//        mFirebaseUserApi = new FirebaseUserApi();

        mProgressDialog = new ProgressDialog(this);
        runtimePermissionValidator = new RuntimePermissionValidator(this);

        init();                             //  initializing view
        addClickListeners();                //  adding necessary clickListeners

    }

    /**
     * Initializes all view objects having global scope.
     */
    private void init() {

        Intent intent = getIntent();
        user = intent.getParcelableExtra(USER);
        allUsers = intent.getParcelableArrayListExtra(ALL_USERS);

        imageUrlEditText = (EditText)findViewById(R.id.field_thread_image_url);
        all_post = (CheckBox) findViewById(R.id.checkbox_post_who_all);
        moderators_post = (CheckBox) findViewById(R.id.checkbox_post_who_moderators);

        all_add = (CheckBox)findViewById(R.id.checkbox_add_who_all);
        moderators_add = (CheckBox)findViewById(R.id.checkbox_add_who_moderators);

        owner_edit = (CheckBox)findViewById(R.id.checkbox_edit_who_owner);
        moderators_edit = (CheckBox)findViewById(R.id.checkbox_edit_who_moderators);

        addMembersField = (EditText)findViewById(R.id.field_thread_members);
        addModeratorsField = (EditText)findViewById(R.id.field_add_moderators);

        nameField  = (EditText)findViewById(R.id.field_thread_name);
        descriptionField = (EditText)findViewById(R.id.field_thread_description);

        for (User user : allUsers) {
            membersFragment.userList.add(user);
        }
    }

    /**
     * Adds all the click listeners that require callbacks. All listeners are present here.
     * The required callbacks are present in helper methods section
     */
    private void addClickListeners() {

        /**
         * When clicked opens a chooser. The user can select an image from gallery which will then
         * be saved and uploaded. Choosing image is performed by chooseImageFromGallery() helper method
         */
        findViewById(R.id.button_choose_thread_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });


        /**
         * hack to make radioButton out of checkbox.
         */
        clickListenerForCheckBox();


        /**
         * Opens AddMemberFragment. The result is obtained using OnMemberSelectedListener callback methods.
         * we are setting the callback here.
         */
        membersFragment.setmCallback(setMemberSelectedListener());
        findViewById(R.id.button_add_thread_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membersFragment.show(getSupportFragmentManager(),ADD_MEMBERS_TAG);
            }
        });


        /**
         * Opens AddMemberFragment for moderators. Here we need to pass the current memberList of
         * users as userList. For this reason, that code is inside clickListener.
         */
        moderatorsFragment.setmCallback(setModeratorSelectedListener());
        findViewById(R.id.button_add_moderators).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                moderatorsFragment.userList = memberList;
                for (User member : memberList){
                    moderatorsFragment.userList.add(member);
                }
                moderatorsFragment.show(getSupportFragmentManager(), ADD_MODERATORS_TAG);
            }
        });


//        /**
//         * Returns all the Probable group members from Firebase and returns it Asynchronously.
//         * The received Users list is then passed to AddMemberFragment. Since the operation is
//         * Asynchronous, the list will not be populated for quite some time.
//         */
//        mFirebaseUserApi.getProbableGroupMembers(setProbableUsersListener());


        /**
         * Called when user clicks Create button
         */
        findViewById(R.id.button_create_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createThreadDialog();
            }
        });

        /**
         * Called when user clicks Cancel button
         */
        findViewById(R.id.button_cancel_create_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog();
            }
        });

        /**
         * Other required click listeners for view.
         * setting uneditable.
         */

        imageUrlEditText.setKeyListener(null);
        addMembersField.setKeyListener(null);
        addModeratorsField.setKeyListener(null);
    }

    /**
     * Called when the user clicks create thread button.
     */
    private void createNewThread() {

        mProgressDialog.setMessage("Creating Thread...");
        String threadName = nameField.getText().toString();

        /**
         * Checks if the name of thread and name description of thread is
         * empty.
         */
        //  validation steps.
        if(threadName.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String threadDescription = descriptionField.getText().toString();
        if(threadDescription.isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        //  validation complete

        /**
         * Putting image for upload. Name of image will be returned.
         * Image upload is asynchronous but the name is returned synchronously.
         * If it's null, we pass the name of default Image.
         */
        img_url = uploadImage(img_path);
        if(img_url == null || img_url.isEmpty()) {
            //  default image
            img_url = DEFAULT_IMAGE;
        }

        /**
         * Defining policy for the new thread. Who can add edit post
         * on the newly created thread.
         */
        NewThread.Policy policy = new NewThread().new Policy(
                NewThread.Policy.TYPE_MODERATORS,
                NewThread.Policy.TYPE_EVERYONE,
                NewThread.Policy.TYPE_OWNER_AND_MODERATORS
        );

        if(all_post.isChecked()) {
            policy.setAdd_post(NewThread.Policy.TYPE_EVERYONE);
        }
        else {
            policy.setAdd_post(NewThread.Policy.TYPE_MODERATORS);
        }

        if(all_add.isChecked()) {
            policy.setAdd_members(NewThread.Policy.TYPE_EVERYONE);
        }
        else {
            policy.setAdd_members(NewThread.Policy.TYPE_MODERATORS);
        }

        if(owner_edit.isChecked()) {
            policy.setEdit_post(NewThread.Policy.TYPE_OWNERS);
        }
        else {
            policy.setEdit_post(NewThread.Policy.TYPE_OWNER_AND_MODERATORS);
        }
        //  policy defined


        /**
         * We set all the members selected by user into a Map
         * of Name and keys.
         */
//        NewThread.Member member;

        //adding owner

        String name = user.getName();
        String owner_key = user.getUid();

        Map<String,String> owners = new HashMap<>();
        owners.put(owner_key,name);
        //  owner added

        //  adding moderators
        Map<String, String> moderators = new HashMap<>();
        for(User user : moderatorList) {
            moderators.put(user.getUid(),user.getName());   //  NOT UID, IT IS THE KEY.
        }

        //  adding members
        Map<String, String> members = new HashMap<>();
        for (User user : memberList ) {
            members.put(user.getUid(),user.getName());     //  NOT UID, IT IS THE KEY.
        }
        //   adding owner to members
        members.put(owner_key,name);

        //  uploading thread
        uploadThread(threadName,threadDescription,policy,members,owners,moderators);

        mProgressDialog.dismiss();
        Toast.makeText(this, "Thread created", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Helper methods. These methods helps in Thread creation
     */

    /**
     * Called by AddMemberFragment object.
     * @return instance of OnMemberSelectedListener
     */
    private OnMemberSelectedListener setMemberSelectedListener() {
        return new OnMemberSelectedListener() {

            //  User selected an item, add that selected item to membersList. Add to my copy of memberList
            //  and AddMemberFragment copy of memberList.
            @Override
            public void isSelected(User user) {
                memberList.add(user);
                membersFragment.memberList.add(user);
            }

            //  User unchecked a particular item, remove it from memberList.
            @Override
            public void isUnSelected(User user) {
                memberList.remove(user);
                membersFragment.memberList.remove(user);
            }

            //  User cancelled the operation. remove all data from memberList.
            //  But not from AddMemberFragmentCopy of memberList.
            @Override
            public void onClickedCancel() {
                memberList = new ArrayList<>();
                addMembersField.setText("");
            }

            //  User clicked ok after the selection, now we have all the selected users
            //  in a list. Use that list to populate editText.
            @Override
            public void onClickedOk() {

                StringBuffer buffer = new StringBuffer();
                for(User user : memberList) {
                    buffer.append(user.getName());
                    buffer.append(";");
                }
                addMembersField.setText("");
                addMembersField.setText(buffer);
            }
        };
    }

    /**
     * Called by AddMemberFragment object.
     * @return instance of OnMemberSelectedListener
     */
    private OnMemberSelectedListener setModeratorSelectedListener() {
        return new OnMemberSelectedListener() {

            //  User selected an item, add that selected item to moderatorList. Add to my copy of moderatorList
            //  and AddMemberFragment copy of moderatorList.

            @Override
            public void isSelected(User user) {
                moderatorList.add(user);
                moderatorsFragment.memberList.add(user);
            }

            //  User unchecked a particular item, remove it from moderatorList.
            @Override
            public void isUnSelected(User user) {
                moderatorList.remove(user);
                moderatorsFragment.memberList.remove(user);
            }

            //  User cancelled the operation. remove all data from moderatorList.
            //  Also from AddMemberFragmentCopy of memberList since, we dont need to preserve that copy.
            @Override
            public void onClickedCancel() {
                moderatorList = new ArrayList<>();
                addModeratorsField.setText("");
                moderatorsFragment.memberList = new ArrayList<>();
            }

            //  User clicked ok after the selection, now we have all the selected users
            //  in a list. Use that list to populate editText.
            @Override
            public void onClickedOk() {

                StringBuffer buffer = new StringBuffer();
                for (User user : moderatorList) {
                    buffer.append(user.getName());
                    buffer.append(";");
                }
                addModeratorsField.setText(buffer);
            }
        };
    }

//    /**
//     *
//     * @return ValueEventListener. initializes users list and passes it to
//     * AddUserFragment.
//     */
//    private ValueEventListener setProbableUsersListener() {
//        return new ValueEventListener() {
//
//            //  Contains all probable users as json array.
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot shot : dataSnapshot.getChildren()) {
//                    User user = shot.getValue(User.class);
//                    user.setUid(shot.getKey());
//                    membersFragment.userList.add(user);
//                    Log.d(TAG, "onDataChange: "+user.getName());
//                }
//            }
//
//            //  Failed to do so.
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                Log.d(TAG, "onCancelled: "+" failed to load probable users list.");
//            }
//        };
//    }

    /**
     * Starts an intent to choose images from gallery.
     * Returned image is handled in onActivityResult() method.
     */
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

        /**
         * Calling our customPhotoGallery to choose the image.
         */
        Intent intent = new Intent(this,CustomPhotoGalleryActivity.class);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    /**
     * Convert the image uri into a Bitmap object.
     * display the uri path in an EditText.
     * @param requestCode, the code
     * @param resultCode, the result code
     * @param data, image paths
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            String[] imagesPath = data.getStringExtra("data").split("\\|");
            img_path = imagesPath[0];
            imageUrlEditText.setText(img_path);
        }
    }

    /**
     * Required for runtimePermissionValidator. Simply pass the call to object
     * of kind runtimePermissionValidator. It will return the result using callback
     * methods.
     * @param requestCode, the code
     * @param permissions, permissions we require
     * @param grantResults, status
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        runtimePermissionValidator.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    /**
     * Creates radio button behaviour from checkboxes. Pretty expensive
     */
    private void clickListenerForCheckBox() {


        all_post.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(all_post.isChecked()) {
                    moderators_post.setChecked(false);
                }
                else {
                    moderators_post.setChecked(true);
                }
            }
        });

        moderators_post.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(moderators_post.isChecked()) {
                    all_post.setChecked(false);
                }
                else {
                    all_post.setChecked(true);
                }
            }
        });

        all_add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(all_add.isChecked()) {
                    moderators_add.setChecked(false);
                }
                else {
                    moderators_add.setChecked(true);
                }
            }
        });

        moderators_add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(moderators_add.isChecked()) {
                    all_add.setChecked(false);
                }
                else {
                    all_add.setChecked(true);
                }
            }
        });

        owner_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(owner_edit.isChecked()) {
                    moderators_edit.setChecked(false);
                }
                else {
                    moderators_edit.setChecked(true);
                }
            }
        });

        moderators_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(moderators_edit.isChecked()) {
                    owner_edit.setChecked(false);
                }
                else {
                    owner_edit.setChecked(true);
                }
            }
        });
    }

    /**
     * This dialog pops up when we try to go back from this activity.
     * Gives simple yes no prompt before actually closing the activity
     * and removing all the entered data.
     */
    private void cancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close parent activity.
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  Close this dialog
            }
        });
        builder.show();
    }

    /**
     * This dialog pops up when the user clicks on createThread dialog and
     * all the validations are satisfied, it called right before creating
     * the actual thread on web server.
     */
    private void createThreadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Create this thread ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createNewThread();
            }
        });

        builder.setNegativeButton("Wait", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  Close this dialog
            }
        });
        builder.show();
    }

    /**
     * Uploads an image stored in location pointed by path. Image upload operation
     * is carried out asynchronously by ImageUploader module. This method returns
     * filename of the image.
     * @param path String, url of image
     * @return String, filename
     */
    public String uploadImage(String path) {

        ImageUploader imageUploader = new ImageUploader(IMAGE_UPLOAD_URL);
        String extension = path.substring(path.lastIndexOf("."));
        String fileName = System.currentTimeMillis()+extension;

        Log.d(TAG, "requesting uploadImage: "+fileName);
        imageUploader.uploadImage(path,fileName);

        return fileName;
    }

    /**
     * Uploading the details collected via form to the server. This process will
     * happen synchronously, except the image upload process.
     * @param threadName, the name of our thread.
     * @param description, some descriptive words about the thread
     * @param policy, who can post, edit and add members.
     * @param members, all the members of this thread.
     * @param owners, the user who created this thread.
     * @param moderators, moderators for this thread
     */
    private void uploadThread(String threadName, String description, NewThread.Policy policy,
                              Map<String,String> members,Map<String,String> owners,
                              Map<String,String> moderators) {
        /**
         * We will update the last_activity field of every user who is joining the group.
         * We do so using the following technique.
         */
        Map<String,String> last_activity = new HashMap<>();
        String user_key = new MySharedPreferences(this).getUserKey();
        last_activity.put(System.currentTimeMillis()+"","Thread created by "+owners.get(user_key)+".");


        /**
         * Creating a thread object to be uploaded. Then we use another
         * method to upload that thread object to server.
         */
        NewThread thread = new NewThread(threadName,last_activity,img_url,description,
                policy,owners,moderators,members);
        FirebaseThreadApi api = new FirebaseThreadApi();
        String thread_id = api.addThread(thread);

        /**
         * Editing user objects, adding thread id to all members of thread.
         * to all moderators of thread. Adding the thread to owned thread
         * list of the user.
         */
        FirebaseUserApi userApi = new FirebaseUserApi();

        List<String> owner_keys = new ArrayList<>(owners.keySet());
        for (String key:owner_keys) {
            userApi.addOwnedThread(key,threadName,thread_id);
        }

        List<String> moderator_keys = new ArrayList<>(moderators.keySet());
        for (String key:moderator_keys) {
            userApi.addModeratedThread(key,threadName,thread_id);
        }

        List<String> member_keys = new ArrayList<>(members.keySet());
        for (String key:member_keys) {
            userApi.addMemberThread(key,threadName,thread_id);
        }
    }
}



