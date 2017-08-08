package mightyaudio.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = DebugActivity.class.getSimpleName();
    Typeface custom_font_bold,custom_font;
    Button btn_okay;
    private TextView cancel_txt,header_txt,save_txt,txt_head;
    private GlobalClass globalClass;
    private IntentFilter intentFilter ;
    private BroadcastReceiver ble_onreceive;
    private SessionManager session;
    private SharedPreferences pref;
    private Spinner spinner_debug_type;
    private String str_debug_type,ticketno_str,discription_str;
    private TextView txt_category,txt_ticket_no,text_description,txt_note;
    private EditText et_ticket_no,et_discription;
    private boolean  isConnected;
    private MaterialDialog mMaterialDialog;
    private TextView alert_header_txt,message_txt;
    private View promptView,debug_promptView,timeout_popup;
    BroadcastReceiver receiver_disconnect;
    IntentFilter intentFilter_disconnect;
    private static Timer progresstimer= new Timer();
    private static TimerTask progresstimerTask;
    Handler handler_timer = new Handler();
    private MaterialDialog debug_sendMetrial,timeout_Metrial;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        globalClass = GlobalClass.getInstance();
        session = new SessionManager(this);
        pref = getSharedPreferences(session.PREF_NAME, Context.MODE_PRIVATE);
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        txt_head=(TextView) findViewById(R.id.txt_head);
        btn_okay=(Button) findViewById(R.id.btn_submit);

        txt_category=(TextView) findViewById(R.id.txt_category);
        txt_ticket_no=(TextView) findViewById(R.id.txt_ticket_no);
        text_description=(TextView) findViewById(R.id.text_description);
        txt_note=(TextView) findViewById(R.id.txt_note);

        et_ticket_no=(EditText) findViewById(R.id.et_ticket_no);
        et_discription=(EditText) findViewById(R.id.et_discription);

        spinner_debug_type = (Spinner) findViewById(R.id.spinner_debug_type);
        changeTypeFaceSpinner();
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font);
        save_txt.setTypeface(custom_font);
        txt_head.setTypeface(custom_font_bold);
        btn_okay.setTypeface(custom_font);
        txt_note.setTypeface(custom_font);

        txt_category.setTypeface(custom_font);
        txt_ticket_no.setTypeface(custom_font);
        text_description.setTypeface(custom_font);
        et_ticket_no.setTypeface(custom_font);
        et_discription.setTypeface(custom_font);

        save_txt.setVisibility(View.INVISIBLE);
        cancel_txt.setText("< BACK");
        header_txt.setText("Send Report");

        btn_okay.setOnClickListener(this);
        cancel_txt.setOnClickListener(this);

        onReceiveBleSoftwareVersion();
        et_ticket_no.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(50)
        });
        et_discription.setFilters(new InputFilter[] {
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
                },new InputFilter.LengthFilter(200)
        });
        disconnectedMighty();
    }

    public void changeTypeFaceSpinner(){
        String [] items = new String[8];
        items[0]="Select";
        items[1]="App Connectivity";
        items[2]="Bluetooth Accessory";
        items[3]="Playback";
        items[4]="Syncing";
        items[5]="Spotify Connection";
        items[6]="WiFi";
        items[7]="Other";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, items) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font);
                return v;
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_debug_type.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        registerReceiver(ble_onreceive,intentFilter);
        registerReceiver(receiver_disconnect, intentFilter_disconnect);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.btn_submit :
                if(globalClass.mighty_ble_device != null){
                    if(0.93f <= Float.valueOf(globalClass.device_info.getSW_Version())){
                        if(!globalClass.syncing_status){
                            isConnected = ConnectivityReceiver.isConnected();
                            if(isConnected){
                                if(globalClass.upgradeProcess){
                                    str_debug_type = spinner_debug_type.getSelectedItem().toString().trim();
                                    ticketno_str = et_ticket_no.getText().toString().trim();
                                    discription_str = et_discription.getText().toString().trim();
                                    if(str_debug_type != "Select"){
                                        if(ticketno_str == null)
                                            ticketno_str = "";
                                        if(discription_str == null)
                                            discription_str = "";
                                        System.gc(); // Clear unused data.
                                        showProgressBar(DebugActivity.this);
                                        globalClass.debug_data.setDebug_data(null);
                                        globalClass.debug_data.setSize(0);

                                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                        TCPClient tcpClient = new TCPClient();
                                        MightyMessage mightyMessage = new MightyMessage();
                                        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                                        mightyMessage.MessageID = Constants.MSG_ID_DEBUG_FEATURE;
                                        tcpClient.SendData(mightyMessage);
                                        startTimer();

                                    }else{
                                        globalClass.toastDisplay("Please select category");
                                    }
                                }else{
                                    globalClass.toastDisplay("Please wait for few minutes and try again");
                                    globalClass.mightyRegistration("BluetoothService");
                                }
                            }else{
                                globalClass.toastDisplay(getString(R.string.check_internet));
                            }
                        }else reportDailog("Report cannot be submitted during sync","Please wait for the sync in progress to complete and try again",0);
                    }else{
                        reportDailog("You need to update to the newest version of the Mighty software in order to send a report","Click on Software Update in the User tab to update your Mighty.",0);
                    }
                }else{
                    reportDailog("Mighty not connected","Please connect your Mighty to submit the report",0);
                }

                break;
        }
    }

    public void reportDailog(String tittle, String meassge,final int flag){
        if(mMaterialDialog != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        alert_header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        alert_header_txt.setText(tittle);
        message_txt.setText(meassge);
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag == 1) {
                            globalClass.auto_connect_force_cancel = 1;
                        }
                        mMaterialDialog.dismiss();
                        mMaterialDialog= null;

                    }
                });
        mMaterialDialog.show();
    }
    /*public String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }*/

    private void onReceiveBleSoftwareVersion(){
        intentFilter = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                Log.e(TAG,"debug_broad");
                if(msgId == Constants.MSG_ID_DEBUG_FEATURE){
                    Log.e(TAG,"Debug_Size Data Recived "+msgType);
                    //Log.e(TAG,"Size of Base data"+readableFileSize(globalClass.debug_data.getDebug_data().length()));
                    if(msgType == 202){
                        isConnected = ConnectivityReceiver.isConnected();
                        if(isConnected){
                            String  accesstoke_token = pref.getString(SessionManager.APITOKEN,"");
                            int userid = pref.getInt(SessionManager.USERID,0);

                            String DeviceOSVersion = Build.VERSION.RELEASE;
                            String DeviceType = getDeviceName();
                            JSONObject debug_json = new JSONObject();
                            try {
                                debug_json.put("userId",userid);
                                debug_json.put("deviceId",globalClass.device_info.getHW_Serial_Number());
                                debug_json.put("DeviceOSVersion",DeviceOSVersion.trim());
                                debug_json.put("DeviceType",DeviceType.trim());
                                debug_json.put("log_type",str_debug_type);
                                debug_json.put("ticket",ticketno_str);
                                debug_json.put("desc",discription_str);
                                debug_json.put("file_content",globalClass.debug_data.getDebug_data());

                                Log.e(TAG,"Decode size "+globalClass.debug_data.getDebug_data().length());
                                sendDebugData(accesstoke_token,debug_json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            stoptimertask();
                            dismissProgressBar();
                            globalClass.toastDisplay(getString(R.string.check_internet));
                        }
                    }else if(msgType == 201){
                        stoptimertask();
                        dismissProgressBar();
                        globalClass.toastDisplay("Couldn't submit the report \n Please try again");
                        Log.e(TAG,"No data");
                    }
                }
            }
        };
    }

    private void disconnectedMighty(){
        intentFilter_disconnect = new IntentFilter("ble.disconnect.successful");
        receiver_disconnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                globalClass.auto_connect_force_cancel = 2;
                stoptimertask();
                dismissProgressBar();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                reportDailog("Mighty not connected","Please connect your Mighty to submit the report",1);

            }
        };
    }

    private  void sendDebugData(final String accesstoke_token, final JSONObject regjson){
        try {
            final String requestBody = regjson.toString();
            Log.e(TAG,"Debug json "+requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.baseUrl+GlobalClass.consumer+"getMightyLogs", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"debug response "+ response);
                   dismissProgressBar();
                    stoptimertask();
                    debugMetrial("Your report has been sent");

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DEBUG VOLLEY", error.toString());
                    stoptimertask();
                    dismissProgressBar();
                    try {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        reportDailog("Something went wrong","Please try again in a few minutes",0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",accesstoke_token);
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
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            globalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debugMetrial(String message){
        if(debug_sendMetrial != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        debug_promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        TextView header_txt=(TextView)debug_promptView.findViewById(R.id.text_header);
        header_txt.setText(message);
        debug_sendMetrial = new MaterialDialog(this)
                .setView(debug_promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        finish();
                        debug_sendMetrial.dismiss();
                        debug_sendMetrial= null;
                    }
                });
        debug_sendMetrial.show();
    }

    public void startTimer() {
        Log.e(TAG,"Timer Start");
        initializeTimerTask();
        if (progresstimer ==null)
            progresstimer = new Timer();
        progresstimer.schedule(progresstimerTask, 1500000, 1500000);
    }

    public void stoptimertask() {
        Log.e(TAG,"Timer Stoped");
        if (progresstimer != null) {
            progresstimer.cancel();
            progresstimer = null;
            if (progresstimerTask != null){
                progresstimerTask.cancel();
            }
        }
    }
    public void initializeTimerTask() {
        progresstimerTask = new TimerTask() {
            public void run() {
                handler_timer.post(new Runnable() {
                    public void run() {
                        Log.e(TAG, "Timer_called ");
                        dismissProgressBar();
                        timeOutDailo();
                        stoptimertask();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                });
            }
        };
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

    public void showProgressBar(Activity activity){
        Log.e(TAG,"Prgressbar clicked");
        try{
            dialog = new Dialog(activity);
            dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
            dialog.setContentView (R.layout.custom_pogrssbar);
            dialog.setCancelable(false);
            dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
            dialog.show ();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dismissProgressBar(){
        try {
            if(dialog != null){
                dialog.cancel();
                dialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        unregisterReceiver(ble_onreceive);
        unregisterReceiver(receiver_disconnect);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void timeOutDailo(){
        if(timeout_Metrial != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        Spannable word = new SpannableString("Unable to send report/Please try again later. If still having issues, please reach out to us at");
        word.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Spannable wordTwo = new SpannableString(" heyo@bemighty.com");
        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        timeout_popup = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        TextView timeout_header_text=(TextView)timeout_popup.findViewById(R.id.text_header);
        timeout_header_text.setText(word);
        timeout_header_text.append(wordTwo);
        timeout_Metrial = new MaterialDialog(this)
                .setView(timeout_popup)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeout_Metrial.dismiss();
                        timeout_Metrial= null;
                    }
                });

        timeout_Metrial.show();
    }

}
