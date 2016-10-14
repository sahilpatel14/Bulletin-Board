package in.sahilpatel.bulletinboard.firebase;


import com.firebase.client.Firebase;

public final class FirebaseApi {

    private static final String baseUrl = "https://bulletin-board-dcee2.firebaseio.com";
    private  static Firebase rootRef;

    public  static Firebase getRootRef(){
        if (rootRef == null) {
            rootRef = new Firebase(baseUrl);
        }
        return rootRef;
    }

    public  static Firebase getRef(String url){
        return new Firebase(baseUrl+url);
    }
}
