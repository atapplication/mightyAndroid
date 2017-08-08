package mightyaudio.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.adapter.YourMusicAdapter;
import mightyaudio.ble.BluetoothLeService;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;
import mightyaudio.core.SpotifySessionManager;

import static mightyaudio.TCP.Constants.MSG_ID_DEVICEINFO_ID;
import static mightyaudio.TCP.Constants.MSG_ID_MIGHTY_LOGIN_ID;
import static mightyaudio.TCP.Constants.MSG_ID_SPOTIFY_LOGIN_ID;
import static android.view.View.GONE;
import static mightyaudio.activity.MightyWifiConnectionActivity.EXTRAS_DEVICE_ADDRESS;
import static mightyaudio.activity.MightyWifiConnectionActivity.EXTRAS_DEVICE_NAME;


public class SetupConnectActivity extends RootActivity
{
    public LeDeviceListAdapter mLeDeviceListAdapter;
    public BluetoothAdapter mBluetoothAdapter;
    public Handler mHandler;
    private final static String TAG = SetupConnectActivity.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mBluetoothLeScanner;

    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private Dialog dialog;

    ListView listView;
    Toolbar toolbar;
    TextView txt_cancel, txt_title, txt_update,text_ble_not_enable,text_enable_subhead;
    Typeface custom_font, custom_font_bold;
    GlobalClass globalClass;
    private IntentFilter intentFilter ,intentFilter_compatibility;
    private BroadcastReceiver ble_scan_list_receiver,receiver_compatibility;
    ProgressBar progressBar;
    Button btn_continue;
    LinearLayout need_mighty;
    FrameLayout ble_not_enabled,ble_discoverable_frame;
    private TextView cancel_txt,header_txt,save_txt,wait_led,txt_need,connect_mighty,spinner_text,ble_discover_text1,ble_discover_text2;
    ImageView mighty_img,success;
    String device_name, device_address;
    View mProgressBarFooter;
    private Activity activity = this;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    private SharedPreferences spotifyPref;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private int timout_flag= 0;
    private MaterialDialog  mMaterialDialog,mightyMaterial;
    private View promptView, mightyPromptView;
    private TextView dailogheader_txt,mightyheader_text;
    private TextView message_txt,mightyMessage_text,established_connection_text;
    private LayoutInflater mInflator;
    final Handler setup_handler = new Handler();
    private static Timer setup_timer= new Timer();
    private static TimerTask setup_timerTask;
    Map<String,BluetoothDevice> ble_hash_map = new LinkedHashMap<String,BluetoothDevice>();
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> local_ble_list = new ArrayList<BluetoothDevice>();
    IntentFilter intentFilter_disconnect;
    BroadcastReceiver receiver_disconnect;



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                stoptimertask();
                cancel_txt.setEnabled(false);
                mConnected = true;
                need_mighty.setVisibility(View.GONE);
                established_connection_text.setVisibility(View.VISIBLE);
                mighty_img.setVisibility(View.INVISIBLE);
                wait_led.setVisibility(View.VISIBLE);
                success.setVisibility(View.GONE);
                btn_continue.setVisibility(View.GONE);
                device_name = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                device_address = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.e(TAG,"disconnect_setup");

