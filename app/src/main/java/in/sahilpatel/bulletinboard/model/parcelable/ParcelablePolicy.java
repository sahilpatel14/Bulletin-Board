package in.sahilpatel.bulletinboard.model.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import in.sahilpatel.bulletinboard.model.NewThread;

/**
 * Created by Administrator on 10/6/2016.
 */

public class ParcelablePolicy implements Parcelable {

    private String add_members;
    private String add_post;
    private String edit_post;

    public ParcelablePolicy(NewThread.Policy policy) {

        if (policy == null)
            return;
        add_members = policy.getAdd_members();
        add_post = policy.getAdd_post();
        edit_post = policy.getEdit_post();
    }

    protected ParcelablePolicy(Parcel parcel) {

        add_members = parcel.readString();
        add_post = parcel.readString();
        edit_post = parcel.readString();
    }

    public NewThread.Policy getPolicy() {

        NewThread.Policy policy = new NewThread().new Policy();
        policy.setAdd_members(add_members);
        policy.setAdd_post(add_post);
        policy.setEdit_post(edit_post);

        return policy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(add_members);
        parcel.writeString(add_post);
        parcel.writeString(edit_post);
    }

    final static Parcelable.Creator<ParcelablePolicy> CREATOR
            = new Parcelable.Creator<ParcelablePolicy>(){

        @Override
        public ParcelablePolicy createFromParcel(Parcel parcel) {

            return new ParcelablePolicy(parcel);
        }

        @Override
        public ParcelablePolicy[] newArray(int size) {
            return new ParcelablePolicy[size];
        }
    };
}
