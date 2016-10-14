package in.sahilpatel.bulletinboard.authentication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;

public class TwitterAuth {

    /**
     * This class handles the Twitter authentication with Firebase part.
     * It required parent activity object to function. The signIn method starts the
     * authentication process. An Interface called OnAuthenticationListener is passed to
     * signIn method. This interface is used to pass messages back to calling activity.
     */

    /**
     * TAG and a status code for onActivityResult.
     * Note * status code must be unique for every social intergration platform type.
     */

    public static final String TAG = TwitterAuth.class.getSimpleName();
    public static final int TWITTER_SIGN_IN_REQUEST_CODE = 140;
    private final String base_img_url = "";
    private final String provider = "TWITTER";

    /**
     * Variables required for twitter Integration. Unique to twitter integration
     */
    private FirebaseAuth mFirebaseAuth;
    TwitterLoginButton twitterLoginButton;
    AppCompatActivity activity;

    /**
     * Callback to send back messages to calling activity.
     */
    private OnAuthenticationListener mCallback;

    public TwitterAuth(AppCompatActivity activity, TwitterLoginButton twitterLoginButton) {

        /**
         * No work other than initialization is kept in this constructor. All the
         * heavy loading is done by signIn method
         */
        this.activity = activity;
        this.twitterLoginButton = twitterLoginButton;

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Called by the activity requiring authentication.
     * Starts a new intent on that activity for result.
     * The onActivityResult method is received by the activity.
     * The activity will then forward it to this class.
     * @param mCallback
     */
    public void signIn(OnAuthenticationListener mCallback) {

        this.mCallback = mCallback;

        /**
         * The callback is required. Twitter button does not have any explicit OnClick
         * listener.
         */
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                /**
                 * Login successful. Start process to retrieve user profile picture.
                 */
                Log.d(TAG, "success: (Twitter) "+"Login success");
                login();
            }

            @Override
            public void failure(TwitterException exception) {
                /**
                 * Login failure. User clicked cancel or something.
                 */
                Log.d("TwitterKit", "Login with Twitter failed", exception);
            }
        });

    }

    /**
     * Returned from the activity with no touch ups.
     * We simply pass the call to twitterLoginButton. No processing happens here.
     * Send appropriate callback.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * All the heavy loading in Twitter authentication happens in this method.
     * The method is only called when the user has successfully signed in. Here we retrieve
     * user's profile picture.
     *
     * Firstly we create a Twitter session, it will be used to get auth token and key (used by firebase).
     * later we call verifyCredentials to get additional information about the user.
     *
     */
    public void login() {

        //  Used by fire base to gain token and key.
        final TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();


        /**
         * Calling verifyCredentials(). The result is received in our callback.
         */
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        Call<User> user = apiClient.getAccountService().verifyCredentials(false,false);


        user.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {

                /**
                 * Received user data object. Proceed to Firebase Authentication
                 */
                firebaseAuthWithTwitter(session,userResult);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterLogin", "failure: "+exception.getMessage());
                mCallback.onAuthenticationFailure(exception.getMessage(),OnAuthenticationListener.SIGN_IN_FAILED);
            }
        });
    }


    /**
     * Till now we have successfully logged the user to his account. We have his authToken in
     * the session object, however we need to pass this token to Firebase.
     * Also we must store user info, name, profile pic that we will need for app's functioning.
     * @param session, contains auth token
     * @param userResult, contains userResult
     */
    private void firebaseAuthWithTwitter(final TwitterSession session, final Result<User> userResult) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: "+"(Twitter) authentication process completed successfully");
                            mCallback.onAuthenticationSuccess("You have now authenticated with Twitter.",
                                    OnAuthenticationListener.AUTH_FIREBASE_SUCCESS,getUser(userResult));
                        }
                        else {
                            Log.d(TAG, "onComplete: "+"(Twitter) could not authenticate with firebase.");
                            mCallback.onAuthenticationFailure("Failed to authenticate with firebase",
                                    OnAuthenticationListener.AUTH_FIREBASE_FAILED);
                        }
                    }
                });
    }

    /**
     * Takes the account object and fetches name and image url from it.
     * This data is then put to User POJO and returned back.
     * @param userResult
     * @return User
     */
    private in.sahilpatel.bulletinboard.model.User getUser(Result<User> userResult){
        String name = userResult.data.name;
        String image_url = base_img_url+userResult.data.profileImageUrl;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return new in.sahilpatel.bulletinboard.model.User(name,image_url,user.getUid(),provider);
    }
}

