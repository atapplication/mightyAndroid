package mightyaudio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;

import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;
import mightyaudio.receiver.ConnectivityReceiver;


public class MightyCreateAccountActivity extends RootActivity implements View.OnClickListener{
    public static final String TAG = MightyCreateAccountActivity.class.getSimpleName();
    TextView cancel, save, txt_field,text_title,txt_cancel,txt_save,text_username_error,text_password_error,text_renter_password_error,text_email_error;
    EditText et_user,  et_pass, et_repass, et_email,et_age;
    private Spinner spinner_gender;
    Typeface custom_font_light,custom_font_bold;
    private String str_username,str_pass,str_repass,str_email,str_gender,str_age,device_id;
    private String OsName, DeviceOSVersion,DeviceType;
    private CheckBox show_password;
    //private ProgressDialog progressDialog;
    private Bundle bundle;
    private  int flag;
    private GlobalClass globalClass;
    private Activity activity =this;
    private TextView text_header_username,txt_password_header,txt_re_enterpass,txt_head_email,txt_head_sex,txt_head_age;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String fbaccessToken;
    private SessionManager session;
    private boolean  isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_mighty_create_account);
        globalClass = GlobalClass.getInstance();
        initialize_widget();  // Intilize UI
        session = new SessionManager(getApplicationContext());
        //Facebook btn initilize
        loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setTypeface(custom_font_light);
        LoginManager.getInstance().logOut();

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
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
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    public void initialize_widget()
    {
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");

        cancel = (TextView) findViewById(R.id.txt_cancel);
        save = (TextView) findViewById(R.id.txt_save);
        et_user = (EditText) findViewById(R.id.et_uname);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_repass = (EditText) findViewById(R.id.et_repass);
        et_email = (EditText) findViewById(R.id.et_email);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
        et_age = (EditText) findViewById(R.id.et_age);
        show_password =(CheckBox) findViewById(R.id.checkBox_showpassword);

        txt_field = (TextView) findViewById(R.id.txt_field);
        text_title = (TextView) findViewById(R.id.text_title);
        txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        txt_save = (TextView) findViewById(R.id.txt_save);
        text_username_error = (TextView) findViewById(R.id.text_username_error);
        text_password_error = (TextView) findViewById(R.id.text_password_error);
        text_renter_password_error = (TextView) findViewById(R.id.text_renter_password_error);
        text_email_error = (TextView) findViewById(R.id.text_email_error);

        text_header_username = (TextView) findViewById(R.id.text_header_username);
        txt_password_header = (TextView) findViewById(R.id.txt_password_header);
        txt_re_enterpass = (TextView) findViewById(R.id.txt_re_enterpass);
        txt_head_email = (TextView) findViewById(R.id.txt_head_email);
        txt_head_sex = (TextView) findViewById(R.id.txt_head_sex);
        txt_head_age = (TextView) findViewById(R.id.txt_head_age);

        cancel.setTypeface(custom_font_light);
        save.setTypeface(custom_font_light);
        cancel.setText("< BACK");
        et_user.setTypeface(custom_font_light);
        et_age.setTypeface(custom_font_light);
        et_pass.setTypeface(custom_font_light);
        et_repass.setTypeface(custom_font_light);
        et_email.setTypeface(custom_font_light);


        text_header_username.setTypeface(custom_font_light);
        txt_password_header.setTypeface(custom_font_light);
        txt_re_enterpass.setTypeface(custom_font_light);
        txt_head_email.setTypeface(custom_font_light);
        txt_head_sex.setTypeface(custom_font_light);
        txt_head_age.setTypeface(custom_font_light);

        text_username_error.setTypeface(custom_font_light);
        text_password_error.setTypeface(custom_font_light);
        text_renter_password_error.setTypeface(custom_font_light);
        text_email_error.setTypeface(custom_font_light);

        txt_field.setTypeface(custom_font_light);
        text_title.setTypeface(custom_font_bold);
        txt_cancel.setTypeface(custom_font_light);
        txt_save.setTypeface(custom_font_light);

        text_username_error.setVisibility(View.INVISIBLE);
        text_password_error.setVisibility(View.INVISIBLE);
        text_renter_password_error.setVisibility(View.INVISIBLE);
        text_email_error.setVisibility(View.INVISIBLE);

        //Geting Android Id
        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceOSVersion = Build.VERSION.RELEASE;
        DeviceType = getDeviceName();
        Field[] fields = Build.VERSION_CODES.class.getFields();
        OsName = fields[Build.VERSION.SDK_INT + 1].getName();
        changeTypeFaceSpinner();

        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    et_pass.setInputType(129);
                    et_repass.setInputType(129);
                    et_pass.setTypeface(custom_font_light);
                    et_repass.setTypeface(custom_font_light);

                } else {
                    et_pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_repass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_pass.setTypeface(custom_font_light);
                    et_repass.setTypeface(custom_font_light);
                }
            }
        });

        et_user.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                text_username_error.setVisibility(View.INVISIBLE);
                et_user.setBackgroundColor(Color.parseColor("#ffffff"));
                if (et_user.getText().toString().matches("^ ") )
                    et_user.setText("");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_password_error.setVisibility(View.INVISIBLE);
                et_pass.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_repass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_renter_password_error.setVisibility(View.INVISIBLE);
                et_repass.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_email_error.setVisibility(View.INVISIBLE);
                et_email.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_age.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(TextUtils.isEmpty(et_user.getText().toString())) {
                        text_username_error.setVisibility(View.VISIBLE);
                        et_user.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_username_error.setText(getString(R.string.field_required));
                        et_user.requestFocus();
                    }else{
                        if(et_user.getText().toString().length() < 6) {
                            text_username_error.setVisibility(View.VISIBLE);
                            et_user.setBackground(getResources().getDrawable(R.drawable.error_border));
                            text_username_error.setText(getString(R.string.your_username_eight_charaters));
                            et_user.requestFocus();
                            //et_user.setText("");
                        }
                    }
                }
            }
        });

        et_repass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(TextUtils.isEmpty(et_pass.getText().toString())) {
                        text_password_error.setVisibility(View.VISIBLE);
                        text_password_error.setText(getString(R.string.field_required));
                        et_pass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        et_pass.requestFocus();
                    }else{
                        if(et_pass.getText().toString().length() < 6) {
                            text_password_error.setVisibility(View.VISIBLE);
                            text_password_error.setText(getString(R.string.your_password_eight_charaters));
                            et_pass.setBackground(getResources().getDrawable(R.drawable.error_border));
                            et_pass.requestFocus();
                        }
                    }
                }
            }
        });

        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(TextUtils.isEmpty(et_repass.getText().toString())) {
                        text_renter_password_error.setVisibility(View.VISIBLE);
                        text_renter_password_error.setText(getString(R.string.field_required));
                        et_repass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        et_repass.requestFocus();
                    }else{
                        if(!et_repass.getText().toString().equals(et_pass.getText().toString())) {
                            text_renter_password_error.setVisibility(View.VISIBLE);
                            text_renter_password_error.setText(getString(R.string.pass_not_match));
                            et_repass.setBackground(getResources().getDrawable(R.drawable.error_border));
                            et_repass.requestFocus();
                            //et_pass.setText("");
                            //et_repass.setText("");
                        }
                    }
                }
            }
        });
        et_user.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(35)
        });
        et_pass.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(16)
        });
        et_repass.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(16)
        });

    }

    /*public boolean userValidate(String username) {
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasLetter =letter.matcher(username);
        Matcher hasDizit =digit.matcher(username);
        return hasLetter.find() && hasDizit.find() && username.length() > 5;
    }*/

    private  void sendData(final JSONObject regjson){
        try {
            final String requestBody = regjson.toString();
            Log.e(TAG,"json data "+requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.baseUrl+GlobalClass.consumer, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"response "+ response);
                    globalClass.dismissProgressBar();
                    globalClass.toastDisplay("User registration successful");
                    if(flag == 1)
                        finish();
                    else {
                        JSONObject jsonObject =  new JSONObject();
                        try {
                            jsonObject.put("UserIndicator", regjson.getString("UserIndicator"));
                            jsonObject.put("UserName", regjson.getString("UserName"));
                            jsonObject.put("Password", regjson.getString("Password"));
                            //getLoginId(jsonObject);
                            isConnected = ConnectivityReceiver.isConnected();
                            if(isConnected){
                                globalClass.refreshBaseTokenLogin(jsonObject, 1, activity);
                            }else {
                                globalClass.toastDisplay(getString(R.string.check_internet));
                                startActivity(new Intent(getApplicationContext(), MightyLoginActivity.class));
                            }

                        } catch (JSONException e) {
                        e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    globalClass.dismissProgressBar();
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        if(response.statusCode == 409){
                            globalClass.hardwarecompatibility(MightyCreateAccountActivity.this,"This username or email address is already registered","Please change your entries and try again");
                        }
                    }else if( error instanceof ServerError && response == null) {
                        Log.e(TAG,"Server Error "+"ServerError");
                        globalClass.toastDisplay("Server under maintenance \n Please try after some time!");
                    } else if( error instanceof TimeoutError) {
                        globalClass.hardwarecompatibility(MightyCreateAccountActivity.this,"Mighty account creation timedout","Please click on save button again, if you get the popup username or email address is already registered please go back and try to login");
                    }else{
                        globalClass.hardwarecompatibility(MightyCreateAccountActivity.this,"Mighty account creation failed","This can be caused by poor connectivity, please try again later");
                    }
                }

            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            //requestQueue.add(stringRequest);
            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Seting the typeface of spinner text & Spinner value
    public void changeTypeFaceSpinner(){
        String [] items = new String[4];
        items[0]="Select";
        items[1]="Male";
        items[2]="Female";
        items[3]="Non-binary";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_light);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter);
    }

    private boolean isEmailIDValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.txt_save :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Log.e(TAG,"click on save");
                    if(globalClass.mighty_ble_device != null)
                        globalClass.bleDisconnect();
                    str_username = et_user.getText().toString().trim();
                    str_pass = et_pass.getText().toString().trim();
                    str_repass = et_repass.getText().toString().trim();
                    str_email = et_email.getText().toString().trim();
                    str_age = et_age.getText().toString().trim();
                    str_gender = spinner_gender.getSelectedItem().toString().trim();
                    if(str_gender == "Select")
                        str_gender = "";
                    if(str_age == null)
                        str_age = "";
                    if(TextUtils.isEmpty(str_username)){
                        text_username_error.setVisibility(View.VISIBLE);
                        et_user.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_username_error.setText(getString(R.string.field_required));
                        et_user.requestFocus();
                    }else if( str_username.length() < 6) {
                        text_username_error.setVisibility(View.VISIBLE);
                        et_user.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_username_error.setText(getString(R.string.your_username_eight_charaters));
                        et_user.requestFocus();
                        //et_user.setText("");
                    }else if(TextUtils.isEmpty(str_pass)){
                        text_password_error.setVisibility(View.VISIBLE);
                        et_pass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_username_error.setText(getString(R.string.field_required));
                        et_pass.requestFocus();
                    }else if(str_pass.length() < 6) {
                        text_password_error.setVisibility(View.VISIBLE);
                        text_password_error.setText(getString(R.string.your_password_eight_charaters));
                        et_pass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        et_pass.requestFocus();
                    }else if(TextUtils.isEmpty(str_repass)){
                        text_renter_password_error.setVisibility(View.VISIBLE);
                        et_repass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_renter_password_error.setText(getString(R.string.field_required));
                        et_repass.requestFocus();
                    }else if(!str_pass.equals(str_repass)){
                        text_renter_password_error.setVisibility(View.VISIBLE);
                        et_repass.setBackground(getResources().getDrawable(R.drawable.error_border));
                        text_renter_password_error.setText(getString(R.string.pass_not_match));
                        et_repass.requestFocus();
                    }else if(TextUtils.isEmpty(str_email)){
                        text_email_error.setVisibility(View.VISIBLE);
                        text_email_error.setText(getString(R.string.field_required));
                        et_email.setBackground(getResources().getDrawable(R.drawable.error_border));
                        et_email.requestFocus();
                    }else if (!isEmailIDValid(str_email)){
                        text_email_error.setVisibility(View.VISIBLE);
                        text_email_error.setText(getString(R.string.email_incorrect));
                        et_email.setBackground(getResources().getDrawable(R.drawable.error_border));
                        et_email.requestFocus();
                    }else{
                        JSONObject jsonObject =  new JSONObject();
                        try {
                            jsonObject.put("UserName",str_username);
                            jsonObject.put("Password",str_pass);
                            jsonObject.put("EmailID",str_email);
                            jsonObject.put("Gender",str_gender);
                            jsonObject.put("Age",str_age);

                            jsonObject.put("UserIndicator","L");
                            jsonObject.put("DeviceModel","Android");
                            jsonObject.put("DeviceID",device_id);
                            jsonObject.put("DeviceName","Android");
                            jsonObject.put("DeviceOS",OsName);
                            jsonObject.put("DeviceOSVersion",DeviceOSVersion);
                            jsonObject.put("DeviceType",DeviceType);
                            globalClass.showProgressBar(this);
                            hideSoftKeboard();
                            sendData(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginManager.getInstance().logOut();
        loginButton.registerCallback(callbackManager, callback);
        bundle = getIntent().getExtras();
        if(bundle != null){
            flag = bundle.getInt("notsetup");
        }
        Log.e(TAG,"flag "+flag);
        loginButton.setText(getString(R.string.com_facebook_loginview_log_in_button_long));
    }

    //Facebook Call Back method
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            final Profile profile = Profile.getCurrentProfile();
            Log.e(TAG,"calling "+accessToken);
            //displayMessage(profile);
            final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
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
                                Log.e(TAG,"Face Book Detail "+object.getString("first_name")+" "+object);
                                String email = "NoEmail";
                                if(!object.isNull("email"))
                                    email = object.getString("email");
                                jsonObject.put("UserIndicator","F");
                                if(fbaccessToken != null)
                                jsonObject.put("UserName", object.getString("name"));
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
            parameters.putString("fields", "id,name,email,gender,birthday,first_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.e(TAG,"onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e(TAG,"FacebookException");
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