               // globalClass.toastDisplay("Mighty is Disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                stoptimertask();
                cancel_txt.setEnabled(false);
                mProgressBarFooter.setVisibility(GONE);
                if(!ble_hash_map.isEmpty())
                    ble_hash_map.clear();
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.addDevice(globalClass.mighty_ble_device);
                mLeDeviceListAdapter.notifyDataSetChanged();
                need_mighty.setVisibility(GONE);
                established_connection_text.setVisibility(View.GONE);
                mighty_img.setVisibility(View.VISIBLE);
                wait_led.setVisibility(View.INVISIBLE);
                success.setVisibility(View.VISIBLE);
                btn_continue.setVisibility(View.VISIBLE);
                btn_continue.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                btn_continue.setEnabled(false);
                globalClass.Status = true;
                globalClass.scanLeDevice(false);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            }
        }
    };

    public SetupConnectActivity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    }
    public void startTimer() {
        Log.e(TAG,"timer start_setup"+ mLeDevices.size()+" "+ ble_hash_map.size());
      /*  if(!globalClass.Status)
            globalClass.scanLeDevice(true);*/

        setup_initializeTimerTask();
        if (setup_timer ==null)
            setup_timer = new Timer();
        if(!globalClass.Status)
            setup_timer.schedule(setup_timerTask, 0, 11000);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        Log.e(TAG,"timer Stop_setup"+ mLeDevices.size()+" "+ ble_hash_map.size());
        globalClass.scanLeDevice(false);
        if (setup_timer != null) {
            setup_timer.cancel();
            setup_timer.purge();
            setup_timer = null;
            if (setup_timerTask != null){
                setup_timerTask.cancel();
                setup_timerTask = null;
            }
        }
    }

    public void setup_initializeTimerTask() {
        setup_timerTask = new TimerTask() {
            public void run() {
                setup_handler.post(new Runnable() {
                    public void run() {

                        Log.e(TAG,"timer___"+ mLeDevices.size()+" "+ ble_hash_map.size());
                        if (globalClass.mighty_ble_device == null) {
                            if(!ble_hash_map.isEmpty()) {
                                ble_discoverable_frame.setVisibility(View.GONE);
                                mProgressBarFooter.setVisibility(View.VISIBLE);
                                for (int j = 0; j < mLeDevices.size(); j++) {
                                    if (ble_hash_map.get(mLeDevices.get(j).getAddress()) == null)
                                        local_ble_list.add(mLeDevices.get(j));
                                }
                                ble_hash_map.clear();

                                for (int i = 0; i < local_ble_list.size(); i++) {
                                    mLeDevices.remove(local_ble_list.get(i));
                                    ble_hash_map.put(local_ble_list.get(i).getAddress(), local_ble_list.get(i));
                                }

                                if (!local_ble_list.isEmpty())
                                    local_ble_list.clear();
                                if(mLeDevices.isEmpty()){
                                    ble_discoverable_frame.setVisibility(View.VISIBLE);
                                    mProgressBarFooter.setVisibility(View.GONE);
                                }

                                Log.e(TAG, " if timer  mledevice ");
                            }else {
                                if (!mLeDevices.isEmpty())
                                    mLeDevices.clear();
                                ble_discoverable_frame.setVisibility(View.VISIBLE);
                                mProgressBarFooter.setVisibility(View.GONE);
                                if (!globalClass.mBluetoothAdapter.isEnabled()) {
                                    ble_discoverable_frame.setVisibility(View.GONE);
                                    mProgressBarFooter.setVisibility(View.GONE);
                                }                                }
                            Log.e(TAG," mLeDevices value_setup "+mLeDevices.size()+" "+ble_hash_map.size());
                            //if(mLeDevices.isEmpty())
                            globalClass.scanLeDevice(false);
                            globalClass.scanLeDevice(true);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }else {
                            Log.e(TAG," else timer  mledevice ");
                        }
                    }
                });

            }
        };
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalClass = GlobalClass.getInstance();
        Log.e(TAG,"Oncreate");
        spotifyPref = getSharedPreferences(SpotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");

        setContentView(R.layout.activity_setup_connect);
        mHandler = new Handler();
        listView = (ListView) findViewById(R.id.listView);
        session = new SessionManager(this);
       // initToolBar();
        wait_led=(TextView) findViewById(R.id.wait_led);
        mProgressBarFooter = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.ble_spinner, null, false);
        spinner_text = (TextView) mProgressBarFooter.findViewById(R.id.spinner_text);
        spinner_text.setVisibility(GONE);
        spinner_text.setTypeface(custom_font);
        listView.addFooterView(mProgressBarFooter);
        txt_need=(TextView) findViewById(R.id.txt_need);
        text_ble_not_enable=(TextView) findViewById(R.id.text_ble_not_enable);
        text_enable_subhead=(TextView) findViewById(R.id.text_enable_subhead);
        ble_discover_text1 = (TextView)findViewById(R.id.ble_discover_text1);
        ble_discover_text2 = (TextView)findViewById(R.id.ble_discover_text2);
        established_connection_text = (TextView) findViewById(R.id.established_connection_text);
        txt_need.setTypeface(custom_font_bold);
        text_ble_not_enable.setTypeface(custom_font_bold);
        connect_mighty=(TextView) findViewById(R.id.connect_mighty);
        connect_mighty.setTypeface(custom_font);
        ble_discover_text1.setTypeface(custom_font_bold);
        ble_discover_text2.setTypeface(custom_font);
        text_enable_subhead.setTypeface(custom_font);
        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font_bold);
        established_connection_text.setTypeface(custom_font);
        save_txt.setTypeface(custom_font);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);
        wait_led.setTypeface(custom_font_bold);


        mighty_img = (ImageView) findViewById(R.id.mighty_img);
        success = (ImageView) findViewById(R.id.success);

        established_connection_text.setVisibility(View.GONE);
        mighty_img.setVisibility(View.GONE);
        success.setVisibility(View.GONE);
        wait_led.setVisibility(View.GONE);

        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setTypeface(custom_font);
        btn_continue.setVisibility(View.GONE);
        need_mighty = (LinearLayout) findViewById(R.id.need_mighty);
        ble_not_enabled = (FrameLayout) findViewById(R.id.ble_not_enabled);
        ble_discoverable_frame = (FrameLayout) findViewById(R.id.ble_discoverable_frame);
        ble_discoverable_frame.setVisibility(View.GONE);
        ble_not_enabled.setVisibility(View.GONE);
        need_mighty.setVisibility(View.VISIBLE);
        if(GlobalClass.mighty_ble_device != null ) {
            Log.e(TAG,"if part exectuing "+globalClass.mighty_ble_device.getName());
            mLeDeviceListAdapter.addDevice(globalClass.mighty_ble_device);
            globalClass.Status = true;
            globalClass.scanLeDevice(false);
            need_mighty.setVisibility(GONE);
            established_connection_text.setVisibility(View.GONE);
            mighty_img.setVisibility(View.VISIBLE);
            wait_led.setVisibility(View.INVISIBLE);
            success.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.VISIBLE);
            mProgressBarFooter.setVisibility(View.GONE);
            ble_discoverable_frame.setVisibility(GONE);
            mLeDeviceListAdapter.notifyDataSetChanged();
        }else {
            Log.e(TAG,"else part exectuing ");
            if(!globalClass.Status) {
                //globalClass.scanLeDevice(true);
                //startTimer();
            }
            ble_hash_map.putAll(globalClass.scan_ble_list);
            // linkedHashble.addAll(globalClass.scan_ble_list.values());
            mLeDevices.addAll(ble_hash_map.values());
            globalClass.scan_ble_list.clear();
            mProgressBarFooter.setVisibility(View.VISIBLE);
            spinner_text.setVisibility(GONE);
            if(mLeDevices.isEmpty()  && globalClass.mBluetoothAdapter.isEnabled() ) {
                ble_discoverable_frame.setVisibility(View.VISIBLE);
                mProgressBarFooter.setVisibility(View.GONE);
            }
        }
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(getApplicationContext(), SetupWifiActivity.class);
                startActivity(nintent);
            }
        });
        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousActivity();
                //finish();
            }
        });

        //Fro runtime location Permission
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT ) {
            // Android M Permission check
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                builder.show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        scanListBle();
        registerReceiver(ble_scan_list_receiver,intentFilter);

        onreceive_ble();

        //ble_disconnect_broadcast
        ble_disconnect();

        //Hardware define bradcast for showing alerdailog
        compatibilityBroadCast();
        registerReceiver(receiver_compatibility,intentFilter_compatibility);
    }

    private void ble_disconnect() {
        intentFilter_disconnect = new IntentFilter("ble.disconnect.successful");
        receiver_disconnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                progrssDailogCancel();
                Log.e(TAG, "Disconnect_broadcast_triggered_setup");
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
                startTimer();
                cancel_txt.setEnabled(true);
                mProgressBarFooter.setVisibility(View.VISIBLE);
                spinner_text.setVisibility(GONE);
                globalClass.Status = false;
                //globalClass.scanLeDevice(true);
                mLeDeviceListAdapter.clear();
                need_mighty.setVisibility(View.VISIBLE);
                established_connection_text.setVisibility(View.GONE);
                mighty_img.setVisibility(View.GONE);
                wait_led.setVisibility(View.INVISIBLE);
                success.setVisibility(View.GONE);
                btn_continue.setVisibility(GONE);
                if(!ble_hash_map.isEmpty())
                    ble_hash_map.clear();
                mHandler.post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                    @Override
                    public void run() {
                        listView.setAdapter(mLeDeviceListAdapter);
                        globalClass.Status = false;
                    }
                });
            }
        };
    }

    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");

                Log.e(TAG,"msgId msgType "+msgId+" "+msgType);

                switch (msgId){

                    case MSG_ID_DEVICEINFO_ID:
                        if (Float.valueOf(globalClass.device_info.getSW_Version()) == 0.64f){
                            btn_continue.setBackgroundColor(getResources().getColor(R.color.btn_color));
                            btn_continue.setEnabled(true);
                            cancel_txt.setEnabled(true);
                        }

                    break;
                    case MSG_ID_MIGHTY_LOGIN_ID :
                        if(msgType == 200 ){
                            if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)){
                                prgrssDailogShow();
                                btn_continue.setBackgroundColor(getResources().getColor(R.color.btn_color));
                                btn_continue.setEnabled(true);
                                cancel_txt.setEnabled(true);
                            }else{
                                btn_continue.setBackgroundColor(getResources().getColor(R.color.btn_color));
                                btn_continue.setEnabled(true);
                                cancel_txt.setEnabled(true);
                            }
                        }else if(msgType == 302)
                        {
                            alertDailog(1);
                        }else if(msgType == 304)
                            setupalertdialog(SetupConnectActivity.this,"This Mighty is registered to another user","That user must connect to this Mighty and logout of their account, then you will be able to connect.");
