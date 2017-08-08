package mightyaudio.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;

public class ChangePasswordActivity extends RootActivity implements View.OnClickListener{
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private TextView left_header_text,middle_header_text,right_header_text;
    private EditText current_password,new_password,confirm_new_password;
    private String str_current_password,str_new_password,str_confir_password,accessToken;
    private SessionManager session;
    private SharedPreferences pref;
    private int user_id;
    private GlobalClass globalClass;
    private CheckBox cb_password_enable_disable;
    private Typeface custom_font_bold,custom_font_light;
  //  private ProgressDialog progressDialog;
    private Dialog dialog;
    private View changepasswordpopup;
    private TextView changepasswordtext,changepasswordmessage;
    private MaterialDialog changepasswordmaterail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        globalClass = GlobalClass.getInstance() ;
        left_header_text = (TextView)findViewById(R.id.txt_cancel);
        middle_header_text = (TextView)findViewById(R.id.text_title);
        right_header_text = (TextView)findViewById(R.id.txt_save);
        middle_header_text.setText(getString(R.string.username_password_header));
        left_header_text.setText("< BACK");
        right_header_text.setText("SAVE");
        middle_header_text.setText("Change Password");

        current_password = (EditText)findViewById(R.id.edit_current_pass);
        new_password =(EditText)findViewById(R.id.edite_new_password);
        confirm_new_password = (EditText)findViewById(R.id.edit_confir_password);
        cb_password_enable_disable = (CheckBox)findViewById(R.id.cb_password);

        session = new SessionManager(getApplicationContext());
        pref = getSharedPreferences(session.PREF_NAME , Context.MODE_PRIVATE);
        user_id= pref.getInt(SessionManager.USERID,0);

        left_header_text.setOnClickListener(this);
        right_header_text.setOnClickListener(this);

        custom_font_light = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        left_header_text.setTypeface(custom_font_light);
        right_header_text.setTypeface(custom_font_light);
        middle_header_text.setTypeface(custom_font_bold);
        setTypefase();

        /**************************** Password Show  ****************************/
        cb_password_enable_disable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    current_password.setInputType(129);
                    new_password.setInputType(129);
                    confirm_new_password.setInputType(129);
                    setTypefase();

                } else {
                    current_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    new_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirm_new_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    setTypefase();
                }
            }
        });

        new_password.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(25)
        });
        confirm_new_password.setFilters(new InputFilter[] {
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
        current_password.setFilters(new InputFilter[] {
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

    private void setTypefase(){
        current_password.setTypeface(custom_font_light);
        new_password.setTypeface(custom_font_light);
        confirm_new_password.setTypeface(custom_font_light);
        cb_password_enable_disable.setTypeface(custom_font_light);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.txt_save:
                dialog = new Dialog(this);
                dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
                dialog.setContentView (R.layout.custom_pogrssbar);
                dialog.setCancelable(false);
                dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
     //           progressDialog = new ProgressDialog(this);
     //           progressDialog.setMessage("Please wait ...");
                str_current_password= current_password.getText().toString().trim();
                str_new_password = new_password.getText().toString().trim();
                str_confir_password = confirm_new_password.getText().toString();
                accessToken  = pref.getString(SessionManager.APITOKEN,"");
                if(TextUtils.isEmpty(str_current_password)){
                    current_password.setError(getString(R.string.field_required));
                    current_password.requestFocus();
                }else if(TextUtils.isEmpty(str_new_password)){
                    new_password.setError(getString(R.string.field_required));
                    new_password.requestFocus();
                }else if(str_new_password.length()  < 6) {
                    globalClass.toastDisplay(getString(R.string.your_password_eight_charaters));
                    new_password.requestFocus();
                }else if(TextUtils.isEmpty(str_confir_password)){
                    new_password.setError(getString(R.string.field_required));
                    new_password.requestFocus();
                }else if(!str_new_password.equals(str_confir_password)){
                    globalClass.toastDisplay(getString(R.string.pass_not_match));
                    new_password.setText("");
                    confirm_new_password.setText("");
                    new_password.requestFocus();
            }/*else if(!userValidate(str_new_password)){
                    globalClass.toastDisplay("Your password must be contain 1 character and 1 Numeric");
                    new_password.requestFocus();
                }*/else {
                    Log.e(TAG,"else part");
                    dialog.show ();
                //    progressDialog.show();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userId",String.valueOf(user_id));
                        jsonObject.put("Password",str_current_password);
                        jsonObject.put("NewPassword",str_new_password);
                        hideSoftKeboard();  //Hide the keyboard
                        updatePassword(jsonObject,accessToken.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                break;

        }
    }

    private void updatePassword(final JSONObject changepassword, final String accessToken){
        try {
            final String requestBody = changepassword.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.baseUrl+GlobalClass.consumer+"changePwd", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"update response "+ response);
                    restoreLoginInfo(changepassword);
                    //progressDialog.cancel();
                    if(dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    globalClass.toastDisplay(getString(R.string.pass_changed));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                 //   progressDialog.cancel();
                    if(dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            Log.e(TAG,"update err "+res+ "resp_code" + response.statusCode);
                            if(response.statusCode == 417){
                                LayoutInflater layoutInflater = LayoutInflater.from(ChangePasswordActivity.this);
                                changepasswordpopup = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                                changepasswordtext=(TextView)changepasswordpopup.findViewById(R.id.text_header);
                                changepasswordmessage=(TextView)changepasswordpopup.findViewById(R.id.text_message);
                                changepasswordtext.setText("Current password is incorrect");
                                changepasswordmessage.setText("Please re-enter your password and try again");
                                changepasswordmaterail = new MaterialDialog(ChangePasswordActivity.this)
                                            .setView(changepasswordpopup)
                                            .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    changepasswordmaterail.dismiss();
                                                    changepasswordmaterail= null;

                                                }
                                            });
                                changepasswordmaterail.show();
                            }else{
                                globalClass.toastDisplay(res);
                            }

                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",accessToken);
                    Log.e(TAG,"header "+header);
                    return header;
                }
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
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public boolean userValidate(String username) {
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasLetter =letter.matcher(username);
        Matcher hasDizit =digit.matcher(username);
        return hasLetter.find() && hasDizit.find() && username.length() > 5;
    }


    private void restoreLoginInfo(JSONObject loginjson){
        String user_name = pref.getString(SessionManager.USER_NAME,"");
        String user_password = null;
        try {
            user_password = loginjson.getString("NewPassword");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userIndicator = pref.getString(SessionManager.USERINDICATOR,"");
        String  statusDesc = pref.getString(SessionManager.STATUSDESC,"");
        String  statusCode = pref.getString(SessionManager.STATUSCODE,"");
        String  apiToken = pref.getString(SessionManager.APITOKEN,"");
        String accessTokenExpDate = pref.getString(SessionManager.ACCESSTOKENEXPDATE,"");
        String  baseToken = pref.getString(SessionManager.BASETOKEN,"");
        String  accessToken = pref.getString(SessionManager.ACCESSTOKEN,"");
        String  baseTokenExpDate = pref.getString(SessionManager.BASETOKENEXPDATE,"");
        String  userStatus = pref.getString(SessionManager.USERSTATUS,"");
        int  userId = pref.getInt(SessionManager.USERID,0);
        String  mightyDiviceId = pref.getString(SessionManager.MIGHTYFEVICE_ID,"");
        //session.clearSession(); //clearing session Data

        session.createLoginSession(user_name, user_password,userIndicator, userId,statusDesc,statusCode,apiToken,baseToken,accessToken,
                accessTokenExpDate,baseTokenExpDate,userStatus,mightyDiviceId);

    }

    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
