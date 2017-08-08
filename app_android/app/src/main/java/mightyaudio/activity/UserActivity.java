package mightyaudio.activity;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
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
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SessionManager;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = UserActivity.class.getSimpleName();
    MightyMsgReceiver mightyMsgReceiver;
    private TextView account_head,mighty_head,text_logout,text_mighty_setup,text_change_password,text_software_update,mighty_helps,privacy_term,txt_version,
            text_forgot_password,text_mighty_about,text_logged_as,text_username,text_mighty_led,text_stay_fresh,text_issue_report;
    private String accesstoke_token;
    private int userid;
    private PackageInfo pinfo;
    private Typeface custom_font,custom_font_bold;
    private JSONObject mightyjsonObject;
    private int progressStatus = 0;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    private Handler handler = new Handler();
    private GlobalClass globalClass;
    private SessionManager session;
    private SharedPreferences pref;
    private android.app.AlertDialog.Builder alertDialogBuilder;
    private SpotifySessionManager spotifySessionManager;
    private TextView arrow1,arrow2,arrow3,arrow4,arrow5,arrow6,arrow8,arrow9,arrow10,arrow11,arrow12;
    private LaunchTabActivity launchTabActivity;
    private LinearLayout change_password_layout,forgot_password_layout;
    private boolean  isConnected;
    private Map<String,String> header;
    private Dialog dialog;
    private MaterialDialog mMaterialsetup,debug_meterial;
    private View promptViewSetup;
    private View debug_promptView;
    private TextView header_txt,debug_header;
    private TextView message_txt,debug_message;
    private TabActivity parentTab;
    private Timer timer;
    private TimerTask  timerTask;
    final Handler handler1 = new Handler();


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        if(pref.getBoolean(SessionManager.IS_LOGIN, false)){
            text_logout.setText(getString(R.string.Logout));
            String username= pref.getString(SessionManager.USER_NAME,"");
            String pass= pref.getString(SessionManager.PASSWORD,"");
            Log.e(TAG ,"username , password "+username+" "+pass);
            text_username.setText(username.toLowerCase());

            if(!pref.getString(SessionManager.USERINDICATOR,"").equals("L")){
                change_password_layout.setVisibility(View.GONE);
                forgot_password_layout.setVisibility(View.GONE);
            }else{
                change_password_layout.setVisibility(View.VISIBLE);
                forgot_password_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        launchTabActivity = (LaunchTabActivity)getParent();   // This Parent activity
        globalClass = GlobalClass.getInstance();
        session = new SessionManager(this);
        parentTab = (TabActivity) this.getParent();
        spotifySessionManager = new SpotifySessionManager(this);
        pref = getSharedPreferences(session.PREF_NAME , Context.MODE_PRIVATE);
        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        change_password_layout = (LinearLayout)findViewById(R.id.change_password_layout);
        forgot_password_layout = (LinearLayout)findViewById(R.id.forgot_password_layout);


        text_stay_fresh = (TextView)findViewById(R.id.text_stay_fresh);
        account_head = (TextView)findViewById(R.id.text_account);
        mighty_head = (TextView)findViewById(R.id.text_header_mighty);
        text_logout = (TextView)findViewById(R.id.text_login);
        text_change_password = (TextView)findViewById(R.id.text_change_password);
        text_forgot_password =(TextView)findViewById(R.id.text_forgot_password);
        text_mighty_setup = (TextView)findViewById(R.id.text_mighty_setup);
        mighty_helps = (TextView)findViewById(R.id.text_mighty_help);
        text_software_update = (TextView)findViewById(R.id.text_software_update);
        privacy_term = (TextView)findViewById(R.id.text_privacy_term);
        txt_version = (TextView)findViewById(R.id.text_version);
        text_mighty_about = (TextView)findViewById(R.id.text_mighty_about);
        text_logged_as = (TextView)findViewById(R.id.text_logged_as);
        text_username = (TextView)findViewById(R.id.text_username);
        text_mighty_led = (TextView)findViewById(R.id.text_mighty_led);
        text_issue_report = (TextView)findViewById(R.id.text_issue_report);
       // horizontal_process.setVisibility(View.GONE);

        arrow1 =(TextView)findViewById(R.id.arrow1);
        arrow2 =(TextView)findViewById(R.id.arrow2);
        arrow3 =(TextView)findViewById(R.id.arrow3);
        arrow4 =(TextView)findViewById(R.id.arrow4);
        arrow5 =(TextView)findViewById(R.id.arrow5);
        arrow6 =(TextView)findViewById(R.id.arrow6);
        arrow8 =(TextView)findViewById(R.id.arrow8);
        arrow9 =(TextView)findViewById(R.id.arrow9);
        arrow10 =(TextView)findViewById(R.id.arrow10);
        arrow11 =(TextView)findViewById(R.id.arrow11);
        arrow12 =(TextView)findViewById(R.id.arrow12);

        text_logout.setOnClickListener(this);
        text_change_password.setOnClickListener(this);
        //create_mighty_ac.setOnClickListener(this);
        text_software_update.setOnClickListener(this);
        text_forgot_password.setOnClickListener(this);
        mighty_helps.setOnClickListener(this);
        privacy_term.setOnClickListener(this);
        text_mighty_setup.setOnClickListener(this);
        text_mighty_about.setOnClickListener(this);
        text_mighty_led.setOnClickListener(this);
        text_stay_fresh.setOnClickListener(this);
        text_issue_report.setOnClickListener(this);

        text_stay_fresh.setTypeface(custom_font);
        text_mighty_setup.setTypeface(custom_font);
        text_change_password.setTypeface(custom_font);
        text_logged_as.setTypeface(custom_font);
        text_username.setTypeface(custom_font);
        text_logout.setTypeface(custom_font);
        mighty_helps.setTypeface(custom_font);
        text_software_update.setTypeface(custom_font);
        mighty_helps.setTypeface(custom_font);
        privacy_term.setTypeface(custom_font);
        text_forgot_password.setTypeface(custom_font);
        text_mighty_about.setTypeface(custom_font);
        text_mighty_led.setTypeface(custom_font);
        text_issue_report.setTypeface(custom_font);

        account_head.setTypeface(custom_font_bold);
        mighty_head.setTypeface(custom_font_bold);
        txt_version.setTypeface(custom_font);
        arrow1.setTypeface(custom_font);
        arrow2.setTypeface(custom_font);
        arrow3.setTypeface(custom_font);
        arrow4.setTypeface(custom_font);
        arrow5.setTypeface(custom_font);
        arrow6.setTypeface(custom_font);
        arrow8.setTypeface(custom_font);
        arrow9.setTypeface(custom_font);
        arrow10.setTypeface(custom_font);
        arrow11.setTypeface(custom_font);
        arrow12.setTypeface(custom_font);

        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);

        mightyMsgReceiver = new MightyMsgReceiver(getBaseContext()) {
            @Override
            public void onConnected() {
                Log.d(TAG, "Con_nected");
            }

            @Override
            public void onDiscovered() {
                Log.d(TAG, "Dis_covered");
                globalClass.mighty_logout_status= false;
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "Dis_Connected");
                globalClass.mighty_logout_status = false;
            }
        };
        mightyMsgReceiver.RegisterReceiver(launchTabActivity);

    }

    @Override
    public void onClick(View v) {
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            accesstoke_token = pref.getString(SessionManager.APITOKEN,"");
            userid = pref.getInt(SessionManager.USERID,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int id = v.getId();
        switch (id){
            case R.id.text_login :
                isConnected = ConnectivityReceiver.isConnected();
                if(!globalClass.syncing_status){
                    if(globalClass.mighty_ble_device != null){
                        if(isConnected){
                            prgrssDailogShow();
                            if(globalClass.device_info != null){
                                JSONObject jsonObject =new JSONObject();
                                try {
                                    jsonObject.put("deviceID",globalClass.device_info.getHW_Serial_Number());
                                    Log.e(TAG,"got Result"+jsonObject+" "+accesstoke_token);
                                    deviceDeregister(jsonObject,accesstoke_token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                                //logoutDailog("Are you sure?","For security purposes, all previously synced playlists will be deleted if you log back in and connect to this Mighty with a different account than the one you used previously",UserActivity.this);
                        }else globalClass.alertDailogSingleText("No Internet connection detected. Please connect to a WiFi or cellular network and try again.",UserActivity.this);
                    }else{
                        logOutApp();
                    }
                } else globalClass.hardwarecompatibility(parentTab,getString(R.string.playlist_sync_logout_tittle),getString(R.string.playlist_sync_logout));
                break;
            case R.id.text_change_password :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected)
                    startActivity(new Intent(this,ChangePasswordActivity.class));
                    //startActivity(new Intent(this,MightySoftwareUpdateActivity.class));
                else globalClass.toastDisplay(getString(R.string.check_internet));
                break;
            case R.id.text_forgot_password :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Intent intent1= new Intent(this,ForgotPasswordActivity.class);
                    startActivity(intent1);
                }
                else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_stay_fresh:
                globalClass.toastDisplay("Coming Soon");
                break;
            case R.id.text_software_update :
                isConnected = ConnectivityReceiver.isConnected();
                Log.e(TAG, "battery_info "+globalClass.battery_info.AvailablePercentage+" "+globalClass.battery_info.getStatus());
                if(isConnected){
                    if(globalClass.mighty_ble_device != null && globalClass.wifi_status){
                        mightyjsonObject = new JSONObject();
                        try {
                            //mightyjsonObject.put("UserID",String.valueOf(userid));
                            if(globalClass.device_info != null){
                                globalClass.showProgressBar(this);
                                mightyjsonObject.put("HWSerialNumber",globalClass.device_info.getHW_Serial_Number());
                                mightyjsonObject.put("SWVersion",globalClass.device_info.getSW_Version());//GlobalClass.device_info.SW_Version);
                                mightyjsonObject.put("AppVersion",pinfo.versionName);
                                mightyjsonObject.put("AppBuild",pinfo.versionCode+"");
                                globalClass.deviceFirmware(mightyjsonObject,accesstoke_token,this);
                            } else Log.e(TAG," No HWSerialNumber");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else globalClass.hardwarecompatibility(launchTabActivity,getString(R.string.software_update_fail),getString(R.string.connect_mighty_to_mobile));
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_mighty_help :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Intent mighty_help_intent = new Intent(this,MightyHelpActivity.class);
                    mighty_help_intent.putExtra("FromData","UserActivity");
                    startActivity(mighty_help_intent);
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_privacy_term :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    startActivity(new Intent(this,PrivacyTermsActivity.class));
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_mighty_setup :
                if (globalClass.mighty_ble_device != null) {
                    setupDailogSwitch();
                }else setup();
                break;
            case R.id.text_mighty_about :
                    startActivity(new Intent(this,MightyAboutActivity.class));
                break;
            case R.id.text_mighty_led :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Intent mighty_help_intent = new Intent(this,MightyHelpActivity.class);
                    mighty_help_intent.putExtra("FromData","MightyLED");
                    startActivity(mighty_help_intent);
                }else globalClass.toastDisplay(getString(R.string.check_internet));
                break;
            case R.id.text_issue_report :
                if(globalClass.mighty_ble_device != null && globalClass.device_info != null){
                    if(0.93f <= Float.valueOf(globalClass.device_info.getSW_Version())){
                        startActivity(new Intent(this,DebugActivity.class));
                    }else{
                        debugDailog("You need to update to the newest version of the Mighty software in order to send a report","Click on Software Update in the User tab to update your Mighty.");
                    }
                }else{
                    debugDailog("Mighty not connected","Please connect your Mighty to submit the report");
                }
                break;
        }
    }

    public void deviceDeregister(JSONObject jsonObject, final String access_token){
        try {
            Log.e(TAG,"json data "+jsonObject+" "+globalClass.baseUrl+globalClass.consumer+"consumer/mightyDeReg");
            final String requestBody = jsonObject.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, globalClass.baseUrl+"consumer/mightyDeReg", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(Integer.parseInt(response)== 200){
                        progrssDailogCancel();
                        if(globalClass.mighty_ble_device != null){
                            globalClass.mightyLogin.setLogin_mode(2);
                            send_set_mightylogout();
                        }else {//logOutApp();
                            globalClass.hardwarecompatibility(UserActivity.this,"Mighty account logout failed","Please try to logout again");
                        }

                        Log.e(TAG,"mightyDeReg"+"Mighty De-Register Done");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    progrssDailogCancel();
                    globalClass.hardwarecompatibility(UserActivity.this,"Mighty account logout failed","Please try to logout again");
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",access_token);
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDailogSwitch(){
        LayoutInflater layoutInflater = LayoutInflater.from(UserActivity.this);
        promptViewSetup = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptViewSetup.findViewById(R.id.text_header);
        message_txt=(TextView)promptViewSetup.findViewById(R.id.text_message);
        header_txt.setText("Are you sure you want to continue?");
        message_txt.setText("Your Mighty will be disconnected from the mobile app. You can reconnect by completing the setup process or accessing the Connections tab.");
        mMaterialsetup = new MaterialDialog(UserActivity.this)
                .setView(promptViewSetup)
                .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        globalClass.bleDisconnect();
                        setup();
                        mMaterialsetup.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialsetup.dismiss();
                    }
                });
        mMaterialsetup.show();
    }

    public void setup(){
        Log.e(TAG,"onResume setup page "+globalClass);
        globalClass.auto_connect_force_cancel =2;
        globalClass.flow_editor.putString(globalClass.FLOW_WAY,"setup");
        globalClass.flow_editor.commit();
        globalClass.userActivityMode = 1;
        Intent setup_intent= new Intent(this,SetUpActivity.class);
        startActivity(setup_intent);

        parentTab.finish();
    }

    private void debugDailog(String myTitle, String myMsg){
        if(debug_meterial != null){
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        debug_promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        debug_header=(TextView)debug_promptView.findViewById(R.id.text_header);
        debug_message=(TextView)debug_promptView.findViewById(R.id.text_message);
        debug_header.setText(myTitle);
        debug_message.setText(myMsg);
        debug_meterial = new MaterialDialog(this)
                .setView(debug_promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debug_meterial.dismiss();
                        debug_meterial = null;
                    }
                });
        debug_meterial.show();
    }

    private void prgrssDailogShow(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();
    }
    private void progrssDailogCancel(){
        dialog.dismiss();
    }


    private void logOutApp(){
        globalClass.stoptimertask();
        globalClass.flow_editor.putString(globalClass.FLOW_WAY,"nothanks");
        globalClass.flow_editor.commit();
        globalClass.mightyLogin.setLogin_mode(2);
        GlobalClass.spotify_frag_status=false;
        globalClass.spotify_status=false;
        globalClass.userActivityMode = 0;
        globalClass.notifyBrowesFragment("Logout");
        globalClass.spotifyloginlogout("Logout");
        spotifySessionManager.clearSession();
        finish();
        globalClass.cleaAutoConnectData();
        parentTab.finish();
        session.logoutUser();

    }


    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                onReceiveMessageUserActivity(msgId, msgType); //your_music_fragment indicating
                Log.e(TAG,"on_Connected_user :" +msgId +" msgType "+ msgType);


            }
        };
    }


    public void onReceiveMessageUserActivity(int msgId, int msgType) {

        if (msgId == 22){
            if (msgType == 102){

                Log.e(TAG,"Dowload_Progress "+ globalClass.global_firm_download.getProgress());
                Log.e(TAG,"Dowload_Status "+ globalClass.global_firm_download.getStatus());
                //downloading_handler(globalClass.global_firm_download.getStatus());
            }
            else if( msgType == 200){

            }
        }
        if (msgId == 21){
            if (msgType == 102){

            }
        }
        if (msgId == 15){
            if(msgType == 200){
                if (globalClass.mighty_logout_status) {
                    Log.e(TAG,"Mighty Device side Logout");
                    stoptimertask();
                    globalClass.bleDisconnect();
                    logOutApp();
                }
            }
        }

    }
    private void send_set_mightylogout(){
        globalClass.mighty_logout_status = true;
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 15;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG," Event_set_structure_done ");

        initializeTimerTask();
        if (timer ==null)
            timer = new Timer();
        timer.schedule(timerTask,10000,10000);
    }

    private void initializeTimerTask(){
        timerTask = new TimerTask(){
            @Override
            public void run() {
                Log.e(TAG,"Timer is executing");
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        if(pref.getBoolean(SessionManager.IS_LOGIN, false)) {
                            stoptimertask();
                            globalClass.hardwarecompatibility(UserActivity.this, "Mighty account logout failed", "Please try to logout again");
                        }
                    }
                });
            }
        };
    }

        @Override
        public void onBackPressed() {
            Log.e(TAG,"onBackPressed");
            Intent launch_intent = new Intent(this,LaunchTabActivity.class);
            launch_intent.putExtra("onBackPressed","onBackPressed");
            startActivity(launch_intent);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mightyMsgReceiver.unRegisterReceiver(launchTabActivity);
        Log.e(TAG,"onDestroy");
    }
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            if (timerTask != null){
                timerTask.cancel();
            }
            Log.e(TAG,"Timer is stop "+timer);
        }

    }



}
