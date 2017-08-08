package mightyaudio.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import mightyaudio.Model.Playlist;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.adapter.YourMusicAdapter;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.MusicMightyDeletePlaylist;
import mightyaudio.fragment.MusicMightyFragment;
import mightyaudio.fragment.YourMusicFragment;
public class MusicActivity extends AppCompatActivity implements YourMusicFragment.BrowesFragmentCommunication,MusicMightyFragment.Musicmightysync,YourMusicFragment.BrowesFragmentSyning,YourMusicAdapter.MusicFragmentInterface,MusicMightyDeletePlaylist {
    String TAG = MusicActivity.class.getSimpleName();

    private TabLayout tabLayout;
    public BluetoothAdapter mBluetoothAdapter;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    ImageView img_tab_indicator1, img_tab_indicator2;
    MightyMsgReceiver mightyMsgReceiver;
    GlobalClass globalClass = GlobalClass.getInstance();
    TextView txtbrowse_click;
    private ImageView mighty_image;
    private MusicMightyFragment musicMightyFragment;
    private YourMusicFragment yourMusicFragment;
    private IntentFilter intentFilter ;
    private BroadcastReceiver playlistreceiver;
    private IntentFilter intentFilter1,intentFilter_compatibility ;
    private BroadcastReceiver ble_onreceive,receiver_compatibility;
    TextView  message_txt;
    private Typeface custom_font,custom_bold;
    Handler handler;
    public SharedPreferences mightyinfopref;
    BluetoothDevice device_obj;
    private View promptView ,bleDisconnectpopup;
    public static DisplayMetrics displayMetrics=new DisplayMetrics();
    private LinearLayout layout_mighty,layout_your_music;
    private MaterialDialog mMaterialDialog,syncDisconnetMaterial,bleDisconnectMaterial;
    private TextView bleDisconnect_header_text;
    private TextView bleDisconnect_message;

    private LaunchTabActivity launchTabActivity ;
    private IntentFilter intentPlayList;
    private BroadcastReceiver receive_fail;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        launchTabActivity = (LaunchTabActivity)getParent();
        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        launchTabActivity= (LaunchTabActivity)getParent();

        //Autoconnect alert dialog view initializing
        promptView = layoutInflater.inflate(R.layout.auto_connect_dialog_layout, null);
        final TextView header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText("Searching for your Mighty");
        message_txt.setText("Scanning");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mightyinfopref = getSharedPreferences(globalClass.MIGHTY_INFO_PREF , Context.MODE_PRIVATE);
        handler = new Handler();
        if(globalClass.mighty_ble_device == null && globalClass.auto_connect_force_cancel == 1){
            Gson gson = new Gson();
            String mighty_info = mightyinfopref.getString(globalClass.AUTO_CONNECT, "");
            device_obj = gson.fromJson(mighty_info, BluetoothDevice.class);
            if (device_obj != null && globalClass.auto_connect_force_cancel == 1) {
                if(mBluetoothAdapter.isEnabled()) {
                    autoConnetDailog();
                }else globalClass.toastDisplay(" Bluetooth is not enabled");
            }
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        img_tab_indicator1 = (ImageView) findViewById(R.id.tab_indicator1);
        img_tab_indicator2 = (ImageView) findViewById(R.id.tab_indicator2);
        layout_mighty =( LinearLayout) findViewById(R.id.layout_mighty);
        layout_your_music =( LinearLayout) findViewById(R.id.layout_your_music);
        img_tab_indicator1.setVisibility(View.VISIBLE);
        img_tab_indicator2.setVisibility(View.INVISIBLE);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        musicMightyFragment = new MusicMightyFragment();
        yourMusicFragment = new YourMusicFragment();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        txtbrowse_click = (TextView) findViewById(R.id.txtbrowse);
        txtbrowse_click.setTypeface(custom_bold);
        mighty_image=(ImageView)findViewById(R.id.mighty_image);

        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);

        /************************************ Tab Selection Indicator *****************************/

