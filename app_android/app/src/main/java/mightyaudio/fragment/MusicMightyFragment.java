package mightyaudio.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mightyaudio.Model.Playlist;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mighty.audio.R;
import mightyaudio.adapter.MusicMightyAdapter;
import mightyaudio.adapter.YourMusicAdapter;
import mightyaudio.ble.BluetoothLeService;
import mightyaudio.core.AnimationFactory;
import mightyaudio.core.CircleView;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.PinkCircle;
import mightyaudio.core.RectangleProgress;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

import static android.view.View.GONE;
import static mightyaudio.adapter.YourMusicAdapter.selectedlist;
import static mightyaudio.fragment.YourMusicFragment.memory_level;
import static mightyaudio.fragment.YourMusicFragment.pink_level;
import static mightyaudio.fragment.YourMusicFragment.pink_memory_level;

public class MusicMightyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public MusicMightyAdapter music_Adapter;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    private String TAG = MusicMightyFragment.class.getSimpleName();

    private TextView battery_available, storege_capacity, refresh_playlsit,txt_stay_fresh,text_storage,text_battery;
    private LinearLayout refresh_layout;
    FrameLayout refresh_playlist;
    GlobalClass globalClass = GlobalClass.getInstance();

    // Font declaration
    Typeface custom_font_light,custom_bold_font;
    ArrayList<Playlist> mighty_playlist;
    static ArrayList<Playlist> downloaded_mighty_playlist;
    static ArrayList<Playlist> downloading_mighty_playlist;
    static ArrayList<Playlist> downloading_delete_mighty_playlist;
    ProgressDialog progressDialog;
    int track_count_getmemory = 0;
    //boolean playlist_not_deleted = false;

    TCPClient tcpClient;
    private YourMusicAdapter yourMusicAdapter;
    private  Context context;
    private ListView listView;
    private TextView txt_autofill,text_message,text_download_percentage;
    private View headerView,footerView;
    private ViewFlipper music_viewFlipper;
    private FrameLayout frame_flip1,frame_flip2;
    private RectangleProgress rect_prog;
    private CircleView circleViewBlue;
    private PinkCircle circleViewPink;
    private int width ;
    private float consumedMemory;
    private int  height ;
    private LinearLayout before_layout;
    private ImageView image_battery_display_charging;
    private TextView text_after_connected,text_before_connected;
    private YourMusicFragment.BrowesFragmentCommunication browesFragmentCommunication;
    private Musicmightysync musicmightysync;
    private IntentFilter intentFilter ;
    private BroadcastReceiver clearPlayListSwitch;
    private ProgressBar progress_wait,download_progress;
    private boolean isConnected;
    private ColorStateList defaultColors;
    private View promptView;
    private TextView header_txt;
    private TextView message_txt;
    private SwipeRefreshLayout swipeLayout;



    public interface Musicmightysync{
        public void sync_music_frag(int flag);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.context= context;
        musicmightysync=(Musicmightysync)context;
        browesFragmentCommunication = (YourMusicFragment.BrowesFragmentCommunication)context;
        Log.e(TAG,"onAttach");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG,"onActivityCreated");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.music_mighty_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        text_message =(TextView)view.findViewById(R.id.text_message);
        before_layout = (LinearLayout)view.findViewById(R.id.before_layout);
        text_after_connected = (TextView)view.findViewById(R.id.text_after_connected);
        text_before_connected = (TextView)view.findViewById(R.id.text_before_connected);
        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.mighty_swipe_layout);
        custom_font_light = Typeface.createFromAsset(getActivity().getAssets(), "serenity-light.ttf");
        custom_bold_font = Typeface.createFromAsset(getActivity().getAssets(), "serenity-bold.ttf");
        headerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mighty_fragment_header, null, false);
       // footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_view, null, false);
        listView.setNestedScrollingEnabled(true);
        listView.addHeaderView(headerView);
        //listView.addFooterView(footerView);
        mighty_playlist = new ArrayList<Playlist>();
        downloaded_mighty_playlist = new ArrayList<Playlist>();
        downloading_mighty_playlist = new ArrayList<Playlist>();
        downloading_delete_mighty_playlist = new ArrayList<Playlist>();
        mighty_playlist.addAll(globalClass.mighty_playlist.values());
        music_Adapter = new MusicMightyAdapter(getActivity(),mighty_playlist);
        music_Adapter.setMode(Attributes.Mode.Single);
        listView.setAdapter(music_Adapter);

        refresh_layout = (LinearLayout) headerView.findViewById(R.id.refresh_layout);
        refresh_playlsit = (TextView) headerView.findViewById(R.id.txt_refresh);
        txt_stay_fresh = (TextView) headerView.findViewById(R.id.txt_stay_fresh);
        music_viewFlipper = (ViewFlipper) headerView.findViewById(R.id.viewFlipper);
        frame_flip1 = (FrameLayout)headerView.findViewById(R.id.flip_side1);
        rect_prog = (RectangleProgress) headerView.findViewById(R.id.rect_prog);
        frame_flip2 = (FrameLayout)headerView.findViewById(R.id.flip_side2);
        download_progress = (ProgressBar)headerView.findViewById(R.id.download_progress);
        progress_wait = (ProgressBar)headerView.findViewById(R.id.progress_wait);
        text_download_percentage = (TextView)headerView.findViewById(R.id.text_download_percentage);
        image_battery_display_charging = (ImageView) headerView.findViewById(R.id.image_battery_display_charging);
        image_battery_display_charging.setVisibility(View.INVISIBLE);
        circleViewPink = (PinkCircle) headerView.findViewById(R.id.pink_storage);
        circleViewBlue = (CircleView) headerView.findViewById(R.id.blue_storage);
        //scroll_up.setPaintFlags(scroll_up.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        refresh_playlist = (FrameLayout) headerView.findViewById(R.id.refresh_playlist);

        storege_capacity = (TextView) headerView.findViewById(R.id.storage_capacity);
        text_battery = (TextView) headerView.findViewById(R.id.text_battery);
        text_storage = (TextView) headerView.findViewById(R.id.text_storage);
        battery_available = (TextView) headerView.findViewById(R.id.playback);
        defaultColors =  battery_available.getTextColors();

        swipeLayout.setOnRefreshListener(this);
        before_layout.setVisibility(View.VISIBLE);
        text_after_connected.setVisibility(GONE);
        refresh_layout.setVisibility(GONE);
        tcpClient = globalClass.tcpClient;
        text_download_percentage.setTypeface(custom_font_light);
        storege_capacity.setTypeface(custom_bold_font);
        text_battery.setTypeface(custom_font_light);
        text_storage.setTypeface(custom_font_light);
        battery_available.setTypeface(custom_bold_font);
        refresh_playlsit.setTypeface(custom_font_light);
        txt_stay_fresh.setTypeface(custom_font_light);
        text_after_connected.setTypeface(custom_font_light);
        text_before_connected.setTypeface(custom_font_light);

        //Custom Toast
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        promptView = layoutInflater.inflate(R.layout.custom_toast, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);


        refresh_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.mighty_ble_device != null) {
                   isConnected = ConnectivityReceiver.isConnected();
                    if(isConnected)
                    {
                        if (globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals("")) {
                            if (globalClass.spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN, false)) {
                                if (!globalClass.syncing_status) {
                                    if (!globalClass.mighty_playlist.isEmpty()) {
                                        globalClass.showProgressBar(getActivity());
                                        if(!downloading_mighty_playlist.isEmpty())
                                            downloading_mighty_playlist.clear();

                                        for (Playlist value : globalClass.mighty_playlist.values()) {
                                            Log.e(TAG, "mighty_playlist values" + value.getName());
                                            downloading_mighty_playlist.add(value);
                                        }
                                        new Handler().post(returnRes);

                                    }else globalClass.toastDisplay("Playlist are Empty");
                                } else globalClass.hardwarecompatibility(getActivity(),"The playlists on your Mighty cannot be refreshed during sync","Please wait for the sync in progress to complete and try again");
                            } else  globalClass.hardwarecompatibility(getActivity(),getString(R.string.stay_fresh_not_possible_tittle),"Please login to your Spotify account and try again");
                        } else globalClass.hardwarecompatibility(getActivity(),getString(R.string.stay_fresh_not_possible_tittle),"Please connect your Mighty to WiFi network and try  again");
                    }else globalClass.hardwarecompatibility(getActivity(),getString(R.string.stay_fresh_not_possible_tittle),"No Internet connection detected. Please connect your phone to a WiFi or cellular network and try again.");
                }else globalClass.toastDisplay("Please Connect your Mighty");
            }
        });

        ViewTreeObserver vto = frame_flip1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                    frame_flip1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width  = frame_flip1.getMeasuredWidth();
                height = frame_flip1.getMeasuredHeight();
                Log.e(TAG,"width hight "+width+" "+height);

            }
        });
        frame_flip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!globalClass.syncing_status)
                    AnimationFactory.flipTransition(music_viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        });
        frame_flip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationFactory.flipTransition(music_viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        });
        spotifyLoginLogout();
        context.registerReceiver(clearPlayListSwitch,intentFilter);
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
        rect_prog.setVisibility(View.INVISIBLE);

        if(globalClass.mighty_ble_device != null){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(globalClass.mighty_ble_device!=null) {
                        memory_info();
                        battery_info();
                    }
                }
            },2000);
        }

        return view;
    }

    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            if (!globalClass.arrayPlayList.isEmpty())
                globalClass.arrayPlayList.clear();

            Log.e(TAG,"globalClass.arrayPlayList "+globalClass.arrayPlayList.size());

            globalClass.count_offset = 0;
            globalClass.count = 0;
            if(!selectedlist.isEmpty())
                selectedlist.clear();
            globalClass.retrivePlayListFromSpotify(0, 50, 1);
            Log.e(TAG, "When Trigger ");
        }
    };

    public void PlayListSizeInfo(float Track_size) {
        circleViewPink.setVisibility(View.VISIBLE);
        Log.e(TAG,"global storage "+ globalClass.memory.getStorage_total());
        consumedMemory = globalClass.memory.getStorage_total() - globalClass.memory.getStorage_free();
        pink_memory_level = (int) (((consumedMemory + Track_size) / globalClass.memory.getStorage_total()) * 100);
        Log.e(TAG,"covert_perc_music_mighty "+ pink_memory_level);
        circleViewPink.paint.setColor(Color.parseColor("#FF8E88"));
        circleViewPink.left=width/8;
        circleViewPink.right=7*width/8;
        circleViewPink.bottom=7*height/8;
        circleViewPink.value=circleViewPink.bottom;
        pink_level = (pink_memory_level) * (circleViewPink.bottom - height / 8) / 100;
        System.out.println("pink_level :"+pink_level);
        circleViewPink.setLevel(pink_level);
        circleViewPink.top=circleViewPink.bottom-pink_level;
        circleViewPink.setPath();
    }


    public void refreshPlayList(){
           // progrssDailogCancel();
        globalClass.dismissProgressBar();
            updatePlaylistTrigger();
            globalClass.update_playlist = true;
            for (Map.Entry<String, Playlist> entry : globalClass.mighty_playlist.entrySet()) {
                Log.e(TAG, "Key values " + entry.getKey() + " " + globalClass.arrayPlayList.get(entry.getKey()));
                if (globalClass.arrayPlayList.get(entry.getKey()) != null) {
                    globalClass.update_playlist_array.add(globalClass.arrayPlayList.get(entry.getKey()));
                } else
                    globalClass.delete_playlist.add(entry.getValue());
            }

            if (!globalClass.delete_playlist.isEmpty()){
                send_delete_playlist();
                Log.e(TAG,"Delete Playlis request send");
            }

            if (globalClass.update_playlist_array.isEmpty() && globalClass.delete_playlist.isEmpty()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (progressDialog != null)
                            progressDialog.cancel();
                        Log.e(TAG, "Empty_array");
                    }
                }, 6000);
            }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!globalClass.update_playlist_array.isEmpty()){
                    update_playlist();
                    Log.e(TAG, "update_playlist is triggered");
                }
            }

        }).start();
    }
    private void updatePlaylistTrigger(){
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
        if (music_viewFlipper.getDisplayedChild() == 1)
            AnimationFactory.flipTransition(music_viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
        progress_wait.setVisibility(View.VISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        download_progress.setVisibility(View.INVISIBLE);
        musicmightysync.sync_music_frag(2);
    }
    public void bleDiscovered(){

        //Blue
        circleViewBlue.paint.setColor(Color.parseColor("#00C2F3"));
        circleViewBlue.left = width / 8;
        circleViewBlue.right = 7 * width / 8;
        circleViewBlue.bottom = 7 * height / 8;
        float level_blue = (0.0f) * (circleViewBlue.bottom - height / 8) / 100;
        circleViewBlue.top = circleViewBlue.bottom - level_blue;
        circleViewBlue.level = level_blue;
        circleViewBlue.value = circleViewBlue.bottom;
        circleViewBlue.setPath();

        //Pink
        circleViewPink.paint.setColor(Color.parseColor("#FF8E88"));
        circleViewPink.left=width/8;
        circleViewPink.right=7*width/8;
        circleViewPink.bottom=7*height/8;
        circleViewPink.value=circleViewPink.bottom;
        pink_level = (0.0f) * (circleViewPink.bottom - height / 8) / 100;
        circleViewPink.setLevel(pink_level);
        circleViewPink.top=circleViewPink.bottom-pink_level;
        circleViewPink.setPath();
        memory_level = 0.0f;
        pink_memory_level = 0.0f;
        pink_level = 0.0f;
        circleViewPink.setVisibility(View.VISIBLE);
        circleViewBlue.setVisibility(View.VISIBLE);
        rect_prog.setVisibility(View.VISIBLE);

        before_layout.setVisibility(View.GONE);
        refresh_layout.setVisibility(GONE);
        text_after_connected.setVisibility(View.VISIBLE);
    }

    public void btnSyniTriger(){
        if (GlobalClass.mighty_ble_device != null) {
                circleViewPink.setVisibility(View.INVISIBLE);
                circleViewBlue.setVisibility(View.INVISIBLE);
                if (music_viewFlipper.getDisplayedChild() == 1)
                    AnimationFactory.flipTransition(music_viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                progress_wait.setVisibility(View.VISIBLE);
                text_download_percentage.setVisibility(View.INVISIBLE);
                download_progress.setVisibility(View.INVISIBLE);

            //Copy Selected Playlist Data to Global class
            for (Playlist value : yourMusicAdapter.selectedlist.values()){
                try {
                    Playlist copy_obj = (Playlist) value.clone();
                    copy_obj.setTracks_count("Waiting to sync");
                    copy_obj.setOffline(0);
                    globalClass.mighty_playlist.put(value.getUri(),copy_obj);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            if (!mighty_playlist.isEmpty())
                mighty_playlist.clear();
            mighty_playlist.addAll(globalClass.mighty_playlist.values());
            before_layout.setVisibility(View.GONE);
            text_after_connected.setVisibility(GONE);
            refresh_layout.setVisibility(View.VISIBLE);
            music_Adapter.notifyDataSetChanged();
        }
    }

    public void btnSyniCancel(){
        progress_wait.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        download_progress.setVisibility(View.INVISIBLE);
    }



    public void bleDisconnected(){
        if (progressDialog != null)
            progressDialog.cancel();
        track_count_getmemory =0;
        download_progress.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        progress_wait.setVisibility(View.INVISIBLE);
        image_battery_display_charging.setVisibility(View.INVISIBLE);
        rect_prog.level = 0.0f;
        rect_prog.setPaths();
        memory_level = 0.0f;
        circleViewBlue.level= 0.0f;
        circleViewBlue.setPath();
        circleViewPink.level = 0.0f;
        circleViewPink.setPath();
        before_layout.setVisibility(View.VISIBLE);
        refresh_layout.setVisibility(GONE);
        text_after_connected.setVisibility(View.GONE);
        Log.e(TAG,"Disconnected_mightyfrag");
        if (!mighty_playlist.isEmpty()){
            mighty_playlist.clear();
        }
        if (!globalClass.mighty_playlist.isEmpty())
            globalClass.mighty_playlist.clear();
        music_Adapter.notifyDataSetChanged();
        storege_capacity.setText("0%");
        storege_capacity.setTypeface(custom_bold_font);
        battery_available.setText("0%");
        battery_available.setTextColor(defaultColors);
        battery_available.setTypeface(custom_bold_font);
        rect_prog.setVisibility(View.INVISIBLE);
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        //Coming from setup flow
        if (globalClass.mighty_ble_device != null) {
            if (!globalClass.syncing_status) {
                circleViewPink.setVisibility(View.VISIBLE);
                circleViewBlue.setVisibility(View.VISIBLE);
                rect_prog.setVisibility(View.VISIBLE);
                before_layout.setVisibility(View.GONE);
                refresh_layout.setVisibility(View.VISIBLE);
                if (mighty_playlist.isEmpty()) {
                    text_after_connected.setVisibility(View.VISIBLE);
                    refresh_layout.setVisibility(GONE);
                } else {
                    text_after_connected.setVisibility(GONE);
                }
            }
    }
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        if(globalClass.mighty_ble_device != null)
        timer.schedule(timerTask, 45000, 60000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(globalClass.mighty_ble_device != null && BluetoothLeService.split_flag == 0 && !globalClass.syncing_status) {
                            get_battery_info();
                        }
                    }
                });

            }
        };
    }


    public void onPause() {
        super.onPause();
        Log.e(TAG,"Onpause in mighty");
        stoptimertask();
    }




    public void send_delete_playlist()
    {
       //Log.e(TAG,"mighty_dele");
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 6;
        tcpClient.SendData(mightyMessage);
    }
    public void get_playlist()
    {

        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        mightyMessage.MessageID = 6;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG," Sent get mighty_playlist request ");
        try {
            Thread.sleep(500);
            get_memory();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void get_battery_info(){
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        mightyMessage.MessageID = 5;
        tcpClient.SendData(mightyMessage);
    }

    public void update_playlist()
    {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 7;
        //Send the GET request to Mighty Device, Note:Only Header is Sent.
        tcpClient.SendData(mightyMessage);
    }

    public void get_memory()
    {
        Log.e(TAG,"sent memory get request");
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        mightyMessage.MessageID = 10;
        tcpClient.SendData(mightyMessage);

    }


    public void onReceivemsg(int msgId, int msgType)
    {
            switch (msgId)
            {
                case Constants.MSG_BIT_RATE_MODE:
                    if (msgType == 200) {
                        if (!globalClass.mighty_playlist.isEmpty()) {
                            if (!globalClass.arrayPlayList.isEmpty()) {
                                for (Map.Entry<String, Playlist> entry : globalClass.mighty_playlist.entrySet()) {
                                    if (globalClass.arrayPlayList.get(entry.getKey()) != null) {
                                        globalClass.arrayPlayList.get(entry.getKey()).setOffline(0);
                                        Log.e(TAG, "getOffline_mighty_fragment " + globalClass.arrayPlayList.get(entry.getKey()).getOffline());
                                    }
                                }
                            }
                        }
                        if (!mighty_playlist.isEmpty())
                            mighty_playlist.clear();
                        if (!globalClass.mighty_playlist.isEmpty())
                            globalClass.mighty_playlist.clear();
                        music_Adapter.notifyDataSetChanged();
                        get_playlist();
                    }
                    break;
                case Constants.MSG_ID_PLAYLIST_ID:
                    if (msgType == 202) {
                        music_Adapter.notifyDataSetChanged();
                        globalClass.setgetplaylist = true;
                        Log.e(TAG, "received playlist in handler " + msgId);
                        if (progressDialog != null)
                            progressDialog.cancel();

                        if (globalClass.mighty_ble_device != null) {
                            if (!globalClass.mighty_playlist.isEmpty()) {
                                if (!mighty_playlist.isEmpty())
                                    mighty_playlist.clear();
                                mighty_playlist.addAll(globalClass.mighty_playlist.values());
                                Log.e(TAG, "Music Mighty Size in globalclass OnReceive " + globalClass.mighty_playlist.size());
                                before_layout.setVisibility(View.GONE);
                                text_after_connected.setVisibility(GONE);
                                refresh_layout.setVisibility(View.VISIBLE);
                                music_Adapter.notifyDataSetChanged();
                            } else {
                                if (!mighty_playlist.isEmpty())
                                    mighty_playlist.clear();
                                music_Adapter.notifyDataSetChanged();
                                refresh_layout.setVisibility(GONE);
                                before_layout.setVisibility(View.GONE);
                                text_after_connected.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (msgType == 200)    // Checking OK response
                    {
                        if (progressDialog != null)
                            if (progressDialog.isShowing())
                                progressDialog.cancel();
                        if (globalClass.mighty_playlist.isEmpty()) {
                            refresh_layout.setVisibility(GONE);
                            text_after_connected.setVisibility(View.VISIBLE);
                            music_btnEnable();
                        }
                        Log.e(TAG, "Deleted successfully");
                        get_memory();
                        if (!globalClass.delete_playlist.isEmpty()) {
                            if (globalClass.syncing_status) {
                                if (!downloaded_mighty_playlist.isEmpty())
                                    downloaded_mighty_playlist.clear();

                                for (int i = 0; i < downloading_mighty_playlist.size(); i++) {
                                    if (globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()) != null) {
                                        downloaded_mighty_playlist.add(globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()));
                                    }else{
                                        downloading_delete_mighty_playlist.add(downloading_mighty_playlist.get(i));
                                    }
                                }

                                if(!downloading_delete_mighty_playlist.isEmpty()){
                                    for(int k=0;k<downloading_delete_mighty_playlist.size();k++) {
                                        downloading_mighty_playlist.remove(downloading_delete_mighty_playlist.get(k));
                                    }
                                    downloading_delete_mighty_playlist.clear();
                                }

                                if (downloaded_mighty_playlist.isEmpty()) {
                                    music_btnEnable();
                                    get_playlist();
                                }
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                        }
                                        globalClass.delete_playlist.clear();
                                    }
                                });
                            } else
                                globalClass.delete_playlist.clear();
                        }
                        if (progressDialog != null) {
                            progressDialog.cancel();
                        }
                    }else if(msgType == 201){
                        delete_mighty_playlist();
                    }
                    break;
                case Constants.MSG_ID_DOWNLOAD_ID:
                    if (msgType == 200) {
                        globalClass.download_status = false;
                        Log.e(TAG, "download ID Events_received");
                        get_playlist();
                    } else if (msgType == 201) {
                        if (!downloading_mighty_playlist.isEmpty()) {
                            for (int i = 0; i < downloading_mighty_playlist.size(); i++) {
                                if (globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()) != null) {
                                    downloaded_mighty_playlist.add(globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()));
                                }
                            }
                            for (int j = 0; j < downloaded_mighty_playlist.size(); j++) {
                                globalClass.mighty_playlist.remove(downloaded_mighty_playlist.get(j).getUri());
                                mighty_playlist.remove(downloaded_mighty_playlist.get(j));
                            }

                            if (!downloaded_mighty_playlist.isEmpty())
                                downloaded_mighty_playlist.clear();
                            music_Adapter.notifyDataSetChanged();
                            music_btnEnable();
                            get_playlist();

                        }
                        if (globalClass.mighty_playlist.isEmpty()) {
                            refresh_layout.setVisibility(GONE);
                            text_after_connected.setVisibility(View.VISIBLE);
                        }
                    } else if (msgType == 102) {
                        if (globalClass.download.getStatus() != 0) {
                            music_btnDisable();
                          //  globalClass.download_error = false;
                            // syncing_status =true;
                        }
                        for (Map.Entry<String, Playlist> entry : globalClass.mighty_playlist.entrySet()) {
                            if (globalClass.download.getGetPlaylist_url().equals(entry.getKey())) {
                                globalClass.mighty_playlist.get(entry.getKey()).setTracks_count(String.valueOf(globalClass.download.getDownloaded_tracks()));
                                music_Adapter.notifyDataSetChanged();
                                track_count_getmemory++;
                                if (track_count_getmemory == 10) {
                                    get_memory();
                                    track_count_getmemory = 0;
                                }
                            }
                        }

                        if (globalClass.download.getStatus() == Constants.DL_FAILED) {
                            music_btnEnable();
                            get_playlist();
                        }

                        if (globalClass.download.getStatus() == Constants.DL_COMPLETED) {//| globalClass.download.getStatus() == Constants.DL_COMPLETED_WITH_ERROR) {
                            if (globalClass.download.getGetPlaylist_url() != null && globalClass.mighty_playlist.get(globalClass.download.getGetPlaylist_url()) != null) {
                                globalClass.mighty_playlist.get(globalClass.download.getGetPlaylist_url()).setOffline(globalClass.download.getStatus());
                                music_Adapter.notifyDataSetChanged();
                            }
                            Log.e(TAG,"size__before"+downloading_mighty_playlist.size());
                            for(int j =0;j<downloading_mighty_playlist.size();j++){
                                if(globalClass.mighty_playlist.get(downloading_mighty_playlist.get(j).getUri()) != null) {
                                    if (globalClass.mighty_playlist.get(downloading_mighty_playlist.get(j).getUri()).getOffline() == 1)//.equals(globalClass.download.getGetPlaylist_url()))
                                        downloading_mighty_playlist.remove(j);
                                }
                            }

                            track_count_getmemory = 0;
                            Log.e(TAG,"size__"+downloading_mighty_playlist.size());
                            if(downloading_mighty_playlist.isEmpty()){
                                music_btnEnable();
                                get_memory();
                            }


                            header_txt.setText(globalClass.download.getPlaylist_name());
                            message_txt.setText("sync completed");
                            Toast toast = new Toast(getActivity());
                            toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(promptView);
                            toast.show();
                            //globalClass.toastDisplay(globalClass.download.getPlaylist_name()+" sync completed");
                        }
                        if (globalClass.download.getStatus() == Constants.DL_COMPLETED_WITH_ERROR) {
                            track_count_getmemory = 0;
                            delete_mighty_playlist();
                            globalClass.alertDailogSingleText("We were unable to sync some of your songs. Click on the Mighty logo to see the number of songs that were successfully synced to each playlists. \n (Error 2)", getActivity());
                        }
                        if (globalClass.download.getStatus() == Constants.DL_COMPLETED && ((int) globalClass.download.getProgress() == 100)) {
                            Log.e(TAG, "download_completed");
                      //      globalClass.download_error = false;
                            music_btnEnable();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    get_playlist();
                                }
                            },1400);

                        }

                        // Update Playlist
                        if (globalClass.download.getStatus() == Constants.DL_REFRESH_COMPLETED | globalClass.download.getStatus() == Constants.DL_REFRESH_COMPLETED_WITH_ERROR) {
                            music_btnEnable();
                            get_playlist();
                        }

                        switch (globalClass.download.getStatus()) {
                            case Constants.DL_FAILED:
                                delete_mighty_playlist();
                                globalClass.hardwarecompatibility(getActivity(), getString(R.string.download_failed_header), getString(R.string.download_failed_body));
                                break;
                            case Constants.DL_WIFI_ERROR:
                                delete_mighty_playlist();
                                globalClass.hardwarecompatibility(getActivity(), getString(R.string.poor_network_head), getString(R.string.some_playlist_not_synced));
                                break;

                            case Constants.DL_SPOTIFY_LOGIN_ERROR:
                                delete_mighty_playlist();
                                globalClass.hardwarecompatibility(getActivity(),getString(R.string.spotify_error_header), getString(R.string.spotify_error_message));
                                break;

                            case Constants.DL_FAILED_STORAGE_ERROR:
                                delete_mighty_playlist();
                                globalClass.hardwarecompatibility(getActivity(),getString(R.string.storage_error_header),getString(R.string.storage_error_message));
                                break;

                            case Constants.DL_SP_PREFETCH_TIMEOUT:
                                delete_mighty_playlist();
                                globalClass.hardwarecompatibility(getActivity(), getString(R.string.poor_network_head), getString(R.string.some_playlist_not_synced));
                                break;
                            case Constants.DL_REFRESH_COMPLETED_WITH_ERROR:
                                delete_mighty_playlist();
                                globalClass.alertDailogSingleText("We were unable to refresh some of your playlists. Please go to the Mighty music tab, delete the playlist(s) with a grey checkmark and add the playlists to Mighty again later.\n (Error 7)", getActivity());
                                break;
                            case Constants.DL_FAILED_BIT_RATE_ERROR:
                                music_btnEnable();
                                delete_mighty_playlist();
                                globalClass.alertDailogSingleText("Bit Rate", getActivity());
                                break;
                        }
                    }
                    break;
                case Constants.MSG_ID_MEMORY_ID :
                    if (msgType == 202) {
                        memory_info();
                    }
                    break;
                case Constants.MSG_ID_BATTERY_INFO_ID :
                    Log.e(TAG, "received playlist in battery " + msgId);
                    battery_info();
                    break;
            }
        }

    private void delete_mighty_playlist(){
        if (!downloading_mighty_playlist.isEmpty()) {
            for (int i = 0; i < downloading_mighty_playlist.size(); i++) {
                if (globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()) != null) {
                    downloaded_mighty_playlist.add(globalClass.mighty_playlist.get(downloading_mighty_playlist.get(i).getUri()));
                }
            }
            for (int j = 0; j < downloaded_mighty_playlist.size(); j++) {
                if(globalClass.mighty_playlist.get(downloaded_mighty_playlist.get(j).getUri()).getTracks_count().equals("Syncing") | globalClass.mighty_playlist.get(downloaded_mighty_playlist.get(j).getUri()).getTracks_count().equals("Wait for Sync") ) {
                    globalClass.mighty_playlist.remove(downloaded_mighty_playlist.get(j).getUri());
                    mighty_playlist.remove(downloaded_mighty_playlist.get(j));
                }
            }
            for(int j=0;j< downloaded_mighty_playlist.size();j++) {
                if(globalClass.arrayPlayList.get(downloaded_mighty_playlist.get(j).getUri()) != null)
                    globalClass.arrayPlayList.get(downloaded_mighty_playlist.get(j).getUri()).setOffline(0);
            }
            if (!downloaded_mighty_playlist.isEmpty())
                downloaded_mighty_playlist.clear();
            music_Adapter.notifyDataSetChanged();
            music_btnEnable();
            get_playlist();
        }
        if (globalClass.mighty_playlist.isEmpty()) {
            refresh_layout.setVisibility(GONE);
            text_after_connected.setVisibility(View.VISIBLE);
        }
    }

    private void battery_info(){
        if (globalClass.battery_info.getStatus() == 1) {
            image_battery_display_charging.setVisibility(View.VISIBLE);
        } else {
            image_battery_display_charging.setVisibility(View.INVISIBLE);
        }
        battery_available.setText(globalClass.battery_info.getAvailablePercentage() + "%");
        rect_prog.left = width / 4;
        rect_prog.right = 3 * width / 4;
        rect_prog.bottom = 5 * height / 6;
        float level = globalClass.battery_info.getAvailablePercentage() * (rect_prog.bottom - height / 6) / 100;
        System.out.println("batterylevelfrom global+" + globalClass.battery_info.getAvailablePercentage());
        float value = 5 * height / 6;
        rect_prog.setValue(value);
        rect_prog.level = level;
        rect_prog.setPaths();
        rect_prog.setVisibility(View.VISIBLE);

        if (globalClass.battery_info.getAvailablePercentage() <= 10) {
            battery_available.setTextColor(Color.parseColor("#FFFB908C"));
        } else {
            battery_available.setTextColor(defaultColors);
            battery_available.setTypeface(custom_bold_font);
        }
    }

    private void memory_info(){
        Log.e(TAG,"storage Percent "+globalClass.memory.getStorageFullPercent()+" "+(float) globalClass.memory.getStorageFullPercent());
        storege_capacity.setText(globalClass.memory.getStorageFullPercent() + "% full");
        memory_level = (float) globalClass.memory.getStorageFullPercent();
        //Blue
        circleViewBlue.paint.setColor(Color.parseColor("#00C2F3"));
        circleViewBlue.left = width / 8;
        circleViewBlue.right = 7 * width / 8;
        circleViewBlue.bottom = 7 * height / 8;

        float level_blue = (memory_level) * (circleViewBlue.bottom - height / 8) / 100;
        //float level_blue = (55.0f) * (circleViewBlue.bottom - height / 8) / 100;
        Log.e(TAG,"level_blue_memory :" + level_blue);
        circleViewBlue.top = circleViewBlue.bottom - level_blue;
        circleViewBlue.level = level_blue;
        circleViewBlue.value = circleViewBlue.bottom;
        circleViewBlue.setPath();
    }


    private void music_btnEnable(){
        Log.e(TAG,"btn_enable_called");
        progress_wait.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        download_progress.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.VISIBLE);
        circleViewPink.paint.setColor(Color.parseColor("#FF8E88"));
        circleViewPink.left=width/8;
        circleViewPink.right=7*width/8;
        circleViewPink.bottom=7*height/8;
        circleViewPink.value=circleViewPink.bottom;
        pink_level = (0.0f) * (circleViewPink.bottom - height / 8) / 100;
        circleViewPink.setLevel(pink_level);
        circleViewPink.top=circleViewPink.bottom-pink_level;
        circleViewPink.setPath();
        pink_memory_level = 0.0f;
        pink_level = 0.0f;
    }

    private void music_btnDisable(){
        progress_wait.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.VISIBLE);
        download_progress.setVisibility(View.VISIBLE);
        text_download_percentage.setText((int) globalClass.download.getProgress() + "%");
        download_progress.setProgress((int) globalClass.download.getProgress());
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"onDetach");
    }

    private void spotifyLoginLogout(){
        intentFilter = new IntentFilter("spotify.login.switch");
        clearPlayListSwitch = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                if (!mighty_playlist.isEmpty()){
                    mighty_playlist.clear();
                }
                if (!globalClass.mighty_playlist.isEmpty())
                    globalClass.mighty_playlist.clear();
                music_Adapter.notifyDataSetChanged();
                if(!YourMusicAdapter.selectedlist.isEmpty())
                YourMusicAdapter.selectedlist.clear();

                if (globalClass.mighty_playlist.isEmpty()) {
                    refresh_layout.setVisibility(GONE);
                    text_after_connected.setVisibility(View.VISIBLE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        get_playlist();
                    }
                },2000);
            }
        };
    }

    // It will triger MusicMighty Adapter when  delete the playlist
    public void deletePlayListFromAdapter(Playlist play_delete) {
        send_delete_playlist();
        mighty_playlist.remove(play_delete);
        if(mighty_playlist.isEmpty()) {
            refresh_layout.setVisibility(GONE);
            text_after_connected.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onRefresh() {
            if(globalClass.mighty_ble_device != null){
                if(globalClass.setgetplaylist) {
                    if (!globalClass.syncing_status) {
                        get_playlist();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeLayout.setRefreshing(false);
                            }
                        }, 6000);
                    } else {
                        swipeLayout.setRefreshing(false);
                        globalClass.hardwarecompatibility(getActivity(), context.getString(R.string.pullto_reresh_doring_sync), context.getString(R.string.playlist_selectwhile_sync_meaage));
                    }
                }else {
                    swipeLayout.setRefreshing(false);
                    globalClass.toastDisplay("Please wait for few seconds");
                }
        }else{
                swipeLayout.setRefreshing(false);
                globalClass.toastDisplay("Please connect your Mighty");
            }

    }
}
