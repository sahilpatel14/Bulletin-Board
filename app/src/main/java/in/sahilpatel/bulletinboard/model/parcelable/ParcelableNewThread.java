package in.sahilpatel.bulletinboard.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import in.sahilpatel.bulletinboard.model.NewThread;

/**
 * Created by Administrator on 10/6/2016.
 */

public class ParcelableNewThread implements Parcelable {

    private String thread_id;
    private String thread_name;
    private String image_url;
    private Map<String, String> last_activity;
    private String description;
    private Map<String, String> memberList;
    private Map<String, String> ownerList;
    private Map<String, String> moderatorList;
    private ParcelablePolicy policy;


    public ParcelableNewThread(NewThread thread) {


        thread_id = thread.getThread_id();
        thread_name = thread.getThread_name();
        image_url = thread.getImage_url();
        last_activity = thread.getLast_activity();
        description = thread.getDescription();
        memberList = thread.getMembersList();
        ownerList = thread.getOwnerList();
        moderatorList = thread.getModeratorList();
        policy = new ParcelablePolicy(thread.getPolicy());
    }

    protected ParcelableNewThread(Parcel parcel) {

        thread_id = parcel.readString();
        thread_name = parcel.readString();
        image_url = parcel.readString();
        last_activity = new HashMap<>();
        last_activity = parcel.readHashMap(null);


        description = parcel.readString();
        memberList = new HashMap<>();
        memberList = parcel.readHashMap(null);

        ownerList = new HashMap<>();
        ownerList = parcel.readHashMap(null);

        moderatorList = new HashMap<>();
        moderatorList = parcel.readHashMap(null);

        policy = parcel.readParcelable(getClass().getClassLoader());

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(thread_id);
        parcel.writeString(thread_name);
        parcel.writeString(image_url);
        parcel.writeMap(last_activity);
        parcel.writeString(description);
        parcel.writeMap(memberList);
        parcel.writeMap(ownerList);
        parcel.writeMap(moderatorList);
        parcel.writeParcelable(policy,0);
    }

    public NewThread getThread() {
        NewThread thread = new NewThread();
        thread.setThread_id(thread_id);
        thread.setThread_name(thread_name);
        thread.setImage_url(image_url);
        thread.setLast_activity(last_activity);
        thread.setDescription(description);
        thread.setMembersList(memberList);
        thread.setOwnerList(ownerList);
        thread.setModeratorList(moderatorList);
        thread.setPolicy(policy.getPolicy());

        return thread;
    }

    final static Parcelable.Creator<ParcelableNewThread> CREATOR
            = new Parcelable.Creator<ParcelableNewThread>(){

        @Override
        public ParcelableNewThread createFromParcel(Parcel parcel) {
            return new ParcelableNewThread(parcel);
        }

        @Override
        public ParcelableNewThread[] newArray(int size) {
            return new ParcelableNewThread[size];
        }
    };
}
