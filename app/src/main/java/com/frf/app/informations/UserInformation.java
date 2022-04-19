package com.frf.app.informations;

import android.content.Context;
import android.service.autofill.UserData;

import com.frf.app.models.user.UserModel;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.SecurePreferences;

public class UserInformation {

    public static void setUserData(UserModel userData) {
        UserInformation.userData = userData;
    }

    private static UserModel userData;

    public static UserModel getUserData() {
        if(userData == null){
            userData = new UserModel();
        }
        return userData;
    }

    private static String userId = "";
    private static int sessionId;

    public static int getSessionId() {
        return sessionId;
    }

    public static void setSessionId(int sessionId) {
        UserInformation.sessionId = sessionId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        UserInformation.userId = userId;
    }

    public static void ClearUserData(Context context){
        userData = new UserModel();
        userId = "";
        SecurePreferences prefs = new SecurePreferences(context, CONSTANTS.SHARED_PREFS_KEY_FILE, CONSTANTS.SHARED_PREFS_SECRET_KEY, true);
        prefs.clear();
    }

}