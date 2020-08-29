package com.example.zaikafoodyville;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    //variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FullName = "fullName";
    public static final String KEY_Email = "email";
    public static final String KEY_Phone = "phone";
    public static final String KEY_Password = "password";
    public static final String KEY_ImageUrl = "imageUrl";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void createLoginSession(String name, String email, String phoneNo, String password, String imageUrl) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FullName, name);
        editor.putString(KEY_Email, email);
        editor.putString(KEY_Phone, phoneNo);
        editor.putString(KEY_Password, password);
        editor.putString(KEY_ImageUrl, imageUrl);

        editor.commit();
    }

    public void createLoginSession(String name, String password) {

        editor.putString(KEY_FullName, name);
        editor.putString(KEY_Password, password);
        editor.commit();
    }

/*
public void createLoginSessionPass(String password) {

        editor.putString(KEY_Password, password);
        editor.commit();
    }
 */


    public void updateImageUrl(String imageUrl) {

        editor.putString(KEY_ImageUrl, imageUrl);
        editor.commit();
    }

    public HashMap<String, String> getUserDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_FullName, sharedPreferences.getString(KEY_FullName, null));
        userData.put(KEY_Email, sharedPreferences.getString(KEY_Email, null));
        userData.put(KEY_Phone, sharedPreferences.getString(KEY_Phone, null));
        userData.put(KEY_Password, sharedPreferences.getString(KEY_Password, null));
        userData.put(KEY_ImageUrl, sharedPreferences.getString(KEY_ImageUrl, null));

        return userData;
    }

    public boolean checkLogin() {
        if (sharedPreferences.getBoolean(IS_LOGIN, false)) {
            return true;
        } else
            return false;
    }

    public void logOutUserFromSession() {
        editor.clear();
        editor.commit();
    }

}
