package administrator.sahilpatel.com.imageuploader;

/**
 * Created by Administrator on 9/13/2016.
 */

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Uploads images and other files to server.
 * dependency, a library called loopj.
 * com.loopj.android:android-async-http:1.4.9
 */
public class ImageUploader {


    private static final String TAG = "ImageUploader";
    private String BASE_URL = "";

    private ImageUploader() {}

    public ImageUploader(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }


    public void uploadImage(String image_path, String image_name) {

        RequestParams requestParams = new RequestParams();
        requestParams.setForceMultipartEntityContentType(true);
        requestParams.setHttpEntityIsRepeatable(true);

        try {
            requestParams.put("img[]",new File(image_path));
            requestParams.put("img_names[]",image_name);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }


       sendRequest(requestParams);

    }

    public void uploadImage(String image_path, String image_name,FileUploadResult mCallback) {

        if (mCallback == null) {
            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.setForceMultipartEntityContentType(true);
        requestParams.setHttpEntityIsRepeatable(true);

        try {
            requestParams.put("img[]",new File(image_path));
            requestParams.put("img_names[]",image_name);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        sendRequest(requestParams,mCallback);
    }

    public void uploadMultipleImages(List<String> paths, List<String> names) {

        RequestParams requestParams = new RequestParams();
        requestParams.setForceMultipartEntityContentType(true);
        requestParams.setHttpEntityIsRepeatable(true);
        int i = 0;
        for (String path : paths) {
            try {

                requestParams.put("img["+i+"]",new File(path));
                requestParams.put("img_names["+i+"]",names.get(i));
                i++;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sendRequest(requestParams);

    }

    public void uploadMultipleImages(List<String> paths, List<String> names, FileUploadResult mCallback) {

        RequestParams requestParams = new RequestParams();
        requestParams.setForceMultipartEntityContentType(true);
        requestParams.setHttpEntityIsRepeatable(true);

        if (mCallback == null) {
            return;
        }

        int i = 0;
        for (String path : paths) {
            try {

                requestParams.put("img["+i+"]",new File(path));
                requestParams.put("img_names["+i+"]",names.get(i));
                i++;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        sendRequest(requestParams,mCallback);
    }

    private void sendRequest(RequestParams requestParams) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(BASE_URL,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY TAG", "onSuccess: "+response.toString());

                try {
                    JSONObject object = response.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY TAG", "onSuccess: "+response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                //Toast.makeText(getContext(), "File Not Uploaded", Toast.LENGTH_SHORT).show();
               // Log.d("VOLLEY TAG", "onSuccess: "+response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray response) {
                //Toast.makeText(getContext(), "File Not Uploaded", Toast.LENGTH_SHORT).show();
               // Log.d("VOLLEY TAG", "onSuccess: "+response);
            }
        });
    }

    private void sendRequest(RequestParams requestParams, final FileUploadResult mCallback) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(BASE_URL,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mCallback.onSuccess(null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mCallback.onSuccess(null);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                mCallback.onFailure(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray response) {
                mCallback.onFailure(null);
            }
        });
    }
}
