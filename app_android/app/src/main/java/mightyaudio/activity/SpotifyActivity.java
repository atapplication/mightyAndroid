package mightyaudio.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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
import mightyaudio.core.SpotifyAuthHelper;
import mightyaudio.receiver.ConnectivityReceiver;

import static mightyaudio.TCP.Constants.MSG_ID_SPOTIFY_LOGIN_ID;

public class SpotifyActivity extends RootActivity implements View.OnClickListener{
    private static final String TAG = SpotifyActivity.class.getSimpleName();
    private Typeface custom_font,custom_bold,custom_serenity_fount,custom_serenity_bold;
    private TextView txt_login_header,txt_learn_more,save_text,txt_cancel,text_title,text_sub_header1,text_sub_header2,text_sub_header3,text_sub_header4,text_learn_more,text_head_spotify,text_spotify_user_name;;
    private Button  btn_login;
    private GlobalClass globalClass;
    private boolean  isConnected;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private MaterialDialog  mightyMaterial;
    private View mightyPromptView;
    private TextView mightyheader_text,mightyMessage_text;
    private boolean spotifySwitchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        globalClass = GlobalClass.getInstance();
        custom_font = Typeface.createFromAsset(getAssets(), "circularuit-book.ttf");
        custom_bold = Typeface.createFromAsset(getAssets(), "circularuit-bold.ttf");

        custom_serenity_fount = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_serenity_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");

        txt_cancel = (TextView)findViewById(R.id.txt_cancel);
        save_text = (TextView)findViewById(R.id.txt_save);
        text_title= (TextView)findViewById(R.id.text_title);
        txt_login_header =(TextView)findViewById(R.id.txt_login_header);
        text_sub_header1 = (TextView)findViewById(R.id.txt_login_sub_header1);
        text_sub_header2 = (TextView)findViewById(R.id.txt_login_sub_header2);
        text_sub_header3 = (TextView)findViewById(R.id.txt_login_sub_header3);
        text_sub_header4 = (TextView)findViewById(R.id.txt_login_sub_header4);
        txt_learn_more =(TextView)findViewById(R.id.txt_learn_more);
        btn_login = (Button)findViewById(R.id.button_login);

        save_text.setVisibility(View.INVISIBLE);
        text_title.setText(getString(R.string.mighty_setup));
        txt_cancel.setText("< BACK");
        //txt_learn_more.setPaintFlags(txt_learn_more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // making underline of text

        txt_login_header.setTypeface(custom_bold);
        text_sub_header1.setTypeface(custom_font);
        text_sub_header2.setTypeface(custom_font);
        text_sub_header3.setTypeface(custom_font);
        text_sub_header4.setTypeface(custom_font);
        txt_learn_more.setTypeface(custom_font);
        txt_cancel.setTypeface(custom_serenity_fount);
        btn_login.setTypeface(custom_font);
        text_title.setTypeface(custom_serenity_bold);


