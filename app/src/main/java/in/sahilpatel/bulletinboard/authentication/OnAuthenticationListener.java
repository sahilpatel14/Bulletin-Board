package in.sahilpatel.bulletinboard.authentication;


import in.sahilpatel.bulletinboard.model.User;

public interface OnAuthenticationListener {

    public static final int AUTH_LOGIN_SUCCESS = 1;
    public static final int AUTH_LOGIN_FAILED = 2;
    public static final int SIGN_IN_FAILED = 3;
    public static final int AUTH_FIREBASE_SUCCESS = 4;
    public static final int AUTH_FIREBASE_FAILED = 5;

    public void onAuthenticationSuccess(String message, int status_code, User user);
    public void onAuthenticationFailure(String message, int status_code);
}
