package in.sahilpatel.runtimepermissions;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Sahil on 9/11/2016.
 */
public class RuntimePermissions {

    private AppCompatActivity activity;
    private final int REQUEST_READ_CONTACTS = 1;
    private RuntimePermissionResult mCallback;
    private String message;

    public RuntimePermissions(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean mayRequestPermission(String permission) {


        try {

            mCallback = (RuntimePermissionResult)this.activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException("You must implement RuntimePermissionResult");
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if(activity.shouldShowRequestPermissionRationale(permission)) {

        }
        else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public boolean mayRequestPermission(String permission, RuntimePermissionResult mCallback) {

        this.mCallback = mCallback;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (activity.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if(activity.shouldShowRequestPermissionRationale(permission)) {

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        else {
            activity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCallback.onPermissionGranted();
            }
        }
    }
}


