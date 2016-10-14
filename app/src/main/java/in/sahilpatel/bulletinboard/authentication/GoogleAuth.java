package in.sahilpatel.bulletinboard.authentication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.model.User;

public class GoogleAuth{

    /**
     * This class handles the Google authentication with Firebase part.
     * It required parent activity object to function. The signIn method starts the
     * authentication process. An Interface called OnAuthenticationListener is passed to
     * signIn method. This interface is used to pass messages back to calling activity.
     */

    /**
     * TAG and a status code for onActivityResult.
     * Note * status code must be unique for every social intergration platform type.
     */
    private final String TAG = GoogleAuth.class.getSimpleName();
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1;
    private final String base_img_url = "https://lh3.googleusercontent.com";
    private final String provider = "GOOGLE";

    /**
     * variables unique to GoogleAuthProcess. FirebaseAuth has global visibility
     * in the application.
     */
    private FirebaseAuth mFirebaseAuth;
    private AppCompatActivity activity;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Callback to send back messages to calling activity.
     */
    private OnAuthenticationListener mCallback;


    public GoogleAuth(AppCompatActivity activity) {

        this.activity = activity;
        /**
         * Required to create GoogleApiClient object. We mention any additional request that we might want.
         * ex : email. We also specify the client id in gso object.
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient  = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity/*The activity*/,
                        (GoogleApiClient.OnConnectionFailedListener) activity/*OnConnectionFailedListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Called by the activity requiring authentication.
     * Starts a new intent on that activity for result.
     * The onActivityResult method is received by the activitiy.
     * The activity will then forward it to thisclass.
     * @param mCallback
     */
    public void signIn(OnAuthenticationListener mCallback) {

        this.mCallback = mCallback;
        Intent googleIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(googleIntent,GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    /**
     * Returned from the activity with no touchups. On success, we pass the account to
     * authenticate with Firebase , which is the second part. If it fails, we will notify the
     * calling activity about the same.
     * The third case is Sign in failure, which happens when the credentials are wrong or any such problem.
     * Send appropriate callback.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                Log.d(TAG, "onActivityResult: "+"(Google) Sign in process successful.");

                GoogleSignInAccount account = result.getSignInAccount();        //  Contains details about user. Save required information
                firebaseAuthWithGoogle(account);                                //  Now authenticate with firebase

            }
            else{
                //  Login with Google failed
                Log.d(TAG, "onActivityResult: "+"(Google) Sign in was successful but Authentication failed..");
                mCallback.onAuthenticationFailure("Google sign in was successful but Authentication failed",OnAuthenticationListener.AUTH_LOGIN_FAILED);
            }
        }
        else {
            Log.d(TAG, "onActivityResult: "+"(Google) Sign in failed.");
            mCallback.onAuthenticationFailure("Google Sign in failed",OnAuthenticationListener.SIGN_IN_FAILED);
        }
    }

    /**
     * Till now we have successfully logged the user to his account. We have his authToken in
     * the account object, however we need to pass this token to Firebase.
     * Also we must store user info, name, profile pic that we will need for app's functioning.
     * @param account
     */
    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: "+"(Google) authentication process completed successfully");
                            mCallback.onAuthenticationSuccess("You have now authenticated with Google.",
                                    OnAuthenticationListener.AUTH_FIREBASE_SUCCESS,getUser(account));
                        }
                        else {
                            Log.d(TAG, "onComplete: "+"(Google) could not authenticate with firebase.");
                            mCallback.onAuthenticationFailure("Failed to authenticate with firebase",
                                    OnAuthenticationListener.AUTH_FIREBASE_FAILED);
                        }
                    }
                });
    }

    /**
     * Takes the account object and fetches name and image url from it.
     * This data is then put to User POJO and returned back.
     * @param account
     * @return User
     */
    private User getUser(GoogleSignInAccount account){
        String name = account.getDisplayName();
        account.getServerAuthCode();
        String image_url = base_img_url+account.getPhotoUrl().getPath();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return new User(name,image_url,user.getUid(),provider);
    }
}
