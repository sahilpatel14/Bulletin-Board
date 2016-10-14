package in.sahilpatel.bulletinboard.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import in.sahilpatel.bulletinboard.model.NewThread;


public class MySharedPreferences {

    private final String TAG = MySharedPreferences.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String USER_KEY_ID = "user_key";
    private final String USER_NAME = "user_name";
    private final String THREAD_KEY_ID = "thread_key";
    private final String SHARED_PREF_FILE_NAME = "bulletinBoardPref";


    public MySharedPreferences(AppCompatActivity activity) {
        sharedPreferences = activity.getSharedPreferences(SHARED_PREF_FILE_NAME,Context.MODE_PRIVATE);
    }

    public MySharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE_NAME,Context.MODE_PRIVATE);
    }

    /**
     * Saves user's key into shared preferences file. Only one key
     * can be stored at a time. The last key will be overridden.
     * @param key
     */
    public void saveUserKey(String key) {
        Log.d(TAG, "saveUserKey: "+"saving user key "+key);
        editor = sharedPreferences.edit();
        editor = editor.putString(USER_KEY_ID,key);
        editor.commit();
    }

    private void saveUserName(String name) {
        Log.d(TAG, "saveUserName: "+"saving user name "+name);
        editor = sharedPreferences.edit();
        editor = editor.putString(USER_NAME,name);
        editor.commit();
    }

    public String getUserName() {
        String key_id = USER_NAME;
        String name = sharedPreferences.getString(key_id,"No key");
        Log.d(TAG, "getUserName: "+"fetching user name"+name);
        return name;
    }

    /**
     * Returns key value stored in sharedPref file.
     * Default value it returns is empty string
     */
    public String getUserKey() {
        String key_id = USER_KEY_ID;
        String key = sharedPreferences.getString(key_id,"No key");
        Log.d(TAG, "getUserKey: "+"fetching user key "+key);
        return key;
    }

    public List<NewThread> getThreadNames() {
        String key_id = THREAD_KEY_ID;
        List<NewThread> threads = new ArrayList<>();
        String data = sharedPreferences.getString(key_id,"");

        Log.d(TAG, "getThreadNames: "+data);

        if (data == null || data.isEmpty()) {
            return threads;
        }

        StringTokenizer st = new StringTokenizer(data,";");
        StringTokenizer st1;
        while (st.hasMoreElements()) {
            String pair = st.nextToken();
            st1 = new StringTokenizer(pair,":");
            while (st1.hasMoreElements()) {
                threads.add(new NewThread(st1.nextToken(),st1.nextToken()));
            }
        }
        return threads;
    }

    public void saveThreadNames(Map<String,String> thread_names) {
        editor = sharedPreferences.edit();

        Object[] keys = thread_names.keySet().toArray();
        String name;
        StringBuffer sb = new StringBuffer();
        String toStore;
        int i = 0;
        for(Object key : keys) {
            name = thread_names.get(key);
            if(i == keys.length-1) {
                toStore = key+":"+name;
            }
            else {
                toStore = key+":"+name+";";
            }
            sb.append(toStore);
            i++;
        }
        Log.d(TAG, "saveThreadNames: "+sb.toString());
        editor = editor.putString(THREAD_KEY_ID,sb.toString());
        editor.commit();
    }

    public void deleteSavedData() {

        Log.d(TAG, "deleteSavedData: "+"logging out");
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
