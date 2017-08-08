package mightyaudio.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mightyaudio.TCP.Constants;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.BtDeviceComunication;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.NonSwipeableViewPager;
import mightyaudio.core.RootActivity;
import mightyaudio.fragment.ConnBluetoothFragmentBle;
import mightyaudio.fragment.ConnSpotifyFragment;
import mightyaudio.fragment.ConnWifiFragment;

public class ConnectActivity extends RootActivity implements BtDeviceComunication
{
    private final static String TAG = ConnectActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView img_tab_indicator1, img_tab_indicator2, img_tab_indicator3, img_tab_indicator4;
    ImageView ble_indicator,wifi_indicator,spotify_indicator;
    ImageView tabBle,tabWifi,tabSpotify;
    //int color = Color.parseColor("#9900c2f3");
    int color = Color.parseColor("#00C2F3");
    int spotify_color = Color.parseColor("#1DB954");
    int defcolor = Color.parseColor("#b0aeae");
    ViewPagerAdapter adapter;
    GlobalClass globalClass = GlobalClass.getInstance();
    MightyMsgReceiver mightyMsgReceiver;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;

    IntentFilter intentFilter;
    IntentFilter intentFilter_disconnect;
    BroadcastReceiver receiver;
    BroadcastReceiver receiver_disconnect;
    IntentFilter connect;
    BroadcastReceiver receiver_connect;
    private ConnWifiFragment connWifiFragment;
    private ConnBluetoothFragmentBle connBluetoothFragmentBle;
    private ConnSpotifyFragment connSpotifyFragment;
    public static LaunchTabActivity launchTabActivity;
    private MaterialDialog  mMaterialDialog;
    private View promptView ;
    private TextView header_txt;
    public SharedPreferences.Editor use_editor;
    public SharedPreferences usingpref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        usingpref = getSharedPreferences(globalClass.USING_PREF, Context.MODE_PRIVATE);
        use_editor = usingpref.edit();
        launchTabActivity = (LaunchTabActivity)getParent();
        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        connWifiFragment = new ConnWifiFragment();
        connBluetoothFragmentBle = new ConnBluetoothFragmentBle();
        connSpotifyFragment = new ConnSpotifyFragment();
        setupViewPager(viewPager);
        Log.e(TAG,"onCreate");
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabTextColors(defcolor,color);
        setupTabIcons();

        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);
        /**************************** Tab Selection Indicator *********************************/
        img_tab_indicator1 = (ImageView) findViewById(R.id.tab_indicator1);
        img_tab_indicator2 = (ImageView) findViewById(R.id.tab_indicator2);
        img_tab_indicator4 = (ImageView) findViewById(R.id.tab_indicator4);
        /**************************************************************************************/

        handling_upper_tab_selection();

        ble_indicator = (ImageView) findViewById(R.id.ble_indicator);
        wifi_indicator = (ImageView) findViewById(R.id.wifi_indicator);
        spotify_indicator = (ImageView) findViewById(R.id.spotify_indicator);

        /******************************* Check Bluetooth Enable/Disable **************************************/

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device Doesn't support Bluetooth",Toast.LENGTH_LONG).show();
        }


        /**************************************************************************/
        if(globalClass.mighty_ble_device == null) {
            ble_indicator.setImageResource(R.drawable.ic_cross);
            wifi_indicator.setImageResource(R.drawable.ic_cross);
            globalClass.wifi_status = false;
            globalClass.ble_status = false;
            tick_cross();
        }else {
            ble_indicator.setImageResource(R.drawable.ic_tick);
            globalClass.ble_status = true;
            if (globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals("") ){
                wifi_indicator.setImageResource(R.drawable.ic_tick);
                globalClass.wifi_status = true; //New modify because down tick is not showing because of wifi
            }else {
                wifi_indicator.setImageResource(R.drawable.cross);
                globalClass.wifi_status = false; //New modify because down tick is not showing because of wifi
                Log.e(TAG,"on_create_wifi_Status "+globalClass.wifi_status);
            }
            tick_cross();
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabPosition = tab.getPosition();
            Log.e(TAG," frag_position tabPosition "+tabPosition+" ");
                if(tabPosition == 0)
                {
                    img_tab_indicator1.setVisibility(View.VISIBLE);
                    img_tab_indicator2.setVisibility(View.INVISIBLE);
                    img_tab_indicator4.setVisibility(View.INVISIBLE);
                    tabBle.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    tabWifi.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
                    tabSpotify.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);


                }
                if(tabPosition == 1)
                {
                    img_tab_indicator1.setVisibility(View.INVISIBLE);
                    img_tab_indicator2.setVisibility(View.VISIBLE);
                    img_tab_indicator4.setVisibility(View.INVISIBLE);
                    tabBle.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
                    tabWifi.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    tabSpotify.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
                }

                if(tabPosition == 2) {

                    Log.e(TAG, "difffff " + globalClass.diffrent_user_block);

                    //viewPager.getChildAt(2).setClickable(false);
                    img_tab_indicator1.setVisibility(View.INVISIBLE);
                    img_tab_indicator2.setVisibility(View.INVISIBLE);
                    img_tab_indicator4.setVisibility(View.VISIBLE);
                    tabBle.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
                    tabWifi.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
                    tabSpotify.getDrawable().setColorFilter(spotify_color, PorterDuff.Mode.MULTIPLY);
                    if (globalClass.lower_version)
                        alertDailogSingleText(launchTabActivity,getString(R.string.please_update_your_mighty));
                }
            }
        });
        mightyMsgReceiver = new MightyMsgReceiver(getBaseContext()) {
            @Override
            public void onConnected() {
                Log.e(TAG,"Connected my");
                if(connBluetoothFragmentBle.isAdded())
                    connBluetoothFragmentBle.bleConnected();
            }
            @Override
            public void onDiscovered() {
                Log.e(TAG,"Discovered");
                refresh();
                globalClass.ble_status = true;
                tick_cross();
                if(globalClass.auto_connect_force_cancel != 1)
                    viewPager.setCurrentItem(1);
                if(connBluetoothFragmentBle.isAdded())
                    connBluetoothFragmentBle.bleDiscovered();
                if(connWifiFragment.isAdded())
                    connWifiFragment.blDdiscovered();
            }
            @Override
            public void onDisconnected() {
                Log.d(TAG,"Disconnected");
                wifi_indicator.setImageResource(R.drawable.ic_cross);
                refresh();
                globalClass.wifi_status = false;
                globalClass.ble_status = false;
                tick_cross();
                if(connWifiFragment.isAdded())
                    connWifiFragment.bleDisconnected();

            }
        };
        mightyMsgReceiver.RegisterReceiver(launchTabActivity);
        //For above spotifytab Green tick
        intentFilter = new IntentFilter("com.spotify.broadcast");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                String value =  spotify.getExtras().getString("value");
                System.out.print(value);
                if(value.equals("logged_in"))
                {
                    spotify_indicator.setImageResource(R.drawable.ic_tick);
                    //LaunchTabActivity.host.setActivated(true);
                }else{
                    spotify_indicator.setImageResource(R.drawable.ic_cross);

                }
            }
        };

        intentFilter_disconnect = new IntentFilter("ble.disconnect.successful");
        receiver_disconnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                Log.e(TAG,"Disconnect_broadcast_triggered");
                if(connBluetoothFragmentBle.isAdded())
                    connBluetoothFragmentBle.bleDisconnected();
            }
        };

        //For above wifitab Green tick
        connect = new IntentFilter("com.spotify.connect");
        receiver_connect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent connect) {
                String value = connect.getExtras().getString("value");
                Log.e(TAG,"spotify_tick1");
                tick_cross();
                if(globalClass.spotify_status){
                    spotify_indicator.setImageResource(R.drawable.ic_tick);
                }else{
                    spotify_indicator.setImageResource(R.drawable.ic_cross);
                }
            }
        };

        // register BroadCast for SpotifyTab tick mark
        registerReceiver(receiver, intentFilter);
        //Connbluetooth fragment disconnect call once
        registerReceiver(receiver_disconnect, intentFilter_disconnect);
        // register BroadCast for wifiTab tick mark
        registerReceiver(receiver_connect,connect);
    }


    private void setupTabIcons()
    {
        tabBle = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabBle.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble));
        tabBle.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        tabLayout.getTabAt(0).setCustomView(tabBle);
        tabBle.setSelected(true);

        tabWifi = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabWifi.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi));   // ic_wifi
        tabWifi.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        tabLayout.getTabAt(1).setCustomView(tabWifi);

        tabSpotify = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSpotify.setImageDrawable(getResources().getDrawable(R.drawable.ic_spotify));
        tabSpotify.getDrawable().setColorFilter(spotify_color, PorterDuff.Mode.MULTIPLY);
        tabLayout.getTabAt(2).setCustomView(tabSpotify);
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),viewPager);
        adapter.addFrag(connBluetoothFragmentBle, "ONE");  // ConnectBluetoothFragmentNew1()
        adapter.addFrag(connWifiFragment, "TWO");       // ConnWifiFragmentNew()
        adapter.addFrag(connSpotifyFragment, "THREE");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, " frag_position" + position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG, " frag_position state " + state);
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    if ((viewPager.getCurrentItem() == 0)||(viewPager.getCurrentItem()==1)||(viewPager.getCurrentItem()==2))
                    {
                        // Hide the keyboard.
                        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                    }
                }
            }
        });

