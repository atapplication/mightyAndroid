package mightyaudio.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;

public class SetUpActivity extends RootActivity implements View.OnClickListener
{
    public final static String TAG = SetUpActivity.class.getSimpleName();
    Typeface custom_font_light;
    GlobalClass globalClass;
    private SharedPreferences usingpref;
    private SessionManager session;
    private SharedPreferences pref;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView txt_no_thanks;
    private MaterialDialog  mMaterialDialog,locationMaterialDialog;
    private View promptView,location_popup;
    private TextView header_txt,text_static_head;
    private TextView message_txt;
    private BroadcastReceiver destroyActivity_reciver;
    private IntentFilter destroyActivity_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        globalClass = GlobalClass.getInstance();
        //globalClass.marshMellowLocationPermission(this);
        pref = getSharedPreferences(session.PREF_NAME, Context.MODE_PRIVATE);
        usingpref = getSharedPreferences(globalClass.USING_PREF, Context.MODE_PRIVATE);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        Log.e(TAG, "Using Staus " + usingpref.getInt("using_satatus", 0) + " " + globalClass.userActivityMode);
        txt_no_thanks = (TextView) findViewById(R.id.text_no_thanks);
        text_static_head = (TextView) findViewById(R.id.text_static_head);
        Button btn_setup = (Button) findViewById(R.id.setup);

        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        txt_no_thanks.setPaintFlags(txt_no_thanks.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );
        txt_no_thanks.setTypeface(custom_font_light);
        text_static_head.setTypeface(custom_font_light);
        btn_setup.setTypeface(custom_font_light);

        btn_setup.setOnClickListener(this);
        txt_no_thanks.setOnClickListener(this);


        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT ) {
            Log.e(TAG,"Show Aler Dailog");
            // Android M Permission check
            // Get Location Manager and check for GPS & Network location services
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // Build the alert dialog
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                location_popup = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                header_txt=(TextView)location_popup.findViewById(R.id.text_header);
                message_txt=(TextView)location_popup.findViewById(R.id.text_message);
                header_txt.setText("Hey! Go turn on your GPS");
                message_txt.setText("Enable Location Services and GPS If you want to use Mighty");
                locationMaterialDialog = new MaterialDialog(this)
                        .setView(location_popup)
                        .setPositiveButton(getString(R.string.continue__button), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                locationMaterialDialog.dismiss();
                                locationMaterialDialog = null;

                            }
                        });
                locationMaterialDialog.show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, globalClass.PERMISSION_REQUEST_COARSE_LOCATION);
        }

        Log.e(TAG," onResume globalClass.userActivityMode  "+globalClass.userActivityMode +" "+usingpref.getInt("using_satatus",0));
        if(usingpref.getInt("using_satatus",0) == 1){ // Here willn't get 1 untill setup flow finish
            if(globalClass.userActivityMode != 1)
                checkLoginStatus();
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
                if(pref.getBoolean(SessionManager.IS_LOGIN, false) && (usingpref.getInt("using_satatus",0) == 1)){
                    globalClass.scanLeDevice(true);
                }
            }
        }
        destryActivity();
        registerReceiver(destroyActivity_reciver,destroyActivity_filter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        //For Blutooth Checking
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //finish();
            return;
        }
    }
    private void destryActivity(){
        destroyActivity_filter=new IntentFilter("destroy.setup.activity");
        destroyActivity_reciver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
    }

    private void checkLoginStatus(){
            session = new SessionManager(getApplicationContext());
            session.checkLogin();
            if(pref.getBoolean(SessionManager.IS_LOGIN, false)) {
                globalClass.scanLeDevice(true);
                GlobalClass.lower_tool_bar_status = 1;
                Intent launch_intent = new Intent(getApplicationContext(),LaunchTabActivity.class);
                launch_intent.putExtra("onBackPressed","onBackPressed");
                startActivity(launch_intent);
                globalClass.flow_editor.putString(globalClass.FLOW_WAY,"nothanks");
                globalClass.flow_editor.commit();
                finish();
            }
        }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
        //globalClass.userActivityMode = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(destroyActivity_reciver);
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.setup :

                    globalClass.userActivityMode = 1;
                    globalClass.auto_connect_force_cancel= 2;
                    startActivity(new Intent(getApplicationContext(), MightyGuide1.class));
                    //startActivity(new Intent(getApplicationContext(), MightySoftwareUpdateActivity.class));
                    globalClass.flow_editor.putString(globalClass.FLOW_WAY,"setup");
                    globalClass.flow_editor.commit();
                break;
            case R.id.text_no_thanks :
                Log.e(TAG,"No Thanks clicked");
                if(usingpref.getInt("using_satatus",0) != 1)
                setupNotcompleted();
                else seletedNoThanks();

                break;
        }
    }

    public void setupNotcompleted(){
        if(mMaterialDialog != null ) {
            return;
        }
        Log.e(TAG, "mighty Registration bradcast alert");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText("Are you sure?");
        message_txt.setText("The Setup link is the quickest way to get you up and running");
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.continue__button), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seletedNoThanks();
                        mMaterialDialog.dismiss();
                        mMaterialDialog = null;

                    }
                })
        .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                mMaterialDialog = null;

            }
        });

        mMaterialDialog.show();
    }

    private void seletedNoThanks(){
        if(pref.getBoolean(SessionManager.IS_LOGIN, false)){
            globalClass.scanLeDevice(true);
            globalClass.lower_tool_bar_status = 1;
            globalClass.auto_connect_force_cancel =1;
            Intent launch_intent = new Intent(getApplicationContext(),LaunchTabActivity.class);
            launch_intent.putExtra("onBackPressed","onBackPressed");
            startActivity(launch_intent);
            //globalClass.userActivityMode = 1;
            globalClass.flow_editor.putString(globalClass.FLOW_WAY,"nothanks");
            globalClass.flow_editor.commit();
            finish();
        }else{
            globalClass.userActivityMode = 1;
            globalClass.lower_tool_bar_status = 1;
            globalClass.auto_connect_force_cancel =1;
            globalClass.flow_editor.putString(globalClass.FLOW_WAY,"nothanks");
            globalClass.flow_editor.commit();
            startActivity(new Intent(getApplicationContext(), MightyLoginActivity.class));
        }
    }


}
