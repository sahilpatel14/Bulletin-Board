package in.sahilpatel.bulletinboard;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class    MyApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "caa4ACm2wSiushsVe86Lz5mz1";
    private static final String TWITTER_SECRET = "sT3kztqrxn5lp9QKGNRtuYeDM7wew2OO72jm9BTbNbF91gIndA";


    /**
     * Setting the context for firebase. This code will only be executed
     * once.
     * TODO setting context for facebook oauth.
     */
    public void onCreate(){
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Firebase.setAndroidContext(this.getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "in.sahilpatel.bulletinboard",
//                    PackageManager.GET_SIGNATURES);
//            Log.d("onCreate: ",info.signatures.length+"");
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
    }
}
