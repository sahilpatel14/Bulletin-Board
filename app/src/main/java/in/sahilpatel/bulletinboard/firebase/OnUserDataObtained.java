package in.sahilpatel.bulletinboard.firebase;


import com.firebase.client.FirebaseError;

import in.sahilpatel.bulletinboard.model.User;

public interface OnUserDataObtained {

    public void onSuccess(User user);
    public void onFailure(String message, FirebaseError error);
}