        btn_login.setOnClickListener(this);
        txt_learn_more.setOnClickListener(this);
        txt_cancel.setOnClickListener(this);
        onreceive_ble();
        registerReceiver(mGattUpdateReceiver, MightyMsgReceiver.makeGattUpdateIntentFilter());
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login :
                globalClass.showProgressBar(this);
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    new Handler().post(returnRes);
                }else{
                    globalClass.dismissProgressBar();
                    globalClass.toastDisplay(getString(R.string.check_internet));
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        globalClass.dismissProgressBar();
                    }
                },15000);
                break;
            case R.id.txt_learn_more :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    Intent mighty_help_intent = new Intent(this,MightyHelpActivity.class);
                    mighty_help_intent.putExtra("FromData","SpotifyPremium");
                    startActivity(mighty_help_intent);
                }
                else globalClass.toastDisplay(getString(R.string.check_internet));
                break;
            case R.id.txt_cancel :
                previousActivity();
                break;
        }
    }


    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            //if(isConnected){
            globalClass.count_offset=0;
            globalClass.count=0;
            if(!globalClass.arrayPlayList.isEmpty())
                globalClass.arrayPlayList.clear();
            loginSpotify();
            // }
            //else {
            // globalClass.dismissProgressBar();
            // globalClass.toastDisplay(getString(R.string.check_internet));}
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(ble_onreceive,intentFilter1);
    }

    private void loginSpotify(){
        PackageManager pm = getApplicationContext().getPackageManager();
        ComponentName compNameSptify = new ComponentName(getPackageName(),getPackageName() + ".AliyasSpotifyActivity");
        ComponentName compNameLaunch = new ComponentName(getPackageName(),getPackageName() + ".AliyasLaunchTabActivity");
        pm.setComponentEnabledSetting(compNameLaunch,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(compNameSptify,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        globalClass.showProgressBar(this);
        SpotifyAuthHelper.startSpotifyAuthFlow(SpotifyActivity.this,
                globalClass.CLIENT_ID,
                globalClass.REDIRECT_URI,
                globalClass.LOCAL_STATE,
                globalClass.AUTHORIZATION_CODE,
                globalClass.REQUESTED_SCOPES,
                globalClass.REQUESTED_SCOPES_ARRAY,
                globalClass.REQUEST_CODE
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"Activity onNewIntent Called ");
        handleSpotifyAuthResult(SpotifyAuthHelper.getResultForBrowserIntent(intent));
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                txt_cancel.setEnabled(true);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {



            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == globalClass.REQUEST_CODE) {
            Log.e(TAG,"onActivity Result Called ");
            handleSpotifyAuthResult(SpotifyAuthHelper.getResultForIntent(resultCode, intent));
        }
    }


    private void handleSpotifyAuthResult(SpotifyAuthHelper.SpotifyAuthResult intentResult) {
        if (intentResult.errorMessage != null) {
            logAndShowToast(intentResult.errorMessage);
        } else {
            txt_cancel.setEnabled(false);
            prgrssDailogShow();
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
           /* if (intentResult.state != null) {
                Log.e(TAG, "The state parameter was returned:" + intentResult.state);
            }*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "triggering_SpotifyActivity handler");
                    if(!spotifySwitchUser){
                        // If Spotify Switch popup will come then it should not triggere
                        txt_cancel.setEnabled(true);
                        progrssDailogCancel();
                        Log.e(TAG, "triggering_SpotifyActivity handler condition");
                    }

                }
            },15000);
        }
    }

    private void logAndShowToast(String toastMessage) {
        Log.d(TAG,"Auth Message"+toastMessage);
        globalClass.toastDisplay(toastMessage);
    }

    @Override
    public void onBackPressed() {
        if(txt_cancel.isEnabled())
            previousActivity();
        else return;
    }

    private void previousActivity(){
        finish();
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
                    case MSG_ID_SPOTIFY_LOGIN_ID :
                        if(msgType == 200){
                            Log.e(TAG, "triggering_SpotifyActivity 200");
                            progrssDailogCancel();
                            txt_cancel.setEnabled(true);
                            globalClass.spotify_status=true;
                            Intent intent = new Intent(getApplicationContext(), AllDoneActivty.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else if (msgType == 301) {

                            Log.e(TAG, "triggering_SpotifyActivity 301");
                            //globalClass.alertdialogmighty(SpotifyActivity.this, 2);
                            spotifySwitchUser= true;
                            spotifySwitchdialog();
                        }
                        break;

                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ble_onreceive);
    }
    //Spotify switch user alert dailog
    private void spotifySwitchdialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(SpotifyActivity.this);
        mightyPromptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        mightyheader_text=(TextView)mightyPromptView.findViewById(R.id.text_header);
        mightyMessage_text=(TextView)mightyPromptView.findViewById(R.id.text_message);
        mightyheader_text.setText("Are you sure?");
        mightyMessage_text.setText("All playlists will be removed from this Mighty if you proceed.");
        mightyMaterial = new MaterialDialog(SpotifyActivity.this)
                .setView(mightyPromptView)
                .setPositiveButton(getString(R.string.continue__button), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        globalClass.spotify_tick();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                globalClass.send_set_spotify();
                            }
                        },3000);
                        if(globalClass.mighty_ble_device == null){
                            txt_cancel.setEnabled(true);
                            progrssDailogCancel();
                            Intent intent = new Intent(getApplicationContext(), AllDoneActivty.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        mightyMaterial.dismiss();

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txt_cancel.setEnabled(true);
                        globalClass.spotifySessionManager.clearSession();
                        globalClass.send_set_spotifylogout();
                        globalClass.notifyBrowesFragment("Logout");
                        globalClass.spotifyloginlogout("Logout");
                        GlobalClass.spotify_frag_status=false;
                        globalClass.spotify_status=false;
                        globalClass.spotify_tick();
                        progrssDailogCancel();
                        mightyMaterial.dismiss();
                    }
                });

        mightyMaterial.show();
    }


    private void prgrssDailogShow(){
        Log.e(TAG,"Prgressbar clicked_spotifyactivity");
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
}