        layout_your_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
        layout_mighty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabPosition = tab.getPosition();
                Log.e(TAG,"tab calling "+tabPosition);
                if(tabPosition == 0)
                {
                    mighty_image.setImageResource(R.drawable.ic_txt_mighty_blue);
                    img_tab_indicator1.setVisibility(View.VISIBLE);
                    img_tab_indicator2.setVisibility(View.INVISIBLE);
                    txtbrowse_click.setTextColor(Color.parseColor("#b0aeae"));
                }
                if(tabPosition == 1)
                {
                    mighty_image.setImageResource(R.drawable.ic_txt_mighty_white);
                    mighty_image.getDrawable().setColorFilter(Color.parseColor("#b0aeae"), PorterDuff.Mode.MULTIPLY);
                    img_tab_indicator2.setVisibility(View.VISIBLE);
                    img_tab_indicator1.setVisibility(View.INVISIBLE);
                    txtbrowse_click.setTextColor(Color.parseColor("#00C2F3"));
                    System.out.println("page chenged in tab"+tab.getPosition());
                }
            }
        });

        System.out.println("frag_status"+GlobalClass.spotify_frag_status);
        LinearLayout tabs=(LinearLayout)tabLayout.getChildAt(0);
        for (int i=0;i<tabs.getChildCount();i++){
            tabs.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.e(TAG,"onTouch calling");
                    if (!GlobalClass.spotify_frag_status)
                        return true;

                    return true;
                }
            });
        }
        mightyMsgReceiver = new MightyMsgReceiver(launchTabActivity) {
            @Override
            public void onConnected() {
                Log.d(TAG,"Connected");
                if(device_obj != null && globalClass.auto_connect_force_cancel == 1)
                    header_txt.setText("Your Mighty is connecting to the mobile app");
                    message_txt.setVisibility(View.GONE);
            }

            @Override
            public void onDiscovered() {
                Log.d(TAG,"Discovered");
                globalClass.scanLeDevice(false);
                if( device_obj != null && globalClass.auto_connect_force_cancel == 1) {
                    if(mMaterialDialog != null)
                        mMaterialDialog.dismiss();
                    Toast toast = Toast.makeText(getBaseContext(),  "Connected", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();;
                }
                yourMusicFragment.bleDiscovered();
                musicMightyFragment.bleDiscovered();

                if(syncDisconnetMaterial != null)
                    syncDisconnetMaterial.dismiss();
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG,"Dis Connected" +musicMightyFragment.isAdded()+" "+ yourMusicFragment.isAdded());
                Log.e(TAG,"Sycing value "+globalClass.syncing_status+" "+globalClass.auto_connect_force_cancel);
                if(globalClass.syncing_status && globalClass.auto_connect_force_cancel != 2){
                    alertDailogSingleText("Your Mighty is reconnecting to the app. This does not affect the sync in progress.",launchTabActivity);
                }
                if(!YourMusicAdapter.selectedlist.isEmpty())
                    YourMusicAdapter.selectedlist.clear();
                if(musicMightyFragment.isAdded()){
                    musicMightyFragment.bleDisconnected();
                }if(yourMusicFragment.isAdded()){
                    yourMusicFragment.bleDisconnected();
                }

            }
        };
        mightyMsgReceiver.RegisterReceiver(launchTabActivity);
        afterRetrivePlaylist();
        registerReceiver(playlistreceiver,intentFilter);

        KeyboardVisibilityEvent.setEventListener(this,new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                // some code depending on keyboard visiblity status
                Log.e(TAG,"Triger "+isOpen);
                if(yourMusicFragment.isAdded( )&& !isOpen){
                    yourMusicFragment.onlyKeyboardHide();
                }
            }
        });
        //Hardware define bradcast for showing alerdailog
        compatibilityBroadCast();
        registerReceiver(receiver_compatibility,intentFilter_compatibility);
        plalistRetriveFailed();
        registerReceiver(receive_fail,intentPlayList);
    }

    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                handleMightyMessage(msgId,msgType);
                musicMightyFragment.onReceivemsg(msgId,msgType); //MusicMightyFragment calling
                yourMusicFragment.onReceiveMessage(msgId,msgType); //your_music_fragment indicating
                Log.e(TAG,"on_Connected_music :" +msgId +" msgType "+ msgType);
            }
        };
    }

    private void plalistRetriveFailed(){
        intentPlayList=new IntentFilter("spotify.playlist.retrievefail");
        receive_fail=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intentfail) {
                String str_status = intentfail.getExtras().getString("flag_value");
                Log.e(TAG,"Retrive Fail "+str_status);
                    yourMusicFragment.playListRetriveFail(str_status);
            }
        };
    }

    private void afterRetrivePlaylist(){
        intentFilter = new IntentFilter("com.spotify.playlist");
        playlistreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                    int value = (int) spotify.getExtras().get("update");
                Log.e(TAG,"Playlist song has done "+value);
                if(globalClass.mighty_playlist != null)
                yourMusicFragment.retrivePlayList(value);
                if(value == 1)
                musicMightyFragment.refreshPlayList();
            }
        };
    }


    @Override
    protected void onResume() {     // 27-08-16
        super.onResume();
        Log.e(TAG,"onResume()");
        Log.e(TAG,"Thread Check "+globalClass.isFromMainThread());
        if(globalClass.mighty_ble_device != null) {
            if(globalClass.mighty_playlist.size() <=0 ) {
               // globalClass.mBluetoothLeService_global.getMightyBasicInfos();
            }
        }
        musicMightyFragment.isAdded();
        Log.e(TAG," MyFragment "+ musicMightyFragment.isAdded());
    }
    private void setupViewPager(final ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),viewPager);
        adapter.addFragment(musicMightyFragment, "");
        adapter.addFragment(yourMusicFragment,"");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(0);
    }

    @Override
    public void browesSyncingTrigger(int flag){
        if(flag == 1 && musicMightyFragment.isAdded()){
            musicMightyFragment.btnSyniTriger();
        }
        else if( musicMightyFragment.isAdded())
            musicMightyFragment.btnSyniCancel();
    }

    @Override
    public void playListDeleted() {
        yourMusicFragment.afterDeletePlayListMighty();
    }

    @Override
    public void sync_music_frag(int flag) {
        Log.e(TAG,"flag value "+flag);
        yourMusicFragment.sync_music_frag(flag);
    }

    @Override
    public void PlaylistSizePink(float track_size) {
        musicMightyFragment.PlayListSizeInfo(track_size);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter    //FragmentPagerAdapter  27-08-16
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPager viewPager;

        public ViewPagerAdapter(FragmentManager manager,ViewPager viewPager)
        {
            super(manager);
            this.viewPager = viewPager;

        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment=mFragmentList.get(position);

            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override                  // 27-08-16
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return 0;
        }
    }

    public void handleMightyMessage(int msgId, int msgType)
    {

        if (msgId==15) {
            if (msgType == 302) {
                Log.e(TAG, "triggering_music");
                globalClass.alertdialogmighty(launchTabActivity,1);
            }

            if(msgType == 304)
                alertdailogBleDisconnect(launchTabActivity,"This Mighty is registered to another user","That user must connect to this Mighty and logout of their account, then you will be able to connect.");
        }

        if(msgId==14) {
            if (msgType == 301) {
                Log.e(TAG, "triggering_music");
                globalClass.alertdialogmighty(launchTabActivity, 2);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause()");
    }

    public void alertdailogBleDisconnect(Activity activity,String tittle,String meassge){
        if(bleDisconnectMaterial != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        bleDisconnectpopup = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        bleDisconnect_header_text=(TextView)bleDisconnectpopup.findViewById(R.id.text_header);
        bleDisconnect_message=(TextView)bleDisconnectpopup.findViewById(R.id.text_message);
        bleDisconnect_header_text.setText(tittle);
        bleDisconnect_message.setText(meassge);
        bleDisconnectMaterial = new MaterialDialog(activity)
                .setView(bleDisconnectpopup)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        globalClass.bleDisconnect();
                        bleDisconnectMaterial.dismiss();
                        bleDisconnectMaterial= null;

                    }
                });

        bleDisconnectMaterial.show();
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");

        unregisterReceiver(playlistreceiver);
        unregisterReceiver(ble_onreceive);
        mightyMsgReceiver.unRegisterReceiver(launchTabActivity);
        unregisterReceiver(receiver_compatibility);
        unregisterReceiver(receive_fail);

    }

    @Override
    public void onBackPressed() {
        Log.e(TAG,"onBackPressed");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        TextView header_txt=(TextView)promptView.findViewById(R.id.text_header);
        header_txt.setText("If you proceed, your App will be closed as well as any existing connection");
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.gc();
                        System.exit(0);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();

    }

    private void autoConnetDailog(){
         mMaterialDialog = new MaterialDialog(this)
               .setView(promptView)
                .setPositiveButton(getString(R.string.hide), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        globalClass.auto_connect_force_cancel = 1;
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        globalClass.auto_connect_force_cancel = 2;
                    }
                });
        mMaterialDialog.show();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(mMaterialDialog != null){
                    mMaterialDialog.dismiss();
                }
            }
        }, 30000);
    }

    private void compatibilityBroadCast(){
        intentFilter_compatibility=new IntentFilter("mighty.hardware.compatibility");
        receiver_compatibility=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                globalClass.hardwarecompatibility(launchTabActivity,"Can not procceed",getString(R.string.hardeware_compality));
            }
        };
    }

    public void alertDailogSingleText(String message,Activity activity){
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        TextView header_txt=(TextView)promptView.findViewById(R.id.text_header);
        header_txt.setText(message);
        syncDisconnetMaterial = new MaterialDialog(activity)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        syncDisconnetMaterial.dismiss();
                        syncDisconnetMaterial= null;

                    }
                });
        syncDisconnetMaterial.show();
    }

    @Override
    public void deletePlayList(Playlist play_delete) {
        musicMightyFragment.deletePlayListFromAdapter(play_delete);
    }

}
