package in.sahilpatel.bulletinboard.listeners;

import in.sahilpatel.bulletinboard.model.User;

/**
 * Created by Administrator on 9/12/2016.
 */
public interface OnMemberSelectedListener {

        void isSelected(User user);
        void isUnSelected(User user);
        void onClickedCancel();
        void onClickedOk();
}
