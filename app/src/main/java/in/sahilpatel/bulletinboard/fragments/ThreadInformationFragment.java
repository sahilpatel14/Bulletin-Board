package in.sahilpatel.bulletinboard.fragments;

import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.adapters.AddMembersAdapter;
import in.sahilpatel.bulletinboard.adapters.posts.holders.PostHolder;
import in.sahilpatel.bulletinboard.firebase.FirebaseThreadApi;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.listeners.OnMemberSelectedListener;
import in.sahilpatel.bulletinboard.model.NewThread;
import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.services.ThreadDownloadService;
import in.sahilpatel.bulletinboard.support.TimestampConversion;


public class ThreadInformationFragment extends DialogFragment {

    private static final String TAG = "ThreadInformationFrag";

    private TextView field_thread_name;
    private TextView field_thread_description;
    private TextView field_last_activity;
    private ImageView field_thread_image;
    private Button button_exit_group;

    private RecyclerView members_recycler_view;
    private NewThread thread;


    private List<User> allUsers;
    private List<User> members;
    private List<User> newMembers;

    private EditText field_add_members;

    private ProgressDialog mProgressDialog;
    private AddMemberAdapter adapter;
    private AddMembersFragment addMembersFragment;

    public void setThread(NewThread thread) {
        this.thread = thread;
    }

    public void setAllUsers(List<User> allUsers) {  this.allUsers = allUsers;   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.layout_thread_information, container, false);

        if (thread == null)
            return null;

        members = new ArrayList<>();
        getMembers();

        field_thread_name = (TextView)rootView.findViewById(R.id.field_thread_name);
        field_thread_description = (TextView)rootView.findViewById(R.id.field_thread_description);
        field_last_activity = (TextView)rootView.findViewById(R.id.field_last_activity);
        members_recycler_view = (RecyclerView)rootView.findViewById(R.id.member_list_recycler_view);
        members_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        field_thread_image = (ImageView)rootView.findViewById(R.id.thread_image);

        button_exit_group = (Button)rootView.findViewById(R.id.button_exit_group);
        button_exit_group.setOnClickListener(exitButtonClicked());

        field_add_members = (EditText)rootView.findViewById(R.id.field_add_members);
        field_add_members.setOnKeyListener(typingMemberName());
        adapter = new AddMemberAdapter();

        addMembersFragment = new AddMembersFragment();
        newMembers = new ArrayList<>();
        addMembersFragment.memberList = newMembers;


        for (User u : allUsers) {
            if (!members.contains(u)){
                addMembersFragment.userList.add(u);
            }
        }

        rootView.findViewById(R.id.button_add_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addMembersFragment.setUserList(allUsers);
                openAddNewMembersWindow();
            }
        });




        addMembersFragment.setmCallback(setMemberSelectedListener());
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Fetching Data...");
        mProgressDialog.show();

