package in.sahilpatel.bulletinboard.authentication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.sahilpatel.bulletinboard.model.User;


public class FacebookAuth {

    private final String TAG = FacebookAuth.class.getSimpleName();
    public static final int FACEBOOK_SIGN_IN_REQUEST_CODE = 2;
    private final String base_img_url = "graph.facebook.com";
    private final String provider = "FACEBOOK";

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private OnAuthenticationListener mCallback;

    private ProfileTracker mProfileTracker;
    private AccessTokenTracker mAccessTokenTracker;

    private AccessToken mAccessToken;
    private Profile mProfile;

    private AppCompatActivity activity;
    private FirebaseAuth mFirebaseAuth;

    public FacebookAuth(AppCompatActivity activity,
                        LoginButton loginButton, CallbackManager callbackManager) {

        this.activity = activity;
        mFirebaseAuth = FirebaseAuth.getInstance();

        this.loginButton = loginButton;
        this.callbackManager = callbackManager;

    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void signIn(OnAuthenticationListener mCallback) {
        this.mCallback = mCallback;

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            FacebookAuth.this.mProfile = currentProfile;
                        }
                    };
                }
                else {
                    FacebookAuth.this.mProfile = Profile.getCurrentProfile();
                }
                mAccessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        FacebookAuth.this.mAccessToken = currentAccessToken;
                    }
                };

                mAccessToken = loginResult.getAccessToken();
                firebaseAuthWithFacebook(mAccessToken);

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


    public void firebaseAuthWithFacebook(AccessToken token) {

        Log.d(TAG, "firebaseAuthWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: "+"(Facebook) authentication process completed successfully");
                            mCallback.onAuthenticationSuccess("You have now authenticated with Facebook.",
                                    OnAuthenticationListener.AUTH_FIREBASE_SUCCESS,getUser());
                        }
                        else {

                            Log.d(TAG, "onComplete: "+"(Facebook) could not authenticate with firebase.");
                            Log.d(TAG, "onComplete: "+task.getException());
                            mCallback.onAuthenticationFailure("Failed to authenticate with firebase",
                                    OnAuthenticationListener.AUTH_FIREBASE_FAILED);
                        }
                    }
                });
    }


    /**
     * Takes the account object and fetches name and image url from it.
     * This data is then put to User POJO and returned back.
     * @return User
     */
    private User getUser(){
        String name = mProfile.getName();
        String image_url = ""+mProfile.getProfilePictureUri(200,200);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return new User(name,image_url,user.getUid(),provider);
    }
}
