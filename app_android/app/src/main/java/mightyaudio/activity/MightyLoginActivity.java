package mightyaudio.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;

import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

public class MightyLoginActivity extends RootActivity implements View.OnClickListener ,ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = MightyLoginActivity.class.getSimpleName();

    private TextView forgot_password,create_account,text_header_username,txt_password_header;
    public TextView text_invalid_password;
    private EditText edit_username,edit_userpass;
    private Button btn_login;
    private SessionManager session;
    private SharedPreferences pref;
    private GlobalClass globalClass= GlobalClass.getInstance();
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private String fbaccessToken;
    private TextView txt_back,text_title,txt_save,text_mighty_setup,text_or;
    private Typeface custom_font_bold,custom_font_light;
    private CheckBox chech_box;
    private String user_id,user_password;
    private String OsName, DeviceOSVersion,DeviceType,device_id;
    private Activity activity =this;
    private boolean  isConnected;
    private BroadcastReceiver broadcast_reciever;
    private IntentFilter intentFilter ;
    public SharedPreferences lastloginpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate()");
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        setContentView(R.layout.activity_mighty_login);
        globalClass.marshMellowLocationPermission(this);
        session = new SessionManager(getApplicationContext());
        //Facebook btn initilize
        loginButton = (LoginButton)findViewById(R.id.login_button);
        closeActivity();


        lastloginpref = getSharedPreferences(globalClass.last_login_pref, Context.MODE_PRIVATE);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if(newToken != null){
                    fbaccessToken = newToken.getToken();
                    Log.e(TAG,"if o Current");
                }else{
                    globalClass.stoptimertask();
                    session.logoutUser();
                    Log.e(TAG,"else logout");
                }

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        pref = getSharedPreferences(session.PREF_NAME , Context.MODE_PRIVATE);
        //disconnectFromFacebook(); //making Logout
        LoginManager.getInstance().logOut();

        AccessToken.setCurrentAccessToken(null);

        edit_username =(EditText)findViewById(R.id.edit_username);
        edit_userpass =(EditText)findViewById(R.id.edit_userpass);
        btn_login = (Button)findViewById(R.id.btn_login);
        chech_box = (CheckBox) findViewById(R.id.chech_box);
        txt_back = (TextView) findViewById(R.id.txt_cancel);
        txt_save = (TextView) findViewById(R.id.txt_save);
        text_title = (TextView)findViewById(R.id.text_title);
        forgot_password = (TextView)findViewById(R.id.text_forgot_password);
        create_account = (TextView)findViewById(R.id.text_create_account);
        text_header_username = (TextView)findViewById(R.id.text_header_username);
        txt_password_header = (TextView)findViewById(R.id.txt_password_header);
        text_invalid_password = (TextView)findViewById(R.id.text_invalid_password);
        text_or = (TextView)findViewById(R.id.text_or);
        text_mighty_setup = (TextView)findViewById(R.id.text_mighty_setup);
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        create_account.setPaintFlags(create_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_mighty_setup.setPaintFlags(create_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgot_password.setOnClickListener(this);
        create_account.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_back.setOnClickListener(this);
        text_mighty_setup.setOnClickListener(this);

        txt_back.setVisibility(View.INVISIBLE);
        txt_save.setVisibility(View.INVISIBLE);
        txt_back.setText("< BACK");
        text_title.setText(getString(R.string.mighty_login));
        if(globalClass.userActivityMode == 1)
            txt_back.setVisibility(View.VISIBLE);

        edit_username.setTypeface(custom_font_light);
        edit_userpass.setTypeface(custom_font_light);
        text_mighty_setup.setTypeface(custom_font_light);
        text_or.setTypeface(custom_font_light);
        txt_back.setTypeface(custom_font_light);
        text_title.setTypeface(custom_font_bold);

        chech_box.setTypeface(custom_font_light);
        btn_login.setTypeface(custom_font_light);
        loginButton.setTypeface(custom_font_light);
        text_invalid_password.setTypeface(custom_font_light);
        text_header_username.setTypeface(custom_font_light);
        txt_password_header.setTypeface(custom_font_light);
        text_invalid_password.setTypeface(custom_font_light);
        forgot_password.setTypeface(custom_font_light);
        create_account.setTypeface(custom_font_light);

        text_invalid_password.setVisibility(View.INVISIBLE);

        /**************************** Password Show  ****************************/
        chech_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    edit_userpass.setInputType(129);
                    edit_userpass.setTypeface(custom_font_light);

                } else {
                    edit_userpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_userpass.setTypeface(custom_font_light);
                }
            }
        });
        /*******************************************************************************/

        edit_username.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                edit_username.setBackgroundColor(Color.parseColor("#ffffff"));
                text_invalid_password.setVisibility(View.INVISIBLE);
                if (edit_username.getText().toString().matches("^ ") )
                    edit_username.setText("");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        edit_userpass.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                edit_username.setBackgroundColor(Color.parseColor("#ffffff"));
                if (edit_userpass.getText().toString().matches("^ ") )
                    edit_userpass.setText("");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                final Profile profile = Profile.getCurrentProfile();
                Log.e(TAG,"calling "+accessToken);
                //displayMessage(profile);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                loginButton.setText(getString(R.string.authontication_facebook));
                                device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                DeviceOSVersion = Build.VERSION.RELEASE;
                                DeviceType = getDeviceName();
                                Field[] fields = Build.VERSION_CODES.class.getFields();
                                OsName = fields[Build.VERSION.SDK_INT + 1].getName();
                                JSONObject jsonObject =  new JSONObject();
                                try {
                                    Log.e(TAG,"Face Book Detail "+object.getString("name")+" "+object);
                                    String email = "NoEmail";
                                    if(!object.isNull("email"))
                                        email = object.getString("email");
                                    jsonObject.put("UserIndicator","F");
                                    jsonObject.put("UserName", object.getString("name"));
                                    if(fbaccessToken != null)
                                    jsonObject.put("Password", fbaccessToken.toString().trim());
                                    jsonObject.put("EmailID", email);
                                    jsonObject.put("Gender", object.getString("gender"));
                                    jsonObject.put("FacebookID", object.getString("id").trim());
                                    jsonObject.put("Age", "");

                                    jsonObject.put("DeviceModel","Android");
                                    jsonObject.put("DeviceID",device_id);
                                    jsonObject.put("DeviceName","Android");
                                    jsonObject.put("DeviceOS",OsName);
                                    jsonObject.put("DeviceOSVersion",DeviceOSVersion);
                                    jsonObject.put("DeviceType",DeviceType);
                                    loginButton.setTag("fb_login");
                                    Log.e(TAG,"LoginActivity"+jsonObject);
                                    if(globalClass.mighty_ble_device != null)
                                        globalClass.bleDisconnect();
                                    if(fbaccessToken != null && object.getString("name") != null)
                                    globalClass.refreshBaseTokenLogin(jsonObject,1,activity);
                                    else globalClass.toastDisplay("Facebook login fail please try again");
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"error");
            }
        });
        if((lastloginpref.getString("LAST_USERNAME","") != "") && (lastloginpref.getString("LAST_PASSWORD","") != "")){
            edit_username.setText(lastloginpref.getString("LAST_USERNAME",""));
            edit_userpass.setText(lastloginpref.getString("LAST_PASSWORD",""));
        }

        edit_username.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[\\x00-\\x7F]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });

        edit_userpass.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[\\x00-\\x7F]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button :
                Log.e(TAG,"facebook Trigger");
                break;
            case R.id.btn_login :
                globalClass.showProgressBar(MightyLoginActivity.this);
                isConnected = ConnectivityReceiver.isConnected();
                Log.e(TAG,"isConnected login mighty "+isConnected);
                if(isConnected){
                   new Handler().post(returnRes);
                   // Thread thread = new Thread(null, internetThread);
                   // thread.start();
                }else{
                    globalClass.dismissProgressBar();
                    globalClass.toastDisplay(getString(R.string.check_internet));
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        globalClass.dismissProgressBar();
                    }
                },15000);

                break;
            case R.id.text_forgot_password :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Intent intent = new Intent(this,ForgotPasswordActivity.class);
                    startActivity(intent);
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_create_account :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    startActivity(new Intent(getApplicationContext(),MightyCreateAccountActivity.class));
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.text_mighty_setup:
                globalClass.userActivityMode = 1;
                Intent setup_intent= new Intent(this,SetUpActivity.class);
                startActivity(setup_intent);
                break;
        }
    }

    private Runnable progressBarStop = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG," isConnected progressBarStop"+isConnected);
            globalClass.dismissProgressBar();
            globalClass.toastDisplay(getString(R.string.check_internet));
        }
    };


    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG," isConnected value before"+isConnected);
            //if(isConnected){
                Log.e(TAG," isConnected value after"+isConnected);
                globalClass.Status = false;
                LoginManager.getInstance().logOut();
                globalClass.stoptimertask();
                //session.logoutUser();
                if(globalClass.mighty_ble_device != null)
                    globalClass.bleDisconnect();
                user_id = edit_username.getText().toString();
                user_password = edit_userpass.getText().toString();
                if(TextUtils.isEmpty(user_id)){
                    globalClass.dismissProgressBar();
                    edit_username.setBackground(getResources().getDrawable(R.drawable.error_border));
                    edit_username.requestFocus();
                }else if(TextUtils.isEmpty(user_password)){
                    globalClass.dismissProgressBar();
                    edit_userpass.setBackground(getResources().getDrawable(R.drawable.error_border));
                    edit_userpass.requestFocus();
                }else {
                    JSONObject jsonObject =  new JSONObject();
                    try {
                        jsonObject.put("UserIndicator","L");
                        jsonObject.put("UserName",user_id.trim());
                        jsonObject.put("Password",user_password.trim());
                        hideSoftKeboard();
                        globalClass.refreshBaseTokenLogin(jsonObject,1,activity);
                        if(globalClass.userActivityMode == 1)
                            txt_back.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        LoginManager.getInstance().logOut();
        registerReceiver(broadcast_reciever, intentFilter);
        globalClass.setConnectivityListener(this);
        if(pref.getBoolean(SessionManager.IS_LOGIN, false)){
            Log.e(TAG,"USERINDICATOR "+pref.getString(SessionManager.USERINDICATOR,""));
            if(!pref.getString(SessionManager.USERINDICATOR,"").equals("F")){
                String username = pref.getString(SessionManager.USER_NAME,"");
                String password = pref.getString(SessionManager.PASSWORD,"");
                edit_username.setText(username);
                edit_userpass.setText(password);
            }else{
                edit_username.setText("");
                edit_userpass.setText("");
            }
            txt_back.setVisibility(View.VISIBLE);
        }
        loginButton.setText(getString(R.string.com_facebook_loginview_log_in_button_long));

    }
    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    //Getting Device type or model
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"onNewIntent call");
        if(pref.getBoolean(SessionManager.IS_LOGIN, false)){
            if(!pref.getString(SessionManager.USERINDICATOR,"").equals("F")){
                String username = pref.getString(SessionManager.USER_NAME,"");
                String password = pref.getString(SessionManager.PASSWORD,"");
                edit_username.setText(username);
                edit_userpass.setText(password);
            }else{
                edit_username.setText("");
                edit_userpass.setText("");
            }
        }else{
            //btn_login.setText(getString(R.string.login));
            //btn_login.setTag("login");
            txt_back.setVisibility(View.INVISIBLE);


        }
    }

    private void closeActivity(){
        intentFilter = new IntentFilter("finish_activity");
        broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
    }

    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast_reciever);
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.e(TAG,"Network check");
        globalClass.showSnack(isConnected,findViewById(R.id.activity_mighty_login));
    }
}
