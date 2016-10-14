package administrator.sahilpatel.com.runtimepermissionvalidator;

/**
 * Created by Sahil Patel on 9/12/2016.
 */

/**
 * This interface is used to return result of runtime permission request.
 * It has two methods, onPermissionGranted and onPermissionDenied. User must
 * override both the methods while using RuntimePermissionValidator.
 */
public interface RuntimePermissionResult {

    /**
     * Called when the permission has been granted by the user.
     * Generally, in this overridden method, you must call the
     * same method that invoked permission checking
     */
    void onPermissionGranted(String permission_name);


    /**
     * Called when the permission has been denied. You can either show
     * an error screen, stop the app, open settings window, disable functionality etc
     * from this method.
     */
    void onPermissionDenied(String permission_name);


    /**
     * Called when there is some internal error that caused to permission to deny.
     * The internal error could be invalid REQUEST_CODE, crashes, etc.
     * @param error
     */
    void onInternalError(String error);
}
