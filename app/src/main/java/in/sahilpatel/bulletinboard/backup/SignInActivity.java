package in.sahilpatel.bulletinboard.backup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Iterator;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.authentication.GoogleAuth;
import in.sahilpatel.bulletinboard.authentication.OnAuthenticationListener;
import in.sahilpatel.bulletinboard.authentication.TwitterAuth;
import in.sahilpatel.bulletinboard.firebase.FirebaseUserApi;
import in.sahilpatel.bulletinboard.firebase.MySharedPreferences;
import in.sahilpatel.bulletinboard.model.User;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    /**
     * User has not Signed in or Logged in. This activity logs him in
     * by either Google Auth or Facebook Auth. Adding new Authorization ways
     * is fairly easy as we distribute authentication process into separate
     * classes.
     */
    private final String TAG = SignInActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    /**
     * Authorization object for GoogleAuth. It will handle the google Authentication process.
     * Any other authentication objects will be added below.
     */
    private GoogleAuth mGoogleAuth;
    private TwitterAuth mTwitterAuth;


    //And it would be added automatically while the configuration
    private static final String TWITTER_KEY = "YOUR TWITTER KEY";
    private static final String TWITTER_SECRET = "YOUR TWITTER SECRET";

    private TwitterLoginButton twitterLoginButton;
    private FirebaseUserApi mFirebaseUserApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseUserApi = new FirebaseUserApi();
        progressDialog = new ProgressDialog(this);

        //Initializing twitter login button
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLogin);
        signInWithTwitter();

        /**
         * setting the onClick for Google Login button. When clicked start SignIn process
         */
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signInWithGoogle();
            }
        });

    }

    /**
     * Create a new object for GoogleAuth and call the signIn method.
     * pass OnAuthenticationListener anonymous class that would be called
     * by GoogleAuth.
     */
    private void signInWithGoogle() {

        progressDialog.setMessage("Signing in...");
        progressDialog.show();
        mGoogleAuth = new GoogleAuth(SignInActivity.this);
        mGoogleAuth.signIn(new OnAuthenticationListener() {
            @Override
            public void onAuthenticationSuccess(String message, int status_code, final User user) {
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();

                mFirebaseUserApi.userExists(user, new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            Log.d(TAG, "onDataChange: "+"user already exists."+dataSnapshot.getKey());
                            Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                            dataSnapshot = iter.next();
                            final String key = dataSnapshot.getKey();
                            new MySharedPreferences(SignInActivity.this).saveUserKey(key);
                            progressDialog.setMessage("Fetching data...");
                            setResult(RESULT_OK);
                        }
                        else{

                            String key = mFirebaseUserApi.addUser(user);
                            Log.d(TAG, "onDataChange: "+" new user. Adding to database."+key);
                            new MySharedPreferences(SignInActivity.this).saveUserKey(key);
                            setResult(RESULT_OK);
//                            openHomeActivity();
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d(TAG, "onCancelled: "+firebaseError.getMessage());
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(String message, int status_code) {
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithTwitter() {

        progressDialog.setMessage("Signing in....");
        new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        mTwitterAuth = new TwitterAuth(this,twitterLoginButton);
        mTwitterAuth.signIn(new OnAuthenticationListener() {

            @Override
            public void onAuthenticationSuccess(String message, int status_code,final User user) {
                progressDialog.show();
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                mFirebaseUserApi.userExists(user, new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            Log.d(TAG, "onDataChange: "+"user already exists in FireBase."+dataSnapshot.getKey());
                            Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                            dataSnapshot = iter.next();
                            final String key = dataSnapshot.getKey();
                            new MySharedPreferences(SignInActivity.this).saveUserKey(key);
                            progressDialog.setMessage("Fetching data...");
                            setResult(RESULT_OK);
//                            startDownloadService(key);
//                            openHomeActivity();
                        }
                        else{

                            String key = mFirebaseUserApi.addUser(user);
                            Log.d(TAG, "onDataChange: "+" new user. Adding to database."+key);
                            new MySharedPreferences(SignInActivity.this).saveUserKey(key);
                            setResult(RESULT_OK);
//                            openHomeActivity();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d(TAG, "onCancelled: "+firebaseError.getMessage());
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(String message, int status_code) {
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }




    /**
     * Returned from response of Authentication. Based on requestCode value,
     * we pass it to particular Auth Handling class.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case GoogleAuth.GOOGLE_SIGN_IN_REQUEST_CODE :
                mGoogleAuth.onActivityResult(requestCode,resultCode,data);
                                                                break;

            case TwitterAuth.TWITTER_SIGN_IN_REQUEST_CODE :
                progressDialog.show();
                mTwitterAuth.onActivityResult(requestCode,resultCode,data);

        }
        Log.d(TAG, "onActivityResult: "+requestCode);
        mTwitterAuth.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * In case the system is not able to connect to GoogleAPI
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: connection failed "+connectionResult.getErrorCode());

    }
}
