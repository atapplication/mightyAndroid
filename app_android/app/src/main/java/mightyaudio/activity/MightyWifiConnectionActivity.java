package mightyaudio.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import mightyaudio.Model.WiFi_Configuration;
import mightyaudio.Model.Wifi_Status;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;

public class MightyWifiConnectionActivity extends RootActivity
{

    public final static String TAG = MightyWifiConnectionActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static final String PREFER_NAME = "Wifi_shared_pref";
    private MightyMsgReceiver mightyMsgReceiver;

    public TextView txt_network;
    public boolean mConnected = false;
    int sent_wifi_status_req = 0;
    private Wifi_Status wifi_data;

    CheckBox cb_password_enable_disable;
    EditText edit_network;

    Typeface custom_font,custom_font_bold;
    TextView txt_cancel,txt_save,txt_title,text_head,text_head_network,text_password,cb_password;
    Button btn;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    public SharedPreferences mightyinfopref;
    public SharedPreferences.Editor mighty_info_editor;
    private final  String WifiCredential = "WifiCredential";
    private GlobalClass globalClass ;
    AlertDialog alert;
    private ProgressDialog progressDialog;
    Handler handler;
    private MaterialDialog  mMaterialDialog;
    private View promptView;
    private TextView header_txt;
    private TextView message_txt;
    private Dialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_conn_wifi_ble);
        globalClass = GlobalClass.getInstance();
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        mightyinfopref = getSharedPreferences("wifipref" , Context.MODE_PRIVATE);
        mighty_info_editor = mightyinfopref.edit();

        txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        txt_save = (TextView) findViewById(R.id.txt_save);
        txt_title = (TextView) findViewById(R.id.text_title);

        text_head = (TextView) findViewById(R.id.text_head);
        txt_network = (TextView)findViewById(R.id.txt_network);
        edit_network = (EditText)findViewById(R.id.edit_network);
        text_head_network = (TextView) findViewById(R.id.text_head_network);
        text_password = (TextView) findViewById(R.id.text_password);
        cb_password = (TextView) findViewById(R.id.cb_password);
        cb_password_enable_disable = (CheckBox)findViewById(R.id.cb_password);
        btn = (Button)findViewById(R.id.btn_conn);

        txt_cancel.setText("< BACK");
        txt_title.setText("WiFi Connection");
        txt_save.setVisibility(View.INVISIBLE);

        text_head_network.setTypeface(custom_font);
        txt_network.setTypeface(custom_font);
        text_password.setTypeface(custom_font);
        cb_password.setTypeface(custom_font);
        txt_cancel.setTypeface(custom_font);
        edit_network.setTypeface(custom_font);
        btn.setTypeface(custom_font);
        txt_title.setTypeface(custom_font_bold);
        text_head.setTypeface(custom_font_bold);
        cb_password_enable_disable.setTypeface(custom_font);
        btn.setTypeface(custom_font);

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cb_password_enable_disable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    edit_network.setInputType(129);
                    edit_network.setTypeface(custom_font);

                } else {
                    edit_network.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edit_network.setTypeface(custom_font);
                }
            }
        });

        Gson gson = new Gson();
        String target = getIntent().getStringExtra("wifiobj");
        wifi_data = gson.fromJson(target, Wifi_Status.class);
        Log.e(TAG,"network id"+wifi_data.getAp_name());
        txt_network.setText(wifi_data.getAp_name());

        String wifi_savedata = mightyinfopref.getString(WifiCredential, "");
        Wifi_Status save_wifi_data =  gson.fromJson(wifi_savedata, Wifi_Status.class);
        if(save_wifi_data != null){
            if(save_wifi_data.getAp_name().equals(wifi_data.getAp_name())){
                edit_network.setText(save_wifi_data.getWifiPssword());
            }
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String name =  wifi_data.getAp_name();
            String pass =  edit_network.getText().toString();
                if(name != null){
                    String ssid = wifi_data.getSsid();
                    //Save data
                    wifi_data.setWifiPssword(pass);
                    Gson gson = new Gson();
                    String json = gson.toJson(wifi_data);
                    mighty_info_editor.putString(WifiCredential,json);
                    mighty_info_editor.commit();

                    WiFi_Configuration wiFi_configuration = new WiFi_Configuration(ssid, name, pass,0, 0, 0);
                    globalClass.wiFi_configuration_global = wiFi_configuration;
                    send_wifistructure_to_ble();
                    prgrssDailogShow();
                    btn.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                    btn.setEnabled(false);
                    hideSoftKeboard();
                    h.sendEmptyMessageDelayed(0,45000);
                    globalClass.wifi_connected_global = null;
                }else globalClass.toastDisplay("Wi-Fi!!!");
            }
        });

        mightyMsgReceiver = new MightyMsgReceiver(this) {
            @Override
            public void onConnected() {}
            @Override
            public void onDiscovered() {
                mConnected = true;
            }
            @Override
            public void onDisconnected() {
                mConnected = false;
                finish();
            }


        };
        mightyMsgReceiver.init();

        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);
    }

    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                handleMightyMessage(msgId,msgType);
                Log.e(TAG,"Calling handle mighty message");
                Log.e(TAG,"on_Connected :" +msgId +" msgType "+ msgType);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        mightyMsgReceiver.RegisterReceiver(this);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progrssDailogCancel();
        mightyMsgReceiver.unRegisterReceiver(this);
        unregisterReceiver(ble_onreceive);
    }

    public void handleMightyMessage(int msgId, int msgType)
    {
            Log.d(TAG, "Received Mighty Message ID = " + msgId);
            if(msgId == 16)
            {
                Log.d(TAG, "Received Event ");
                if(globalClass.events_global.getWiFi_status() == 1 | globalClass.events_global.getWiFi_status() == 12) {
                    get_wifi_status();
                }
                enum_handler(globalClass.events_global.getWiFi_status());
            }
            if(msgId == 20 | msgId ==23){
                if (globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals("")) {
                    Toast.makeText(getApplicationContext(),"WiFi Connected",Toast.LENGTH_LONG).show();
                    progrssDailogCancel();
                    finish();

                }
            }
    }

    /************************ Sending Request to BLE **************************/
    public void send_wifistructure_to_ble()
    {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 1;

        Log.d(TAG,"Set Wifi structure");

        //Send the GET request to Mighty Device, Note:Only Header is Sent.
        tcpClient.SendData(mightyMessage);
        Log.d("Sent Wifi structure", "Done");
    }

    final Handler h = new Handler() {
        public void handleMessage(Message message) {
            progrssDailogCancel();
            btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
            btn.setEnabled(true);
        }
    };
    public void enum_handler(int enum_value)
    {
        switch (enum_value)
        {
            case Constants.WIFI_CONNECTED:
             //   get_wifi_status();
                Log.d(TAG,"WIFI_CONNECTED");

            //    btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
           //     btn.setEnabled(true);
             //   finish();
                break;
            case Constants.WIFI_DISCONNECTED:
                Log.d(TAG,"WIFI_DISCONNECTED");
               // Toast.makeText(getApplicationContext(),"Please Wait.....",Toast.LENGTH_LONG).show();
               // progrssDailogCancel();
           //     btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
           //     btn.setEnabled(true);

                break;
            case Constants.WIFI_WRONG_PASSWORD:
                Log.d(TAG,"WIFI_WRONG_PASSWORD");
                wifi_alert_dialog(1,"Please re-enter the WiFi password","Incorrect password");
            //    Toast.makeText(getApplicationContext(),"WIFI WRONG PASSWORD",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);

                break;
            case Constants.WIFI_SHORT_PASSWORD:
                Log.d(TAG,"WIFI_SHORT_PASSWORD");
                if(edit_network.getText().toString().equals("") )
                    wifi_alert_dialog(1,"Please enter the WiFi password","No password provided");
                else
                    wifi_alert_dialog(1,"WiFi password must be at least eight characters","Password is too short");
                //Toast.makeText(getApplicationContext(),"WIFI SHORT PASSWORD",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_CONN_FAILED:
                Log.d(TAG,"WIFI_CONN_FAILED");
                get_wifi_scan_list();
                wifi_alert_dialog(0,"Attempting to reconnect","Mighty WiFi Connection failed");
              //  Toast.makeText(getApplicationContext(),"WIFI CONNECTION FAILED",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_AUTH_NOT_VALID:
                Log.d(TAG,"WIFI_AUTH_NOT_VALID");
                Toast.makeText(getApplicationContext(),"WIFI AUTH NOT VALID",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setEnabled(true);
                break;
            case Constants.WIFI_TIMEOUT:
                Log.d(TAG,"WIFI_TIMEOUT");
                get_wifi_scan_list();
                Toast.makeText(getApplicationContext(),"WIFI TIMEOUT",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_TERMINATE:
                Log.d(TAG,"WIFI_TERMINATE");
                Toast.makeText(getApplicationContext(),"WIFI TERMINATE",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_TEMP_DISABLED:
                Log.d(TAG,"WIFI_TEMP_DISABLED");
                get_wifi_scan_list();
                Toast.makeText(getApplicationContext(),"WIFI TEMP DISABLED",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_SSID_NOT_FOUND:
                Log.d(TAG,"WIFI_SSID_NOT_FOUND");
                get_wifi_scan_list();
                wifi_alert_dialog(0,"Please scan again","WiFi Network not found");
              //  Toast.makeText(getApplicationContext(),"WIFI_SSID_NOT_FOUND",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);
                break;
            case Constants.WIFI_SCAN_FAILED:
                Log.d(TAG,"WIFI_SCAN_FAILED");
                Toast.makeText(getApplicationContext(),"WiFi scan failed try again",Toast.LENGTH_LONG).show();
                progrssDailogCancel();
                btn.setBackgroundColor(getResources().getColor(R.color.btn_color));
                btn.setEnabled(true);

                break;
            default:
        }
    }
        public void get_wifi_status() {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 20;

        tcpClient.SendData(mightyMessage);
        Log.d("Sent wifi structure", "Done");
    }

    public void get_wifi_scan_list() {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 23;
        tcpClient.SendData(mightyMessage);
        Log.d("Sent wifi scan list", "Done");
    }

    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void prgrssDailogShow() {
        Log.e(TAG,"Prgressbar clicked_wifiactivity");
        if(dialog != null){
            if(dialog.isShowing())
                return;
        }

        dialog = new Dialog(this);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();

    }
    private void progrssDailogCancel(){
        if(dialog != null){
            dialog.cancel();
            dialog.dismiss();
        }

    }
    //GlobalAlertdialog
    public void wifi_alert_dialog(final int flag, String msg, String title){
        if (mMaterialDialog != null) return;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(title);
        message_txt.setText(msg);
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag == 0){
                            finish();
                        }
                        mMaterialDialog.dismiss();
                        mMaterialDialog = null;

                    }
                });

        mMaterialDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
