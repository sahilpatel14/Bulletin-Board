package in.sahilpatel.bulletinboard.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 9/12/2016.
 */
public class MyCacheManager {

    private final String TAG = MyCacheManager.class.getSimpleName();
    AppCompatActivity activity;

    public MyCacheManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void saveThreadNames(Map<String,String> thread_names) {
        String FILENAME = "thread_names.txt";

        try {

            FileOutputStream fos = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            Object[] keys = thread_names.keySet().toArray();
            String name;

            for(Object key : keys) {
                name = thread_names.get(key);
                String toStore = key+"-"+name;
                Log.d(TAG, "saveThreadNames: saving "+toStore);
                fos.write(toStore.getBytes());
            }
            fos.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getThreadNames() {
        String FILENAME = "thread_names.txt";
        StringBuffer sb = new StringBuffer();
        try {

            FileInputStream fis = new FileInputStream(new File(activity.getCacheDir(), FILENAME));


            int content;
            while ((content = fis.read()) != -1) {
                // convert to char and display it
                Log.d(TAG, "getThreadNames: retrieving "+content);
                sb.append((char) content);
            }

            return sb.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }


}