//Sathish changed
                        break;
                    case MSG_ID_SPOTIFY_LOGIN_ID :
                        if(msgType == 200){
                            progrssDailogCancel();
                        }else if (msgType == 301) {
                            progrssDailogCancel();
                            alertDailog(2);
                        }
                        break;
                }
            }
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
    private void scanListBle(){
        intentFilter = new IntentFilter("ble.scan.list");
        ble_scan_list_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent bleScanIntent) {
                Log.e(TAG,"On Receive hit");
                ble_discoverable_frame.setVisibility(View.GONE);
                mProgressBarFooter.setVisibility(View.VISIBLE);
                BluetoothDevice device = bleScanIntent.getParcelableExtra("blelist");
                Log.e(TAG,"Device_uuid" + device.getName() +"addr "+device.getAddress());
                ble_hash_map.put(device.getAddress(), device);
                mLeDeviceListAdapter.addDevice(device);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
        };
    }

private void alertDailog(final int flag){
    LayoutInflater layoutInflater = LayoutInflater.from(activity);
    mightyPromptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
    mightyheader_text=(TextView)mightyPromptView.findViewById(R.id.text_header);
    mightyMessage_text=(TextView)mightyPromptView.findViewById(R.id.text_message);
    mightyheader_text.setText("Are you sure?");
    mightyMessage_text.setText("All playlists will be removed from this Mighty if you proceed.");
    mightyMaterial = new MaterialDialog(activity)
            .setView(mightyPromptView)
            .setPositiveButton(getString(R.string.continue__button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag == 1){
                        globalClass.mightyLogin.setLogin_mode(1);
                        TCPClient tcpClient = new TCPClient();
                        MightyMessage mightyMessage = new MightyMessage();
                        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
                        mightyMessage.MessageID = 15;
                        tcpClient.SendData(mightyMessage);
                        Log.e(TAG, " MightyLogin_set_structure_done ");
                    }else if(flag == 2){
                        globalClass.spotify_status=true;
                        globalClass.spotifyLogin.setLogin_mode(1);
                        if (!globalClass.mighty_playlist.isEmpty())
                            globalClass.mighty_playlist.clear();
                        if(!YourMusicAdapter.selectedlist.isEmpty())
                            YourMusicAdapter.selectedlist.clear();
                        TCPClient tcpClient = new TCPClient();
                        MightyMessage mightyMessage = new MightyMessage();
                        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                        mightyMessage.MessageID = 10;
                        tcpClient.SendData(mightyMessage);
                        Log.e(TAG, " Memory_get_structure_done ");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                globalClass.send_set_spotify();
                            }
                        }).start();

                    }
                    mightyMaterial.dismiss();

                }
            })
            .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(flag == 1){
                        globalClass.mighty_info_editor.clear();
                        globalClass.mighty_info_editor.commit();
                        globalClass.bleDisconnect();
                        finish();
                    }else if(flag == 2){
                        globalClass.spotifySessionManager.clearSession();
                        globalClass.send_set_spotifylogout();
                        globalClass.notifyBrowesFragment("Logout");
                        globalClass.spotifyloginlogout("Logout");
                        GlobalClass.spotify_frag_status=false;
                        globalClass.spotify_status=false;
                        globalClass.spotify_tick();
                    }
                    mightyMaterial.dismiss();
                }
            });

    mightyMaterial.show();
    }

    //GlobalAlertdialog
    public void setupalertdialog(Activity activity, String tittle,String msg){

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        dailogheader_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        dailogheader_txt.setText(tittle);
        message_txt.setText(msg);
        mMaterialDialog = new MaterialDialog(activity)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        globalClass.bleDisconnect();
                        mMaterialDialog.dismiss();

                    }
                });

        mMaterialDialog.show();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        stoptimertask();
        unregisterReceiver(ble_scan_list_receiver);
        unregisterReceiver(receiver_compatibility);

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, MightyMsgReceiver.makeGattUpdateIntentFilter());
        Log.e(TAG,"onResume");
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(Bluetooth_BroadcastReceiver, filter);
        registerReceiver(ble_onreceive,intentFilter1);
        registerReceiver(receiver_disconnect, intentFilter_disconnect);
        if(globalClass.mighty_ble_device != null) {
            stoptimertask();
           // cancel_txt.setEnabled(false);
            mProgressBarFooter.setVisibility(GONE);
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.addDevice(globalClass.mighty_ble_device);
            mLeDeviceListAdapter.notifyDataSetChanged();
            need_mighty.setVisibility(GONE);
            established_connection_text.setVisibility(View.GONE);
            mighty_img.setVisibility(View.VISIBLE);
            wait_led.setVisibility(View.INVISIBLE);
            success.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.VISIBLE);
            globalClass.Status = true;
   //         globalClass.scanLeDevice(false);
        }else{
            progrssDailogCancel();
            Log.e(TAG, "Disconnect_broadcast_triggered_setup");
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.notifyDataSetChanged();
            startTimer();
            cancel_txt.setEnabled(true);
            mProgressBarFooter.setVisibility(View.VISIBLE);
            spinner_text.setVisibility(GONE);
            globalClass.Status = false;
            //globalClass.scanLeDevice(true);
            mLeDeviceListAdapter.clear();
            need_mighty.setVisibility(View.VISIBLE);
            established_connection_text.setVisibility(View.GONE);
            mighty_img.setVisibility(View.GONE);
            wait_led.setVisibility(View.INVISIBLE);
            success.setVisibility(View.GONE);
            btn_continue.setVisibility(GONE);
            mHandler.post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                @Override
                public void run() {
                    listView.setAdapter(mLeDeviceListAdapter);
                    globalClass.Status = false;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
        stoptimertask();
        unregisterReceiver(mGattUpdateReceiver);
        unregisterReceiver(receiver_disconnect);
        //globalClass.scanLeDevice(false);
        //mLeDeviceListAdapter.clear();
        unregisterReceiver(Bluetooth_BroadcastReceiver);
        unregisterReceiver(ble_onreceive);
    }
    private final BroadcastReceiver Bluetooth_BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG,"BluetoothAdapter.STATE_OFF ");
                        ble_not_enabled.setVisibility(View.VISIBLE);
                        need_mighty.setVisibility(GONE);
                        mProgressBarFooter.setVisibility(View.GONE);
                        globalClass.scanLeDevice(false);
                        if(!ble_hash_map.isEmpty())
                            ble_hash_map.clear();
                        if(!mLeDevices.isEmpty())
                            mLeDevices.clear();
                        stoptimertask();
                        ble_discoverable_frame.setVisibility(View.GONE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG,"BluetoothAdapter.STATE_ON ");
                        ble_not_enabled.setVisibility(View.GONE);
                        need_mighty.setVisibility(View.VISIBLE);
                        globalClass.scanLeDevice(true);
                        startTimer();
                        mProgressBarFooter.setVisibility(View.VISIBLE);
                        if (mLeDevices.isEmpty()) {
                            ble_discoverable_frame.setVisibility(View.VISIBLE);
                            mProgressBarFooter.setVisibility(View.GONE);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }

            }
        }
    };


    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {

        public LeDeviceListAdapter() {
         //   mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.isEmpty()) {
                for (int i = 0; i < mLeDevices.size(); i++) {
                    Log.e(TAG, "adapter adddress " + mLeDevices.get(i).getAddress() + " device address" + device.getAddress());
                    if (device.getAddress().toString().trim() != "00:00:00:00:00:00") {
                        if (!mLeDevices.contains(device) && device.getName() != null) {
                            mLeDevices.add(device);
                        }
                    }
                }
            } else if (device.getName() != null) {
                if (!mLeDevices.contains(device)) {
                    mLeDevices.add(device);
                }
            }
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.ble_listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.text_arrow = (TextView) view.findViewById(R.id.text_arrow);
                viewHolder.ble_image = (ImageView) view.findViewById(R.id.blue_trick);
                viewHolder.bt_delete = (ImageView) view.findViewById(R.id.bt_delete);
                viewHolder.text_arrow.setVisibility(View.GONE);
                viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.img_plus = (ImageView) view.findViewById(R.id.img_plus);
                viewHolder.img_plus_rect = (FrameLayout) view.findViewById(R.id.img_plus_rect);
                viewHolder.ble_device_swipe = (SwipeLayout) view.findViewById(R.id.ble_device_swipe);
                viewHolder.ble_device_swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
                viewHolder.ble_device_swipe.setSwipeEnabled(false);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.deviceName.setTypeface(custom_font);
            viewHolder.text_arrow.setTypeface(custom_font);


            //Some time getting no name
            if (mLeDevices.get(i).getName() != null && mLeDevices.get(i).getName().length() != 0) {
                viewHolder.deviceName.setText(mLeDevices.get(i).getName());
                Log.e(TAG, "device Name " + mLeDevices.get(i).getName());
            }



            if (globalClass.mighty_ble_device != null) {
                viewHolder.ble_image.setVisibility(View.VISIBLE);
                viewHolder.text_arrow.setTypeface(custom_font);
                viewHolder.deviceName.setText(globalClass.ble_device_name);
                viewHolder.ble_device_swipe.setSwipeEnabled(true);
                viewHolder.text_arrow.setVisibility(View.VISIBLE);
                viewHolder.img_plus_rect.setVisibility(View.GONE);
                viewHolder.img_plus.setVisibility(View.INVISIBLE);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                globalClass.Status = false;
                Log.e(TAG, "if refreshing ");
            } else {
                viewHolder.ble_image.setVisibility(View.GONE);
                viewHolder.text_arrow.setVisibility(View.GONE);
                viewHolder.img_plus.setVisibility(View.VISIBLE);
                viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "else refreshing ");
                if (globalClass.Status) {
                    viewHolder.img_plus.setVisibility(View.INVISIBLE);  //setImageResource(android.R.color.transparent);
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    viewHolder.ble_device_swipe.setSwipeEnabled(true);
                }
                viewHolder.ble_image.setVisibility(View.GONE);
            }

            viewHolder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(globalClass.mighty_ble_device != null) {
                        if (cancel_txt.isEnabled())
                            startActivity(new Intent(getApplicationContext(), DeviceRenameActivity.class));
                    }
                }
            });
            Log.e(TAG,"viewHolder.ble_device_swipe "+!cancel_txt.isEnabled());
            viewHolder.img_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (globalClass.Status == false) {
                        stoptimertask();
                        final BluetoothDevice device = mLeDevices.get(i);  //mLeDeviceListAdapter.getDevice(i);
                     //   if (device == null)
                    //        return;
                        globalClass.bleConnect(device);
                        cancel_txt.setEnabled(false);
                        globalClass.Status = true;
                        mProgressBarFooter.setVisibility(GONE);
                        mLeDeviceListAdapter.clear();
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }


                }
            });

            viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    globalClass.Status = false;
                    final BluetoothDevice device = mLeDevices.get(i);
                    if (device == null) return;
                    globalClass.bleDisconnect();
                    viewHolder.text_arrow.setVisibility(View.GONE);
                    viewHolder.img_plus.setVisibility(View.VISIBLE);
                    viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);
