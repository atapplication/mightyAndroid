package mightyaudio.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpotifySessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "SpotifyPref";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String USER_NAME = "name";
    public static final String ACCESSTOKEN = "accessToken";
    public static final String REFRESHTOKEN = "refreshToken";
    public static final String ACCESSTOKENEXPTIME = "accessTokenExpTime";
    public static final String USERID = "user_id";
    public static final String CURRENTTIME = "currenttime";


    public SpotifySessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createSpotifySession(String username,long user_id,String accessToken,String refreshTokne, String accessTokenExpTime,long current_time){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USER_NAME, username);
        editor.putLong(USERID, user_id);
        editor.putString(REFRESHTOKEN, refreshTokne);
        editor.putString(ACCESSTOKEN,accessToken);
        editor.putString(ACCESSTOKENEXPTIME,accessTokenExpTime);
        editor.putLong(CURRENTTIME,current_time);
        editor.commit();
    }


    public void clearSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

}
