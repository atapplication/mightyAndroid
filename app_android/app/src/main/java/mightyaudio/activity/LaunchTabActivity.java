package mightyaudio.activity;

import android.app.Activity;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SessionManager;
import mightyaudio.core.SpotifyAuthHelper;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;
import mightyaudio.receiver.SpotifyIntentService;

public class LaunchTabActivity extends TabActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private static final String TAG = LaunchTabActivity.class.getSimpleName();
    public static TabHost host;//==change
    TabHost.TabSpec spec;
    GlobalClass globalClass = GlobalClass.getInstance();
    private SessionManager session;
    private SharedPreferences pref,mightyinfopref;
    private SpotifySessionManager spotifySessionManager;
    private SharedPreferences spotifyPref;
    private boolean  isConnected;
    private String passedArg;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private MaterialDialog  locationMaterialDialog;
    private View location_popup;
    private TextView header_txt;
    private TextView message_txt;



    @Override
    protected void onResume() {
        super.onResume();

        globalClass.setConnectivityListener(this);
        session.checkLogin();
        //globalClass.setConnectivityListener(this);
        Log.e(" On resume ","Launch act " + GlobalClass.lower_tool_bar_status+" "+host.getCurrentTab());
        host.setCurrentTab(GlobalClass.lower_tool_bar_status);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        globalClass = GlobalClass.getInstance();
        globalClass.marshMellowLocationPermission(this);
        session = new SessionManager(this);
        pref = getSharedPreferences(session.PREF_NAME, Context.MODE_PRIVATE);
        session.checkLogin();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        spotifySessionManager = new SpotifySessionManager(this);
        spotifyPref = getSharedPreferences(spotifySessionManager.PREF_NAME, Context.MODE_PRIVATE);
        mightyinfopref = getSharedPreferences(globalClass.MIGHTY_INFO_PREF, Context.MODE_PRIVATE);


        host = (TabHost) findViewById(R.id.tabs);
        host = getTabHost();
        host.setup();
        host.setup(this.getLocalActivityManager());

        //Tab one
        spec = host.newTabSpec("Tab One");
        spec.setContent(new Intent(this, ConnectActivity.class));
        spec.setIndicator("", getResources().getDrawable(R.drawable.foot_connect));
        host.addTab(spec);

        spec = host.newTabSpec("Tab Two");
        spec.setContent(new Intent(this, MusicActivity.class));
        spec.setIndicator("", getResources().getDrawable(R.drawable.foot_music));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(new Intent(this, UserActivity.class));
        spec.setIndicator("", getResources().getDrawable(R.drawable.foot_user));
        host.addTab(spec);


        host.setCurrentTab(1);
        host.getTabWidget().setCurrentTab(1);
        host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.bg_foot_color);


        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
                    if (host.getCurrentTab() == i) {
                        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundResource(R.drawable.bg_foot_color);
                    } else
                        host.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#00ffffff"));
                }
            }
        });

        // disable music activity for 0.64V
        host.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Activated " + host.getTabWidget().getChildAt(1).isActivated());
                if (globalClass.lower_version == true) {
                    globalClass.alertDailogSingleText(getString(R.string.please_update_your_mighty),LaunchTabActivity.this);
                } else {
                    host.setCurrentTab(1);
                    host.getTabWidget().setCurrentTab(1);
                    host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.bg_foot_color);
                }
            }
        });

        //MIghty Cloud Login
        callingForRefreshToken();

        if (spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN, false)) {
            GlobalClass.spotify_status = true;
            GlobalClass.spotify_frag_status = true;
            isConnected = ConnectivityReceiver.isConnected();
            if (isConnected)
                retrivePlayListAppLaunc();
        }
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            // Android M Permission check
            // Get Location Manager and check for GPS & Network location services
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
        }
        Intent close_login_activity = new Intent();
        close_login_activity.setAction("finish_activity");
        sendBroadcast(close_login_activity);
    }

    private void callingForRefreshToken(){
        if(pref.getBoolean(SessionManager.IS_LOGIN, false)){

            Log.e(TAG,"Login "+pref.getBoolean(SessionManager.IS_LOGIN, false));
            isConnected = ConnectivityReceiver.isConnected();
            if(isConnected)
                globalClass.refreshAccesssToken();
        }
    }

    private  void retrivePlayListAppLaunc(){
        long refresh_token_time = spotifyPref.getLong(SpotifySessionManager.CURRENTTIME,0l);
        Log.e(TAG,"Expire time token "+refresh_token_time);
        if(globalClass.arrayPlayList.isEmpty()){
            if(System.currentTimeMillis() <= refresh_token_time ){
                Log.e(TAG,"Still time is there "+refresh_token_time);
                globalClass.retrivePlayListFromSpotify(0,50,0);
            }else {
                Log.e(TAG,"Still Time is Over "+refresh_token_time);
                Intent spotifyServiceIntent = new Intent(this, SpotifyIntentService.class);
                spotifyServiceIntent.putExtra("launch","launch");
                this.startService(spotifyServiceIntent);
            }
        }
    }

    private void afterChackConnection(){
        long refresh_token_time = spotifyPref.getLong(SpotifySessionManager.CURRENTTIME,0l);
        if(System.currentTimeMillis() <= refresh_token_time ){
            if(globalClass.arrayPlayList.isEmpty()){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        globalClass.retrivePlayListFromSpotify(0,50,0);
                    }
                });
            }
        }else{
            Intent spotifyServiceIntent = new Intent(this, SpotifyIntentService.class);
            spotifyServiceIntent.putExtra("launch","launch");
            this.startService(spotifyServiceIntent);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"Activity onPause");
        GlobalClass.lower_tool_bar_status =host.getCurrentTab();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"Activity onstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Activity onDestroy");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        globalClass.showSnack(isConnected,findViewById(android.R.id.tabcontent));
        if(isConnected){
            //MIghty Cloud Login
            Log.e(TAG,"onNetworkConnectionChanged");
            callingForRefreshToken();
            afterChackConnection();
            if(globalClass.wifi_connected_global != null && !globalClass.upgradeProcess && globalClass.wifi_connected_global.getAp_name() != null &&  !globalClass.wifi_connected_global.ap_name.equals("") && !globalClass.softwareupdateTrriger){
                globalClass.mightyRegistration("LaunchTab");
                globalClass.upgradeProcess = true;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"Activity onNewIntent Called ");
        passedArg = intent.getExtras().getString("onBackPressed");
        if(passedArg != null){
            Log.e(TAG,"onBackPressed"+ passedArg);
            globalClass.lower_tool_bar_status =1;
        }else{

            handleSpotifyAuthResult(SpotifyAuthHelper.getResultForBrowserIntent(intent));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == globalClass.REQUEST_CODE) {
            Log.e(TAG,"onActivity Result Called ");
            handleSpotifyAuthResult(SpotifyAuthHelper.getResultForIntent(resultCode, intent));
        }
        //For Blutooth Checking
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG,"Blutooth  not enable");
            //TabActivity parentTab = (TabActivity) this.getParent();
            //parentTab.finish();

            return;
        }
    }

    private void handleSpotifyAuthResult(SpotifyAuthHelper.SpotifyAuthResult intentResult) {
        if (intentResult.errorMessage != null) {
            logAndShowToast(intentResult.errorMessage);
        } else {
            globalClass.showProgressBar(this);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("client_id",globalClass.CLIENT_ID);
                jsonObject.put("client_secret",globalClass.Client_Secret);
                jsonObject.put("code",intentResult.authCode);
                jsonObject.put("redirect_uri",globalClass.REDIRECT_URI);
                globalClass.spotifyLoginUsingMightyCloud(jsonObject,this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //logAndShowToast("The auth code is:" + intentResult.authCode);
            if (intentResult.state != null) {
                Log.e(TAG, "The state parameter was returned:" + intentResult.state);
            }
        }
    }

    private void logAndShowToast(String toastMessage) {
        Log.d(TAG,"Auth Message"+toastMessage);
        globalClass.toastDisplay(toastMessage);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG,"onBackPressed");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }



}
