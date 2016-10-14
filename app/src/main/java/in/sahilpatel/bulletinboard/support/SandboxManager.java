package in.sahilpatel.bulletinboard.support;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/28/2016.
 */
public class SandboxManager {

    private static final String TAG = "SandboxManager";
    private File rootFile;
    private Context context;


    public SandboxManager(Builder builder) {

        this.context = builder.context;
        rootFile = Environment.getExternalStorageDirectory();
        //rootFile = context.getExternalFilesDir("BB");
        rootFile = new File(rootFile,builder.directoryName);

        rootFile.mkdirs();
    }


    /**
     * Saves the passed POJO in the internal storage with the
     * given filename. If a file with same name already exists,
     * returns without saving the object.
     *
     * If any property in POJO object is set to null, it will
     * simply be skipped.
     *
     * if any property in POJO is not set, the default value will
     * be stored.
     * This behaviour, courtesy GSON.
     *
     * @param fileName, name of file
     * @param object, The POJO object
     */
    public void savePOJO(String fileName, Object object) {

        if (fileName == null) {
            return;
        }
        if (object == null) {
            return;
        }

        try {
            File file = new File(rootFile+"/"+fileName);

            if (file.exists()){
                //  File already exists. Can not override the file.
                //  Use editObject instead.
                return;
            }
            Gson gson = new Gson();
            String jsonInString = gson.toJson(object);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonInString.getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Saves the passed string in the internal storage with the
     * given filename. If a file with same name already exists,
     * returns without saving the object.
     * @param fileName, name of file
     * @param jsonInString, Json object as string
     */
    public void saveJSON(String fileName, String jsonInString) {

        if (fileName == null || jsonInString == null) {
            //  don't pass null
            return;
        }

        try {
            File file = new File(rootFile+"/"+fileName);

            if (file.exists()){
                //  File already exists. Can not override the file.
                //  Use editObject instead.
                return;
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonInString.getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Saves the passed JSONObject in the internal storage with the
     * given filename. If a file with same name already exists,
     * returns without saving the object.
     * @param fileName
     * @param jsonObject
     */
    public void saveJSON(String fileName, JSONObject jsonObject) {

        if (fileName == null) {
            return;
        }

        if (jsonObject == null) {
            return;
        }

        try {
            File file = new File(rootFile+"/"+fileName);
            if (file.exists()){
                //  File already exists. Can not override the file.
                //  Use editObject instead.
                return;
            }

            String jsonInString = jsonObject.toString();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonInString.getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * removes the file from internal storage. No access specifier required.
     * If the file does not exist, return without performing any operation.
     * @param fileName, name of the file
     */
    public void removeFile(String fileName) {

        if (fileName == null) {
            return;
        }

        try {
            File file = new File(rootFile+"/"+fileName);

            if (file.exists()) {
                //  File exists. Delete it.
                file.delete();
            }
            else {
                throw new FileNotFoundException("file not found.");
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    /**
     * Returns the data stored in the file as a POJO object. The type of which is passed
     * to the method.
     * REMEMBER, unset properties are given default values of the type.
     * if there is no value for a property was saved in file, it will be given null on
     * POJO object.
     *
     * If there is any field in saved file which is not present in POJO object, that
     * field will simple be skipped.
     *
     * @param fileName
     * @param type
     * @return
     */
    public Object getPOJO(String fileName, Class type) {

        if (type == null){
            return null;
        }

        String contents = getFile(fileName);

        if (contents == null) {
            return null;
        }

        try {
            Gson gson = new Gson();
            Object object = type.newInstance();

            if (object == null)
                return null;
            object = gson.fromJson(contents,type);
            return object;
        }
        catch (InstantiationException e){
            //  Not correct pojo
            e.printStackTrace();
        }
        catch (IllegalAccessException e){
            //  Problem in your pojo
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            //  Incorrect POJO passed
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the data stored in the file as a POJO object. The type of which is passed
     * to the method.
     * REMEMBER, unset properties are given default values of the type.
     * if there is no value for a property was saved in file, it will be given null on
     * POJO object.
     *
     * If there is any field in saved file which is not present in POJO object, that
     * field will simple be skipped.
     *
     * @param fileName
     * @param type
     * @return
     */
    public Object[] getPOJOS(String fileName, Class type) {

        if (type == null){
            return null;
        }

        String contents = getFile(fileName);

        if (contents == null) {
            return null;
        }

        try {
            Gson gson = new Gson();
            Object[] object = (Object[]) gson.fromJson(contents,type);
            return object;
        }
        catch (IllegalStateException e) {
            //  Incorrect POJO passed
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the data that is stored as a String object.
     * Albeit any kind.
     * @param fileName
     * @return, String of data stored in file
     */
    public String getDataAsString(String fileName) {

        String content = getFile(fileName);
        if (content == null)
            return "NO data";

        return content;
    }


    /**
     * Helper method, returns data stored in file as a String, if
     * file does not exist or some problem occurs, it returns nll
     * @param fileName
     * @return, String containing data, or null
     */
    private String getFile(String fileName) {

        if (fileName == null){
            return null;
        }

        File file = new File(rootFile+"/"+fileName);

        if(file.exists()) {
            try {

                FileInputStream stream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader buffer = new BufferedReader(reader);
                String line;
                StringBuffer sb = new StringBuffer();
                while((line = buffer.readLine())!= null){
                    sb.append(line);
                }
                return sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "null";

    }

    /**
     * Saves a list of POJO objects in a single file.
     *
     * Skips the object that does not conform to JSON Validation.
     *
     * @param fileName
     * @param objects
     */
    public void savePOJOS(String fileName, List<?> objects) {


        if (fileName == null || objects == null)
            return;

        if (objects.isEmpty())
            return;

        if (fileName.isEmpty())
            return;

        StringBuffer stringBuffer = new StringBuffer();

        File file = new File(rootFile+"/"+fileName);
        stringBuffer.append("[");

        Log.d(TAG, "savePOJOS: "+file);

        String jsonInString = null;
        Gson gson = new Gson();

        for (int i = 0; i < objects.size();i++){
            stringBuffer.append(gson.toJson(objects.get(i)));
            if (i != objects.size()-1)
                stringBuffer.append(",");
        }
        stringBuffer.append("]");
        jsonInString = stringBuffer.toString();

        if (jsonInString == null)
            return;

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonInString.getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public void saveJSONArray(String fileName, String jsonArray) {

        if (fileName == null || jsonArray == null)
            return;

        if (jsonArray.isEmpty())
            return;

        if (fileName.isEmpty())
            return;

        File file = new File(rootFile+"/"+fileName);
        if (file.exists()){
            //  File already exists. Can not override the file.
            //  Use editObject instead.
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonArray.getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addPOJOTo(String fileName, Object data) {

        if (fileName == null) {
            return;
        }
        if (data == null) {
            return;
        }

        try {

            File file = new File(rootFile+"/"+fileName);

            if (file.exists()) {
                String contents = getFile(fileName);

                String temp = contents.substring(0,contents.length()-1);

                Log.d(TAG, "addPOJO: "+temp);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(data);
                contents = temp+","+jsonInString+"]";
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(contents.getBytes());
                fos.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }



    public List<String> getAllFiles() {

        File[] files = rootFile.listFiles();
        List<String> fileNames = new ArrayList<>();
        for(File f : files) {
            fileNames.add(f.getName());
        }
        return fileNames;
    }

    public static class Builder {

        private Context context;
        private String directoryName = "";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder directory(String directoryName) {
            this.directoryName = directoryName;
            return this;
        }

        public SandboxManager build() {
            SandboxManager manager = new SandboxManager(this);
            validateSandboxManager(manager);
            return manager;
        }

        private void validateSandboxManager(SandboxManager manager){

        }
    }
}
































