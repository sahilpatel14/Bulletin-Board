package administrator.sahilpatel.com.imageuploader;

import org.json.JSONArray;

/**
 * Created by Administrator on 9/13/2016.
 */
public interface FileUploadResult {

    void onSuccess(JSONArray result);
    void onFailure(String message);
}