/*        viewPager.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalClass.diffrent_user_block == false)
                    viewPager.getChildAt(2).setClickable(false);
                else
                    viewPager.getChildAt(2).setClickable(true);
            }
        });*/
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPager viewPager;

        public ViewPagerAdapter(FragmentManager manager,ViewPager viewPager) {
            super(manager);
            this.viewPager = viewPager;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Tab View get Item : " + position+ " "+globalClass.events_global.getWiFi_status());
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }

    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                if(connWifiFragment.isAdded())
                    connWifiFragment.onReceiveMessage(msgId,msgType);
                if(connSpotifyFragment.isAdded())
                    connSpotifyFragment.onReceiveMessage(msgId,msgType);
                handleMightyMessage(msgId,msgType);
                if(connBluetoothFragmentBle.isAdded())
                    connBluetoothFragmentBle.handleMightyMessage(msgId,msgType);
                Log.e(TAG,"BitRate in Connection Activity "+msgId+" "+msgType);
                if(msgId == Constants.MSG_BIT_RATE_MODE && msgType == 200){
                    Intent bitrate_intent = new Intent();
                    bitrate_intent.setAction("bitrate.broadcast");
                    bitrate_intent.putExtra("msgid",msgId);
                    bitrate_intent.putExtra("msgType",msgType);
                    sendBroadcast(bitrate_intent);
                }
                Log.e(TAG,"on_Connected :" +msgId +" msgType "+ msgType);
            }
        };
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG,"Main Thread "+globalClass.isFromMainThread());

        if(!globalClass.process_GoingOn) {
            viewPager.setCurrentItem(0);
        }else globalClass.process_GoingOn = false;

        Log.d(TAG, "ONResume()"+globalClass.process_GoingOn);

        if (GlobalClass.spotify_frag_status){
            spotify_indicator.setImageResource(R.drawable.ic_tick);
        }else{
            spotify_indicator.setImageResource(R.drawable.ic_cross);
        }
    }

    public void refresh() {
        if (globalClass.mighty_ble_device == null) {
            ble_indicator.setImageResource(R.drawable.ic_cross);
            globalClass.ble_status = false;
        } else {
            ble_indicator.setImageResource(R.drawable.ic_tick);
            globalClass.ble_status = true;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "ONPause()");
        if (GlobalClass.spotify_frag_status){
            spotify_indicator.setImageResource(R.drawable.ic_tick);
        }else{
            spotify_indicator.setImageResource(R.drawable.ic_cross);
        }
    }

    public void handling_upper_tab_selection()
    {
        GlobalClass globalClass =  (GlobalClass)getApplicationContext();
        int upper_tab_status = globalClass.connect_upper_tool_bar_status;

        if(upper_tab_status == 0)
        {
            img_tab_indicator1.setVisibility(View.VISIBLE);
            img_tab_indicator2.setVisibility(View.INVISIBLE);
            img_tab_indicator4.setVisibility(View.INVISIBLE);
            tabBle.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            tabWifi.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
            tabSpotify.getDrawable().setColorFilter(defcolor, PorterDuff.Mode.MULTIPLY);
        }else if(upper_tab_status == 1)
        {
            img_tab_indicator1.setVisibility(View.INVISIBLE);
            img_tab_indicator2.setVisibility(View.VISIBLE);
            img_tab_indicator4.setVisibility(View.INVISIBLE);

        }
        TabLayout.Tab tab = tabLayout.getTabAt(upper_tab_status);
        tab.select();
    }

    public void handleMightyMessage(int msgId, int msgType)
    {
        Log.d(TAG, "Received Mighty Message ID = " + msgId);

        if(msgId == 16 | msgId == 20 | msgId == 23) {
/*            if ((globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals(""))) {
                wifi_indicator.setImageResource(R.drawable.ic_tick);
                globalClass.wifi_status = true;

            } else {
                wifi_indicator.setImageResource(R.drawable.ic_cross);
                globalClass.wifi_status = false;
            }*/
            Log.d(TAG, "Received Event ");

        //    if(msgType == 102) {
/*                if (globalClass.events_global.getWiFi_status() != 1) {
                    wifi_indicator.setImageResource(R.drawable.ic_cross);
     //               globalClass.wifi_status = false;
                } else {
                    wifi_indicator.setImageResource(R.drawable.ic_tick);
     //               globalClass.wifi_status = true;
                }*/
         //   }
            tick_cross();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    tick_cross();
                }
                },2000);

            wificonnectedYourMusicPlusIconBradcast(globalClass.wifi_status);
        }
    }

    private void wificonnectedYourMusicPlusIconBradcast(boolean status){
        Log.e(TAG,"wifisatus "+status);
        Intent wificonnectiomIntent = new Intent();
        wificonnectiomIntent.setAction("wifi.connection.broadcast");
        if(status)wificonnectiomIntent.putExtra("wifi_status","true");
        else wificonnectiomIntent.putExtra("wifi_status","false");
        sendBroadcast(wificonnectiomIntent);
    }



    public void tick_cross(){
        Log.e(TAG,"All connecticon tick "+globalClass.spotify_status+" "+globalClass.ble_status+" "+globalClass.wifi_status);
        if((globalClass.spotify_status) && (globalClass.ble_status) && (globalClass.wifi_status)){
            use_editor.putInt("using_satatus",1);
            use_editor.commit();
            wifi_indicator.setImageResource(R.drawable.ic_tick);
            launchTabActivity.host.setActivated(true);
        }else{
            launchTabActivity.host.setActivated(false);
            if(globalClass.wifi_status == false) {
                wifi_indicator.setImageResource(R.drawable.ic_cross);
            }else
                wifi_indicator.setImageResource(R.drawable.ic_tick);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"On_Stop");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Activity onDestroy()");

        mightyMsgReceiver.unRegisterReceiver(launchTabActivity);
        // mightyMsgReceiver.unRegisterReceiver(getApplicationContext());
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG,"onBackPressed");
        Intent launch_intent = new Intent(this,LaunchTabActivity.class);
        launch_intent.putExtra("onBackPressed","onBackPressed");
        startActivity(launch_intent);
    }

    public void alertDailogSingleText(Activity activity, String message){
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        header_txt.setText(message);
        mMaterialDialog = new MaterialDialog(activity)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        viewPager.setCurrentItem(1);

                    }
                });
        mMaterialDialog.show();
    }

    @Override
    public void btDeviceRequest() {
        connBluetoothFragmentBle.send_bt_structure();
    }

}
