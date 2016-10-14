package in.sahilpatel.bulletinboard.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.bumptech.glide.Glide;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.model.User;


/**
 * Adapter to add user data on AddMemebersFragment. It takes in two Lists userList and memberList.
 * userList contains all the users who could be added to the group. MemberList contains user's
 * already added by thread creator. the two lists are public.
 */
public class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.MyViewHolder> {

    /**
     * list to store all potential members.
     * list to store all already added members.
     * the parent fragment containing Recycler View.
     */
    private List<User> userList;
    private List<User> memberList;
    private Fragment fragment;


    /**
     * initializer for all private members
     * @param fragment
     * @param userList
     * @param memberList
     */
    public AddMembersAdapter(Fragment fragment, List<User> userList, List<User> memberList) {
        this.fragment = fragment;
        this.userList = userList;
        this.memberList = memberList;
    }

    /**
     * inflates our item layout for the Recycler
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_member_list_item,parent,false);

        return new MyViewHolder(itemView);
    }

    /**
     * normal binding process except one catch. In case the user to be added is already
     * present on memberList, we check the field.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = userList.get(position);
        holder.textView.setText(user.getName());


        if(memberList.contains(user)){
            holder.textView.setChecked(true);
        }
        Glide.with(fragment).load(user.getProfile_image_url()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder contains two fields. A CheckedTextView containing name of the user and
     * an ImageView with his profile pic.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckedTextView textView;
        public CircleImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (CheckedTextView) itemView.findViewById(R.id.add_member_name_field);
            imageView = (CircleImageView) itemView.findViewById(R.id.add_member_image_field);
        }
    }
}
