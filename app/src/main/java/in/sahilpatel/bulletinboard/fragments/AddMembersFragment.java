package in.sahilpatel.bulletinboard.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.adapters.AddMembersAdapter;
import in.sahilpatel.bulletinboard.listeners.ClickListener;
import in.sahilpatel.bulletinboard.listeners.OnMemberSelectedListener;
import in.sahilpatel.bulletinboard.listeners.RecyclerTouchListener;
import in.sahilpatel.bulletinboard.model.User;
import in.sahilpatel.bulletinboard.activities.NewThreadActivity.*;
import in.sahilpatel.bulletinboard.support.DividerItemDecoration;
import io.fabric.sdk.android.InitializationException;

/**
 * A Dialog Fragment that opens a recycler view containing all the users
 * that are not already in the group and could be added. Uses AddMemberAdapter
 * class to populate data.
 *
 * Uses a callback interface to return which user was selected or clicked by user.
 */
public class AddMembersFragment extends DialogFragment {

    /**
     * Two lists to store potential members and already existing members.
     * RecyclerView to show users.
     * The adapter for RecyclerView
     *
     * callback listener
     */
    public List<User> userList;
    public List<User> memberList;
    private RecyclerView mRecyclerView;
    private AddMembersAdapter mAddMembersAdapter;
    private OnMemberSelectedListener mCallback;

    private static final String TAG = "AddMembersFragment";


    public AddMembersFragment(){
        super();
        /*
            Initializing lists
        */
        userList = new ArrayList<>();
        memberList = new ArrayList<>();
        mAddMembersAdapter = new AddMembersAdapter(this,userList,memberList);
    }

    /**
     * Since a fragment must have a default empty constructor, we move listener initialization
     * part to a setter. This creates a problem of uninitialized callback exception. (mCallback
     * throwing NullPointerException).
     *
     * @param mCallback
     */

    public void setmCallback(OnMemberSelectedListener mCallback) {

        this.mCallback = mCallback;
    }


    /**
     * We set what kind of Dialog we need. sets style and window layout for
     * the Dialog.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called when the view is about to be created. When user calls show on the class's
     * object. We initialize all members that should be before showing the view. This is the location
     * where we set all the listeners for buttons as well.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mCallback == null) {
            throw new InitializationException("You must initialize mCallback");
        }

        final View rootView = inflater.inflate(R.layout.layout_add_members, container, false);

        //  initializing adapter

        mAddMembersAdapter.notifyDataSetChanged();


        //  Initializing recyclerView object, setting ItemDecoration, layout, itemAnimator and set listeners
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.add_members_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAddMembersAdapter);

        //  set all listeners on all buttons and recyclerView
        setListeners(rootView);
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    //  Helper methods

    /**
     * Adds listeners for all buttons and recyclerView.
     * @param rootView
     */
    private void setListeners(final View rootView) {

        mRecyclerView.addOnItemTouchListener(this.setRecyclerTouchListener());

        rootView.findViewById(R.id.button_cancel_add_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClickedCancel();
                dismiss();
            }
        });

        rootView.findViewById(R.id.button_add_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClickedOk();
                dismiss();
            }
        });
    }

    /**
     * Creates a recyclerTouchListener object with necessary overridden methods.
     * @return RecyclerTouchListener
     */
    private RecyclerTouchListener setRecyclerTouchListener() {
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(
                getContext().getApplicationContext(), mRecyclerView, new ClickListener()
        {

            @Override
            public void onClick(View view, int position) {

                /**
                 * view is a RelativeLayout containing ImageView and CheckedTextView. For textView,
                 * we check if it is checked or not, if it is checked, we change it to unchecked, and call
                 * the callback.isUnchecked method with the user object it is containing.
                 *
                 * else we check the field and call isChecked method of mCallback
                 */
                CheckedTextView textView = (CheckedTextView) view.findViewById(R.id.add_member_name_field);

                if (textView.isChecked()) {
                    textView.setChecked(false);
                    User user = userList.get(position);
                    Toast.makeText(getContext().getApplicationContext(),"You unselected "+user.getName(),Toast.LENGTH_SHORT).show();

                    mCallback.isUnSelected(user);
                }
                else {
                    textView.setChecked(true);
                    User user = userList.get(position);
                    Toast.makeText(getContext().getApplicationContext(),"You selected "+user.getName(),Toast.LENGTH_SHORT).show();
                    mCallback.isSelected(user);
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        return recyclerTouchListener;
    }
}