//                    viewHolder.img_plus.setImageResource(R.drawable.list_item_plus);
                    clear();
                    if (!GlobalClass.hasmap_bt_scan_headset_lists.isEmpty())
                        GlobalClass.hasmap_bt_scan_headset_lists.clear();
                    //BtdeviceListAdapter.clear();
                    notifyDataSetInvalidated();
                    GlobalClass.mighty_ble_device = null;
                }
            });

            return view;
        }
    }

    class ViewHolder {
        TextView deviceName;
        ImageView ble_image;
        ImageView img_plus;
        FrameLayout img_plus_rect;
        TextView text_arrow;
        ProgressBar progressBar;
        SwipeLayout ble_device_swipe;
        ImageView bt_delete;

    }

    @Override
    public void onBackPressed() {
        if(cancel_txt.isEnabled())
        previousActivity();
        else return;
    }

    private void previousActivity(){
        /*Intent intent = new Intent(getBaseContext(),PlugYourMightActivity.class);
        startActivity(intent);*/
        finish();
    }

    private void prgrssDailogShow(){
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

    private void compatibilityBroadCast(){
        intentFilter_compatibility=new IntentFilter("mighty.hardware.compatibility");
        receiver_compatibility=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                globalClass.hardwarecompatibility(SetupConnectActivity.this,"Can not procceed",getString(R.string.hardeware_compality));
            }
        };
    }
}