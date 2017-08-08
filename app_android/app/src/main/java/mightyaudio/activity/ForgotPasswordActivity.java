package mightyaudio.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.receiver.ConnectivityReceiver;

public class ForgotPasswordActivity extends RootActivity implements View.OnClickListener{
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private TextView save_text,back_text,text_title,text_head_lebel;
    private EditText edit_email;
    private String str_email;
    private Typeface custom_font,custom_bold;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private MaterialDialog  mMaterialDialog;
    private View promptView;
    private TextView header_txt;
    private boolean  isConnected;
    private GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        globalClass = GlobalClass.getInstance();
        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        edit_email = (EditText)findViewById(R.id.edit_email);
        save_text =(TextView)findViewById(R.id.txt_save);
        back_text =(TextView)findViewById(R.id.txt_cancel);
        text_title =(TextView)findViewById(R.id.text_title);
        text_head_lebel =(TextView)findViewById(R.id.text_head_lebel);
        save_text.setText("SUBMIT");
        back_text.setText("< BACK");
        text_title.setText("Forgot Password");

        //set TypeFace
        back_text.setTypeface(custom_font);
        save_text.setTypeface(custom_font);
        text_title.setTypeface(custom_bold);
        edit_email.setTypeface(custom_font);
        text_head_lebel.setTypeface(custom_font);

        save_text.setOnClickListener(this);
        back_text.setOnClickListener(this);

        edit_email.setFilters(new InputFilter[] {
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

    private boolean isEmailIDValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.txt_save :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected) {
                    dialog = new Dialog(this);
                    dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
                    dialog.setContentView (R.layout.custom_pogrssbar);
                    dialog.setCancelable(false);
                    dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);

              //      progressDialog = new ProgressDialog(this);
              //      progressDialog.setMessage("Please wait ...");
                    str_email = edit_email.getText().toString().trim();
                    if (TextUtils.isEmpty(str_email)) {
                        edit_email.setError(getString(R.string.field_required));
                        edit_email.requestFocus();
                    } else if (!isEmailIDValid(str_email)) {
                        edit_email.setError(getString(R.string.email_incorrect));
                        edit_email.requestFocus();
                    } else {
                        try {
                            dialog.show ();
               //             progressDialog.show();
                            Log.e(TAG, "email" + str_email);
                            JSONObject email_json = new JSONObject();
                            email_json.put("Email", str_email);
                            hideSoftKeboard();
                            sendData(email_json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
        }
    }

    private  void sendData(JSONObject jsonObj){
        try {
            final String requestBody = jsonObj.toString();
            Log.e(TAG,"json data "+requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.baseUrl+GlobalClass.consumer+"resetPassword", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"response "+ response);
             //       progressDialog.cancel();
                    if(dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    succsessDailog(getString(R.string.reset_password_message));
                    //globalClass.toastDisplay(getString(R.string.reset_password_message));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
              //      progressDialog.cancel();
                    if(dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){
                        switch(response.statusCode){
                            case 400:
                                Log.e(TAG,"response.statusCode "+response.statusCode);
                                globalClass.hardwarecompatibility(ForgotPasswordActivity.this,"Incorrect email address","Please enter the email address associated with your Mighty account");
                                edit_email.setText("");
                                break;
                            case 500:
                                //Change the message base on cloud
                                globalClass.hardwarecompatibility(ForgotPasswordActivity.this,"Incorrect email address","Please enter the email address associated with your Mighty account");
                                edit_email.setText("");
                                break;
                        }
                        //Additional cases
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

    private void succsessDailog(String myTitle){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        header_txt.setText(myTitle);
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mMaterialDialog.dismiss();

                    }
                });
        mMaterialDialog.show();
    }
    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
