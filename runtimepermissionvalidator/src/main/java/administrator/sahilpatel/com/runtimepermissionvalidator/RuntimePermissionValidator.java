package administrator.sahilpatel.com.runtimepermissionvalidator;

/**
 * Created by Sahil Patel on 9/12/2016.
 */

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * This is the root class in the validator module. This class must be
 * instantiated whenever you need to check or ask for runtime permissions.
 * The class takes only one parameter in its constructor, the name of activity
 * that needs to ask permission.
 *
 * IMPORTANT
 * The calling class must implement RuntimePermissionResult interface or
 * it must send the interface as a callback. Failing to do both would result
 * in a ClassCastException or a NullPointerException
 */
public final class RuntimePermissionValidator {

    /**
     * We need an activity object, AppCompatActivity, to provide backward compatibility.
     * REQUEST_PERMISSION is an integer code, to segregate our request from other requests.
     * a Callback object of kind RuntimePermissionResult.
     * A String variable to store message. This message will be shown to user if required.
     */
    private AppCompatActivity activity;
    public final int REQUEST_PERMISSION = 14;
    private RuntimePermissionResult mCallback;
    private String message;

    /**
     * Empty constructor is private, so that no other activity can instantiate
     * it without passing activity object.
     *
     * Our public constructor. This constructor will be used to create objects
     * of our class.
     */
    private RuntimePermissionValidator(){}
    public RuntimePermissionValidator(AppCompatActivity activity) {

        this.activity = activity;
    }

    /**
     * This is the only method that will be used to check for permissions. It returns a
     * boolean value based based on the fact if we need to ask for runtime permission.
     * In case we need to ask for permission, the same method would ask for it as well.
     * @param permission
     * @return boolean , required to ask permission or not.
     */
    public boolean mayRequestPermission(final String permission) {

        /**
         * Checking if the calling activity has implemented the interface or not.
         * If not, we raise an exception.
         */
        try {
            mCallback = (RuntimePermissionResult)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException("You must implement RuntimePermissionResult");
        }

        /**
         * If the device version is less than marshmallow, we dont need  to ask
         * for permission. It is already given.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        /**
         * If the permission was already asked before and given, we should not ask it again.
         * This condition checks the same thing.
         */
        if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        /**
         * Here we are using a message to tell the user why we need the permission.
         * This message must be set using setter method before calling mayRequestPermission
         */
        if(activity.shouldShowRequestPermissionRationale(permission)) {

            if(message.isEmpty()) {
                //  User did not set the message.
                message = "The app needs to have "+permission.toString()+" permission to function properly.";
            }
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION);
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
        else {
            /**
             * No need for a rationale, directly ask for permission
             */
            activity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION);
        }
        return false;
    }


    /**
     * This is the only method that will be used to check for permissions. It returns a
     * boolean value based based on the fact if we need to ask for runtime permission.
     * In case we need to ask for permission, the same method would ask for it as well.
     * @param permission
     * @return boolean , required to ask permission or not.
     */
    public boolean mayRequestPermission(final String permission, RuntimePermissionResult mCallback) {

        /**
         * In this signature of mayRequestPermission, we pass the callback interface object as an
         * argument. We simply need to assign it to our global callback object.
         */
        if(mCallback == null) {
            throw new NullPointerException("You must pass an instance of RuntimePermissionResult.");
        }
        else{
            this.mCallback = mCallback;
        }

        /**
         * If the device version is less than marshmallow, we dont need  to ask
         * for permission. It is already given.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        /**
         * If the permission was already asked before and given, we should not ask it again.
         * This condition checks the same thing.
         */
        if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        /**
         * Here we are using a message to tell the user why we need the permission.
         * This message must be set using setter method before calling mayRequestPermission
         */
        if(activity.shouldShowRequestPermissionRationale(permission)) {

            if(message.isEmpty()) {
                //  User did not set the message.
                message = "The app needs to have "+permission.toString()+" permission to function properly.";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION);
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
        else {
            /**
             * No need for a rationale, directly ask for permission
             */
            activity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION);
        }
        return false;
    }

    /**
     * Calling activity would simply forward the permissionResult object to us.
     * It is then our duty to check and process this request. based on the request_code,
     * size of grant_results, we call the appropriate callback.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            //  Correct request code, we were expecting it.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  permission granted, tell the activity
                mCallback.onPermissionGranted(permissions[0]);
            }
            else {
                //  permission denied, tell the activity
                mCallback.onPermissionDenied(permissions[0]);
            }
        }
        else {
            //  Not our request, its an internal error.
            mCallback.onInternalError("Found invalid REQUEST CODE.");
        }
    }
}
