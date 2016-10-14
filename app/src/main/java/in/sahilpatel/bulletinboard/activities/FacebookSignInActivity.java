package in.sahilpatel.bulletinboard.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import in.sahilpatel.bulletinboard.R;

public class FacebookSignInActivity extends AppCompatActivity {

    public static final int FACEBOOK_SIGN_IN_REQUEST_CODE = 2;
    private static final String TAG = "FacebookSignInActivity";
    
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    private AccessToken accessToken;
    private Profile profile;

    protected String userName;
    protected String pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            FacebookSignInActivity.this.profile = currentProfile;
                            userName = profile.getName();
                            pic_url = profile.getProfilePictureUri(100,100).getPath();
                            Log.d(TAG, "onSuccess: "+userName);
                            Log.d(TAG, "onSuccess: "+pic_url);
                        }
                    };
                }
                else {
                    FacebookSignInActivity.this.profile = Profile.getCurrentProfile();
                    userName = profile.getName();
                    pic_url = profile.getProfilePictureUri(100,100).getPath();
                    Log.d(TAG, "onSuccess: "+userName);
                    Log.d(TAG, "onSuccess: "+pic_url);
                }

                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        FacebookSignInActivity.this.accessToken = currentAccessToken;
                    }
                };
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