//        fetchThreadData();
        displayThreadData();
        threadPolicies(rootView);
        return rootView;
    }

    private void threadPolicies(View rootView) {

        Map<String, String> moderators = thread.getMembersList();
        Map<String, String> owners = thread.getMembersList();

        String user_key = new MySharedPreferences(getContext()).getUserKey();
        NewThread.Policy policy = thread.getPolicy();


        if (policy.getAdd_members().equals(NewThread.Policy.TYPE_OWNERS)){
            if (owners.containsKey(user_key)) {
                rootView.findViewById(R.id.label_members_list).setVisibility(View.VISIBLE);
                return;
            }
        }

        if (moderators.containsKey(user_key)){
            rootView.findViewById(R.id.label_members_list).setVisibility(View.VISIBLE);
            return;
        }


        switch (policy.getAdd_members()){
            case NewThread.Policy.TYPE_EVERYONE :
                rootView.findViewById(R.id.label_members_list).setVisibility(View.VISIBLE);
                break;
            case NewThread.Policy.TYPE_MODERATORS :
                rootView.findViewById(R.id.label_members_list).setVisibility(View.GONE);
                break;
        }
    }

    private void openAddNewMembersWindow() {
        for (User user : newMembers){
            addMembersFragment.memberList.add(user);
        }
        addMembersFragment.show(getChildFragmentManager(),"Add members");
    }

    private OnMemberSelectedListener setMemberSelectedListener() {
        return new OnMemberSelectedListener() {
            @Override
            public void isSelected(User user) {
                newMembers.add(user);
//                addMembersFragment.userList.remove(user);
            }

            @Override
            public void isUnSelected(User user) {
                newMembers.remove(user);
//                addMembersFragment.userList.add(user);
            }

            @Override
            public void onClickedCancel() {
                newMembers = new ArrayList<>();
                field_add_members.setText("");
            }

            @Override
            public void onClickedOk() {

                StringBuffer sb = new StringBuffer();
                for (User user : newMembers) {
                    sb.append(user.getName()+";");

                }

                field_add_members.setText("");
                field_add_members.setText(sb);

                AlertDialog.Builder builder = new AlertDialog.Builder(ThreadInformationFragment.this.getContext());
                builder.setMessage("Add "+sb+" to the group?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (User user : newMembers) {
                            addUser(user);
                        }
                        field_add_members.setText("");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        field_add_members.setText("");
                    }
                });
                builder.show();
            }
        };
    }

    private void addUser(User user){
        Toast.makeText(getContext(), "Adding user", Toast.LENGTH_SHORT).show();
        new FirebaseThreadApi().addMember(thread.getThread_id(),user);

        FirebaseUserApi firebaseUserApi = new FirebaseUserApi();

        String message = user.getName()+" joined "+thread.getThread_name()+" thread.";
        for (User member: members) {
            firebaseUserApi.updateLastUserActivity(member.getUid(),message,User.LAST_ACTIVITY_STATUS_UNSEEN);
        }

        adapter.members.add(user.getUid());
        adapter.notifyDataSetChanged();
    }

    private void getMembers() {

        Map<String, String> m = thread.getMembersList();
        List<String> keys = new ArrayList<>(m.keySet());
        User user;
        for (String key : keys) {
            user = new User();
            user.setUid(key);
            user.setName(m.get(key));
            members.add(user);
        }

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setTitle("Thread info");
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private View.OnKeyListener typingMemberName() {
       return new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                String  ch = keyEvent.getCharacters();
                Log.d(TAG, "onKey: "+ch);
                return true;
            }
        };
    }


    private View.OnClickListener exitButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_key = new MySharedPreferences(getContext()).getUserKey();
                final String thread_key = thread.getThread_id();

                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new FirebaseUserApi().exitThread(user_key, thread_key, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                                        startDownloadService(user_key);
                                        Toast.makeText(getContext(), "Thread deleted", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismiss();
                            }
                        })
                        .create();
                dialog.show();

            }
        };
    }

    private void displayThreadData() {
        field_thread_name.setText(thread.getThread_name());
        field_thread_description.setText(thread.getDescription());

        Map<String, String> last_activity=  thread.getLast_activity();

        if (last_activity == null || last_activity.isEmpty()){
            field_last_activity.setText("No recent activity.");
            return;
        }
        List<String> ts = new ArrayList<>(last_activity.keySet());
        field_last_activity.setText(last_activity.get(ts.get(0))+" - "+ TimestampConversion.getReadableTimestamp(ts.get(0)));

        Log.d(TAG, "displayThreadData: "+PostHolder.url+thread.getImage_url());

        Glide.with(getContext()).load(PostHolder.url+thread.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sample_photo)
                .dontAnimate()
                .into(field_thread_image);

        members_recycler_view.setAdapter(adapter);
        mProgressDialog.dismiss();

    }

    private void startDownloadService(String user_id) {

        Intent intent = new Intent(getActivity(), ThreadDownloadService.class);
        intent.putExtra(ThreadDownloadService.USER_ID,user_id);

        getActivity().startService(intent);
        Log.d(TAG, "startDownloadService: "+"Service started.");
    }

    class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MemberViewHolder> {

        private List<String> moderators;
        private List<String> owners;
        private List<String> members;

        public AddMemberAdapter() {

            if (thread.getModeratorList() == null){
                moderators = new ArrayList<>();
            }
            else {
                moderators = new ArrayList<>(thread.getModeratorList().keySet());
            }

            owners = new ArrayList<>(thread.getOwnerList().keySet());
            members = new ArrayList<>(thread.getMembersList().keySet());
        }



        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list,parent,false);
            return new MemberViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MemberViewHolder holder, int position) {

            String key = members.get(position);


            String name = "";
            String url = "";
            for (User user : allUsers) {
                if (user.getUid().equals(key)){
                    name = user.getName();
                    url = user.getProfile_image_url();
                }
            }
            holder.textView.setText(name);

            if(owners.contains(key)) {
                holder.member_type_owner.setVisibility(View.VISIBLE);
            }
            else
                if (moderators.contains(key)){
                    holder.member_type_moderator.setVisibility(View.VISIBLE);
                }

            Glide.with(getContext()).load(url).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        class MemberViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView imageView;
            private TextView textView;
            private TextView member_type_owner;
            private TextView member_type_moderator;

            public MemberViewHolder(View itemView) {
                super(itemView);

                imageView = (CircleImageView)itemView.findViewById(R.id.member_image);
                textView = (TextView) itemView.findViewById(R.id.member_name);
                member_type_owner = (TextView) itemView.findViewById(R.id.member_type_owner);
                member_type_moderator = (TextView) itemView.findViewById(R.id.member_type_moderator);
            }
        }
    }

}




























