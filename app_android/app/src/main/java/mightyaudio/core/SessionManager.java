package mightyaudio.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import mightyaudio.activity.MightyLoginActivity;

public class SessionManager {
	private SharedPreferences pref;
	private Editor editor;
	private Context _context;
	int PRIVATE_MODE = 0;
	public static final String PREF_NAME = "MightyPref";
	public static final String IS_LOGIN = "IsLoggedIn";
	public static final String USER_NAME = "name";

	public static final String MIGHTYFEVICE_ID = "mightydeviceId";

	public static final String STATUSDESC = "statusDesc";
	public static final String STATUSCODE = "statusCode";
	public static final String APITOKEN = "apiToken";
	public static final String BASETOKEN = "baseToken";
	public static final String ACCESSTOKEN = "accessToken";
	public static final String ACCESSTOKENEXPDATE = "accessTokenExpDate";
	public static final String BASETOKENEXPDATE = "baseTokenExpDate";
	public static final String USERSTATUS = "userStatus";
	public static final String USERID = "user_id";
	public static final String PASSWORD ="password";
	public static final String USERINDICATOR = "UserIndicator";


	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	public void createLoginSession(String username, String password,String userIndicator, int user_id,String statusDesc,String statusCode,String apiToken,
								   String baseTken,String accessToken,
								   String accessTokenExpDate,String baseTokenExpDate,String userStatus,String mighty_deviceId){
		editor.putBoolean(IS_LOGIN, true);
		
		editor.putString(USER_NAME, username);
		editor.putString(PASSWORD, password);
		editor.putString(USERINDICATOR,userIndicator);
		editor.putInt(USERID, user_id);
		editor.putString(STATUSDESC,statusDesc);
		editor.putString(STATUSCODE,statusCode);
		editor.putString(APITOKEN,apiToken);
		editor.putString(BASETOKEN,baseTken);
		editor.putString(ACCESSTOKEN,accessToken);
		editor.putString(ACCESSTOKENEXPDATE,accessTokenExpDate);
		editor.putString(BASETOKENEXPDATE,baseTokenExpDate);
		editor.putString(USERSTATUS,userStatus);
		editor.putString(MIGHTYFEVICE_ID,mighty_deviceId);
		editor.commit();
	}	

	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, MightyLoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}

	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(USER_NAME, pref.getString(USER_NAME, null));
		// user email id
		user.put(PASSWORD, pref.getString(PASSWORD, null));
		user.put(USERINDICATOR,pref.getString(USERINDICATOR,null));
		user.put(STATUSDESC,pref.getString(STATUSDESC,null));
		user.put(STATUSCODE,pref.getString(STATUSCODE,null));
		user.put(APITOKEN,pref.getString(APITOKEN,null));
		user.put(BASETOKEN,pref.getString(BASETOKEN,null));

		user.put(ACCESSTOKEN,pref.getString(ACCESSTOKEN,null));
		user.put(ACCESSTOKENEXPDATE,pref.getString(ACCESSTOKENEXPDATE,null));
		user.put(BASETOKENEXPDATE,pref.getString(BASETOKENEXPDATE,null));
		user.put(USERSTATUS,pref.getString(USERSTATUS,null));
		// mighty id
		user.put(MIGHTYFEVICE_ID,pref.getString(MIGHTYFEVICE_ID, null));
		// return user
		return user;
	}
	
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		//LoginManager.getInstance().logOut();
		
		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, MightyLoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}

	public void clearSession(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
	}
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
