package mightyaudio.fragment;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mightyaudio.activity.DownloadQualityActivity;
import mightyaudio.activity.MightyHelpActivity;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SpotifyAuthHelper;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;


public class ConnSpotifyFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = ConnSpotifyFragment.class.getSimpleName();
    private SpotifySessionManager spotifySessionManager;
    private SharedPreferences spotifyPref;

    public TextView switch_user,text_download_qulity,text_spotify_help,arrow1,arrow2,arrow3,arrow4;
    private LinearLayout logout_layout,login_layout;
    private Typeface custom_font,custom_bold,spotify_custom_font,spotify_custom_bold;
    public Button btn_login;
    private TextView text_logout,text_header,text_sub_header1,text_sub_header2,text_sub_header3,text_sub_header4,text_learn_more,text_head_spotify,text_username,text_logged_at;

    GlobalClass globalClass = GlobalClass.getInstance();

    TCPClient tcpClient;
    private Context context;
    //private  AuthenticationRequest request;
    private LinearLayout spotify_header;
    private IntentFilter intentFilter ;
    private BroadcastReceiver spotify_login;
    private TabActivity parentTab;
    private boolean  isConnected;
    private FrameLayout frame_layout;
    private MaterialDialog  mMaterialDialog;
    private View promptView ;
    private TextView header_txt;
    private TextView message_txt;
    private ProgressDialog progressDialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;

        //SpotifyLogin with Cloud
        spotifySessionManager = new SpotifySessionManager(context);
        spotifyPref = context.getSharedPreferences(spotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);

        /*request = new AuthenticationRequest.Builder(globalClass.CLIENT_ID, AuthenticationResponse.Type.TOKEN, globalClass.REDIRECT_URI)

                .setScopes(new String[]{"user-read-private", "playlist-read","playlist-read-private","streaming"})        // "playlist-read"             "user-read-private", "playlist-read", "playlist-read-private", "streaming"
                .build();*/
        tcpClient = globalClass.tcpClient;

        custom_font = Typeface.createFromAsset(getActivity().getAssets(), "serenity-light.ttf");
        custom_bold = Typeface.createFromAsset(getActivity().getAssets(), "serenity-bold.ttf");
        spotify_custom_font = Typeface.createFromAsset(getActivity().getAssets(), "circularuit-book.ttf");
        spotify_custom_bold = Typeface.createFromAsset(getActivity().getAssets(), "circularuit-bold.ttf");

        view = (View) inflater.inflate(R.layout.fragment_conn_spotify,container, false);

        spotify_header = (LinearLayout) view.findViewById(R.id.spotify_header);
        frame_layout = (FrameLayout) view.findViewById(R.id.frame_layout);
        btn_login = (Button)view.findViewById(R.id.button_login);
        text_logout = (TextView)view.findViewById(R.id.text_log_out);
        text_head_spotify = (TextView)view.findViewById(R.id.text_head_spotify);
        text_username = (TextView)view.findViewById(R.id.text_username);
        text_logged_at = (TextView)view.findViewById(R.id.text_logged_at);
        switch_user = (TextView)view.findViewById(R.id.txt_switchuser);
        logout_layout = (LinearLayout)view.findViewById(R.id.logout_layout);
        login_layout = (LinearLayout)view.findViewById(R.id.login_layout);
        text_header = (TextView)view.findViewById(R.id.txt_login_header);
        text_sub_header1 = (TextView)view.findViewById(R.id.txt_login_sub_header1);
        text_sub_header2 = (TextView)view.findViewById(R.id.txt_login_sub_header2);
        text_sub_header3 = (TextView)view.findViewById(R.id.txt_login_sub_header3);
        text_sub_header4 = (TextView)view.findViewById(R.id.txt_login_sub_header4);
        text_learn_more = (TextView)view.findViewById(R.id.txt_learn_more);
        //text_learn_more.setPaintFlags(switch_user.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // making underline of text

        text_download_qulity = (TextView)view.findViewById(R.id.text_download_qulity);
        text_spotify_help = (TextView)view.findViewById(R.id.text_spotify_help);
        arrow1=(TextView)view.findViewById(R.id.arrow1);
        arrow2=(TextView)view.findViewById(R.id.arrow2);
        arrow3=(TextView)view.findViewById(R.id.arrow3);
        arrow4=(TextView)view.findViewById(R.id.arrow4);

        text_logout.setTypeface(custom_font);
        switch_user.setTypeface(custom_font);
        text_download_qulity.setTypeface(custom_font);
        text_spotify_help.setTypeface(custom_font);
        text_username.setTypeface(custom_font);
        text_logged_at.setTypeface(custom_font);
        arrow1.setTypeface(custom_font);
        arrow2.setTypeface(custom_font);
        arrow3.setTypeface(custom_font);
        arrow4.setTypeface(custom_font);
        btn_login.setTypeface(spotify_custom_font);

        text_header.setTypeface(spotify_custom_bold);
        text_sub_header1.setTypeface(spotify_custom_font);
        text_sub_header2.setTypeface(spotify_custom_font);
        text_sub_header3.setTypeface(spotify_custom_font);
        text_sub_header4.setTypeface(spotify_custom_font);
        text_learn_more.setTypeface(spotify_custom_font);
        text_head_spotify.setTypeface(custom_bold);

        switch_user.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_logout.setOnClickListener(this);
        text_download_qulity.setOnClickListener(this);
        text_learn_more.setOnClickListener(this);
        text_spotify_help.setOnClickListener(this);

        if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)){
            Log.e(TAG,"Login is there "+spotifyPref.getString(SpotifySessionManager.USER_NAME,"")+" "+!getActivity().isDestroyed());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.TOP;
            frame_layout.setLayoutParams(params);
            login_layout.setVisibility(View.GONE);
            spotify_header.setVisibility(View.VISIBLE);
            logout_layout.setVisibility(View.VISIBLE);
            text_username.setVisibility(View.VISIBLE);
            text_username.setText(spotifyPref.getString(SpotifySessionManager.USER_NAME,""));
            globalClass.spotify_tick();
        }else{
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            frame_layout.setLayoutParams(params);
            spotify_header.setVisibility(View.GONE);
            logout_layout.setVisibility(View.GONE);
            login_layout.setVisibility(View.VISIBLE);
            text_username.setVisibility(View.GONE);
        }
        spotifyLoginLogout();
        context.registerReceiver(spotify_login,intentFilter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume ");
        globalClass.process_GoingOn = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
    }

    public void spotify_logout(){
        globalClass.send_set_spotifylogout();
        globalClass.notifyBrowesFragment("Logout");
        globalClass.count_offset=0;
        globalClass.count=0;
        globalClass.arrayPlayList.clear();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        frame_layout.setLayoutParams(params);
        spotify_header.setVisibility(View.GONE);
        logout_layout.setVisibility(View.GONE);
        login_layout.setVisibility(View.VISIBLE);
        text_username.setVisibility(View.GONE);
        GlobalClass.spotify_frag_status = false;
        globalClass.spotify_status = false;
        spotifySessionManager.clearSession();
        globalClass.spotify_tick();
        globalClass.stopRefreshToken();

    }


    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id){
            case R.id.button_login :
                globalClass.showProgressBar(getActivity());
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    new Handler().post(returnRes);
                    //Thread thread = new Thread(null, internetThread);
                    //thread.start();
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
            case R.id.text_log_out :
                if (!globalClass.syncing_status) {
                            if(globalClass.mighty_ble_device != null) {
                                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                                header_txt=(TextView)promptView.findViewById(R.id.text_header);
                                message_txt=(TextView)promptView.findViewById(R.id.text_message);
                                header_txt.setText("Are you sure?");
                                message_txt.setText("For security purposes, logging out of Spotify in the Mighty app will block playback on your Mighty. If you logout, you can re-enable playback by connecting your Mighty to the mobile app.");
                                mMaterialDialog = new MaterialDialog(getActivity())
                                        .setView(promptView)
                                        .setPositiveButton(getString(R.string.Logout), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                spotify_logout();
                                                mMaterialDialog.dismiss();

                                            }
                                        })
                                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mMaterialDialog.dismiss();
                                    }
                                });
                                mMaterialDialog.show();
                            }
                            else {
                                spotify_logout();
                            }


                }else globalClass.alertDailogSingleText(getString(R.string.playlist_sync_message_spotifylogout),getActivity());
                break;
            case R.id.txt_switchuser :
                globalClass.userActivityMode = 2;
                globalClass.send_set_spotifylogout();
               // AuthenticationClient.stopLoginActivity(getActivity(), globalClass.REQUEST_CODE);
                //AuthenticationClient.clearCookies(context);
                login_layout.setVisibility(View.VISIBLE);
                logout_layout.setVisibility(View.GONE);
                text_username.setVisibility(View.GONE);
                //text_spotify_user_name.setText(globalClass.spotifyLogin.getUsername());
                globalClass.notifyBrowesFragment("Logout");
                //Login page should be
                GlobalClass.spotify_frag_status=false;
                GlobalClass.spotify_status=false;
                globalClass.spotify_tick();
                spotifySessionManager.clearSession();

                break;
            case R.id.text_download_qulity :
                if (globalClass.mighty_ble_device == null){
                    setupDailoglogout(getString(R.string.download_quality_msg),getString(R.string.mighty_not_connected_title));
                }else if (globalClass.syncing_status) {
                    setupDailoglogout(getString(R.string.playlist_selectwhile_sync_meaage),getString(R.string.download_bitrate_change_syncing_tittle));
                }else{
                    globalClass.process_GoingOn = true;
                    startActivity(new Intent(context, DownloadQualityActivity.class));
                }
                break;
            case R.id.txt_learn_more :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    globalClass.process_GoingOn = true;
                    Intent mighty_help_intent = new Intent(getActivity(),MightyHelpActivity.class);
                    mighty_help_intent.putExtra("FromData","SpotifyPremium");
                    startActivity(mighty_help_intent);
                }else globalClass.toastDisplay(getString(R.string.check_internet));

                break;
            case R.id.text_spotify_help :
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected){
                    globalClass.process_GoingOn = true;
                    Intent mighty_help_intent = new Intent(getActivity(),MightyHelpActivity.class);
                    mighty_help_intent.putExtra("FromData","SpotifyHelp");
                    startActivity(mighty_help_intent);
                }else globalClass.toastDisplay(getString(R.string.check_internet));
                break;
        }
    }

    private Runnable progressBarStop = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG," isConnected progressBarStop"+isConnected);
            globalClass.dismissProgressBar();
            globalClass.toastDisplay(getString(R.string.check_internet));
        }
    };

    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
                PackageManager pm = getActivity().getApplicationContext().getPackageManager();
                ComponentName compName = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".AliyasSpotifyActivity");
                ComponentName compNameLaunch = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".AliyasLaunchTabActivity");
                pm.setComponentEnabledSetting(compName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(compNameLaunch,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                loginSpotify();
        }
    };

    private void loginSpotify(){
        globalClass.userActivityMode = 2;
        parentTab = (TabActivity) getActivity().getParent();
        SpotifyAuthHelper.startSpotifyAuthFlow(parentTab,
                globalClass.CLIENT_ID,
                globalClass.REDIRECT_URI,
                globalClass.LOCAL_STATE,
                globalClass.AUTHORIZATION_CODE,
                globalClass.REQUESTED_SCOPES,
                globalClass.REQUESTED_SCOPES_ARRAY,
                globalClass.REQUEST_CODE
        );
        globalClass.process_GoingOn = true;
    }
    private void setupDailoglogout(String message,String tittle){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(tittle);
        message_txt.setText(message);
        mMaterialDialog = new MaterialDialog(getActivity())
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                    }
                });
        mMaterialDialog.show();
    }

    private void spotifyLoginLogout(){
        intentFilter = new IntentFilter("spotify.login.successful");
        spotify_login = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                String value = spotify.getExtras().getString("login_status");
                if(value.equals("Login")) {
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.TOP;
                    frame_layout.setLayoutParams(params);
                    spotify_header.setVisibility(View.VISIBLE);
                    globalClass.process_GoingOn = false;
                    globalClass.dismissProgressBar();
                    login_layout.setVisibility(View.GONE);
                    logout_layout.setVisibility(View.VISIBLE);
                    text_username.setVisibility(View.VISIBLE);
                    text_username.setText(globalClass.spotifyLogin.getUsername());

                }else{
                    if(getActivity() != null && !getActivity().isDestroyed()){
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.CENTER;
                        frame_layout.setLayoutParams(params);
                        spotify_header.setVisibility(View.GONE);
                        login_layout.setVisibility(View.VISIBLE);
                        logout_layout.setVisibility(View.GONE);
                        text_username.setVisibility(View.GONE);

                    }
                }
            }
        };
    }


    public void onReceiveMessage(int msgId, int msgType){

        Log.e(TAG,"Spotify_fragment msgID "+msgId +" msgype "+ msgType);
        if(msgId==14){
            if (msgType==301){
                //Check music activity
                // globalClass.alertdialogmighty(getActivity(),2);
            }
            if(msgType==200){
                Log.e(TAG,"spotify set request received");
            }
        }

    }
    private void send_set_spotify(){
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 14;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG," spotify_set_structure_done "+globalClass.spotifyLogin.getUsername());
    }
    private void prgrssDailogShow(){
        if(progressDialog != null){
            if(progressDialog.isShowing())
                return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    /*private void progrssDailogCancel(){
        if(progressDialog != null){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }*/

}
