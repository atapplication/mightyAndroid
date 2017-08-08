package mightyaudio.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import mightyaudio.Model.Playlist;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.adapter.YourMusicAdapter;
import mightyaudio.core.AnimationFactory;
import mightyaudio.core.CircleView;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.PinkCircle;
import mightyaudio.core.RectangleProgress;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

import static mightyaudio.TCP.Constants.MSG_ID_BATTERY_INFO_ID;
import static mightyaudio.TCP.Constants.MSG_ID_DOWNLOAD_ID;
import static mightyaudio.TCP.Constants.MSG_ID_MEMORY_ID;
import static mightyaudio.fragment.MusicMightyFragment.downloaded_mighty_playlist;
import static mightyaudio.fragment.MusicMightyFragment.downloading_mighty_playlist;


public class YourMusicFragment extends Fragment implements YourMusicAdapter.BrowseFragmentCommunication,View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = YourMusicFragment.class.getSimpleName();
    Button btn_sync,header_button_sync;
    GlobalClass globalClass;
    public ListView listView;
    Typeface custom_font_light,custom_bold_font;
    private CircleView circleViewBlue;
    public static PinkCircle circleViewPink;
    public static float pink_level;
    public static float pink_memory_level;
    public static float memory_level;


    TextView storage, playback_hrs,text_storage,text_battery;
    float consumedMemory;
    ImageView autofill_tick,image_battery_display_charging;
    private ProgressBar progBar;
    private TextView text_download_percentage,text_loading,text_search,text_scroll_to_top;
    private YourMusicAdapter yourMusicAdapter;
    private Context context;
    private ArrayList<Playlist> arrayPlaylist = new ArrayList<Playlist>();
    private LinearLayout empty_layout;
    private  View headerView;
    private View footerView;
    private LinearLayout loding_layout;
    private ViewFlipper viewFlipper;
    private FrameLayout frame_flip1,frame_flip2,stick_header;
    private RectangleProgress rect_prog;
    private ImageView image_battery_display,image_storage_diplay,autofill_plus;
    public static int width ;
    public static int  height ;
    public static float battery_level;
    private ProgressBar download_progress,progress_wait;
    private LinearLayout search_layout,auto_fill_layout;
    private EditText inputSearch ;
    private TextView txt_autofill,text_message,txt_autofill_sub_head;
    private IntentFilter intentFilter ,intentFilter_wifi;
    IntentFilter intentFilter_changebitrate;
    BroadcastReceiver receiver_changebitrate,receiver_wifi;
    private FrameLayout auto_fill_frame,image_back_btn;
    private BroadcastReceiver login_logout_broadCast;

    //it will tell us weather to load more items or not
    public static boolean loadingMore = true;
    //public boolean play_list_triger = false;
   // public static boolean syncing_status = false;
    private String login_status;
    private BrowesFragmentSyning browesFragmentSyning;
    private SpotifySessionManager spotifySessionManager;
    private SharedPreferences spotifyPref;
    private LinearLayout serchbar_btn,searchbar_focus;
    private boolean isConnected;
    private ColorStateList defaultColors;
    private MaterialDialog  mMaterialDialog;
    private View promptView ;
    private TextView header_txt;
    private TextView message_txt;
    private SwipeRefreshLayout swipeLayout;
    private boolean pullTo_Refresh= false;
    private int count_offsetFail;
    private int count_Fail;
    private int count_TotalFail;
    private ImageView arraow_image;
    private boolean listsize_capture;
    private int last_visible;
    public Map<String,Playlist> temp_plalist = new LinkedHashMap<String,Playlist>();

    public interface BrowesFragmentCommunication {
        public void playListDeleted();
    }
    public interface BrowesFragmentSyning{
        public void browesSyncingTrigger(int value);
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.context = context;
        browesFragmentSyning = (BrowesFragmentSyning) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        globalClass = GlobalClass.getInstance();

        final View view = (View) inflater.inflate(R.layout.your_music_fragment, container, false);


        spotifySessionManager = new SpotifySessionManager(getActivity());
        spotifyPref = getActivity().getSharedPreferences(spotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);

        listView = (ListView) view.findViewById(R.id.listView);
        header_button_sync = (Button) view.findViewById(R.id.header_button_sync);
        text_message =(TextView)view.findViewById(R.id.text_message);
        arraow_image =(ImageView) view.findViewById(R.id.arraow_image);
        empty_layout = (LinearLayout) view.findViewById(R.id.empty_layout);
        stick_header = (FrameLayout) view.findViewById(R.id.stick_header);
        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.wifi_swipe_layout);
        custom_font_light = Typeface.createFromAsset(getActivity().getAssets(), "serenity-light.ttf");
        custom_bold_font = Typeface.createFromAsset(getActivity().getAssets(), "serenity-bold.ttf");
        headerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.browes_fragment_header, null, false);
        footerView =((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_view, null, false);
        listView.setNestedScrollingEnabled(true);
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);
        image_battery_display_charging = (ImageView) headerView.findViewById(R.id.image_battery_display_charging);
        image_battery_display_charging.setVisibility(View.INVISIBLE);
        yourMusicAdapter = new YourMusicAdapter(getActivity(), arrayPlaylist, this);
        listView.setAdapter(yourMusicAdapter);



        loding_layout = (LinearLayout)footerView.findViewById(R.id.loding_layout);
        text_loading =(TextView)footerView.findViewById(R.id.text_loading);
        auto_fill_frame = (FrameLayout) footerView.findViewById(R.id.auto_fill_frame);
        autofill_plus = (ImageView) footerView.findViewById(R.id.img_view_autofill);
        autofill_tick = (ImageView) footerView.findViewById(R.id.img_blue_tick_autofill);


        viewFlipper = (ViewFlipper) headerView.findViewById(R.id.viewFlipper);
        frame_flip1 = (FrameLayout)headerView.findViewById(R.id.flip_side1);
        download_progress = (ProgressBar)headerView.findViewById(R.id.download_progress);
        progress_wait = (ProgressBar)headerView.findViewById(R.id.progress_wait);
        text_download_percentage = (TextView)headerView.findViewById(R.id.text_download_percentage);
        search_layout = (LinearLayout)headerView.findViewById(R.id.search_layout);
        serchbar_btn = (LinearLayout)headerView.findViewById(R.id.serchbar_btn);
        searchbar_focus = (LinearLayout)headerView.findViewById(R.id.searchbar_focus);
        image_back_btn = (FrameLayout) headerView.findViewById(R.id.image_back_btn);
        text_search = (TextView) headerView.findViewById(R.id.text_search);
        auto_fill_layout = (LinearLayout)footerView.findViewById(R.id.auto_fill_layout);
        text_scroll_to_top = (TextView) footerView.findViewById(R.id.text_scroll_to_top);
        inputSearch = (EditText) headerView.findViewById(R.id.inputSearch);
        txt_autofill = (TextView) footerView.findViewById(R.id.txt_autofill);
        txt_autofill_sub_head = (TextView) footerView.findViewById(R.id.txt_autofill_sub_head);

        image_battery_display = (ImageView) headerView.findViewById(R.id.image_battery_display);
        image_storage_diplay = (ImageView) headerView.findViewById(R.id.image_storage_diplay);
        rect_prog = (RectangleProgress) headerView.findViewById(R.id.rect_prog);
        frame_flip2 = (FrameLayout)headerView.findViewById(R.id.flip_side2);
        circleViewPink = (PinkCircle) headerView.findViewById(R.id.pink_storage);
        circleViewBlue = (CircleView) headerView.findViewById(R.id.blue_storage);

       // progBar = (ProgressBar) headerView.findViewById(R.id.list_progressBar);
        btn_sync = (Button) headerView.findViewById(R.id.button_sync);
        storage = (TextView) headerView.findViewById(R.id.storage_capacity);
        text_battery = (TextView) headerView.findViewById(R.id.text_battery);
        text_storage = (TextView) headerView.findViewById(R.id.text_storage);
        playback_hrs = (TextView) headerView.findViewById(R.id.playback);
        defaultColors =  playback_hrs.getTextColors();

        //btn_sync.setEnabled(false);
        btn_sync.setTypeface(custom_font_light);
        text_search.setTypeface(custom_font_light);

        btn_sync.setTypeface(custom_font_light);
        header_button_sync.setTypeface(custom_font_light);
        text_loading.setTypeface(custom_font_light);
        text_download_percentage.setTypeface(custom_font_light);
        inputSearch.setTypeface(custom_font_light);
        txt_autofill.setTypeface(custom_font_light);
        txt_autofill_sub_head.setTypeface(custom_font_light);
        storage.setTypeface(custom_bold_font);
        playback_hrs.setTypeface(custom_bold_font);
        text_message.setTypeface(custom_font_light);
        text_battery.setTypeface(custom_font_light);
        text_storage.setTypeface(custom_font_light);
        text_scroll_to_top.setTypeface(custom_font_light);

        //For AutoFill_Plus
        swipeLayout.setOnRefreshListener(this);
        autofill_plus.setOnClickListener(this);
        autofill_tick.setOnClickListener(this);
        serchbar_btn.setOnClickListener(this);
        image_back_btn.setOnClickListener(this);
        text_scroll_to_top.setOnClickListener(this);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (globalClass.mighty_ble_device != null) {
                        memory_info();
                        battery_info();
                    }
                   }
            },4000);

        ViewTreeObserver vto = frame_flip1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    frame_flip1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    frame_flip1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                width  = frame_flip1.getMeasuredWidth();
                height = frame_flip1.getMeasuredHeight();
                Log.e(TAG,"width hight "+width+" "+height);

            }
        });

        //Initialy Invisival layout
        search_layout.setVisibility(View.GONE);
        auto_fill_layout.setVisibility(View.GONE);
        text_scroll_to_top.setVisibility(View.GONE);

        frame_flip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!globalClass.syncing_status)
                AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        });
        frame_flip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }

        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (inputSearch.getText().toString().matches("^ ") )
                    inputSearch.setText("");
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                yourMusicAdapter.filter(text);
            }
        });

        //setting  listener on scroll event of the list
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                Log.e(TAG,"item "+lastInScreen+" "+firstVisibleItem+" "+visibleItemCount);
                Log.e(TAG,"item position "+listView.getVisibility()+globalClass.plalistTotal);
                int lastVisibleposition = view.getLastVisiblePosition();
                if(0 < lastVisibleposition && !listsize_capture){
                    last_visible = view.getLastVisiblePosition() +2;
                    Log.e(TAG,"last Visible "+last_visible+" "+lastVisibleposition);
                    listsize_capture = true;
                }
                Log.e(TAG,"last Visible out side"+last_visible+" "+lastVisibleposition);
                if(last_visible <= lastVisibleposition && last_visible != 0){
                    stick_header.setVisibility(View.VISIBLE);
                }else{
                    stick_header.setVisibility(View.GONE);
                }
                if(globalClass.plalistTotal != 0 && !(loadingMore) && globalClass.count_offset < globalClass.plalistTotal && lastInScreen == totalItemCount ){
                    isConnected = ConnectivityReceiver.isConnected();
                    if(isConnected) {
                        loadingMore = true;
                        Log.e(TAG, "if part scrll");
                        Thread thread = new Thread(null, loadMoreListItems);
                        thread.start();
                    }else globalClass.toastDisplay(getString(R.string.check_internet));
                }
            }
        });

        //register the Broadcast while Login and Logout of Spotify
        spotifyLoginLogout();

        context.registerReceiver(login_logout_broadCast,intentFilter);
        btn_sync.setOnClickListener(this);
        header_button_sync.setOnClickListener(this);

        if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)){
            if(GlobalClass.mighty_ble_device == null)
                auto_fill_frame.setVisibility(View.INVISIBLE);
            search_layout.setVisibility(View.GONE);
            if(!globalClass.arrayPlayList.isEmpty())
            auto_fill_layout.setVisibility(View.VISIBLE);
            playListfailText(globalClass.playlist_status);
        }else{
            search_layout.setVisibility(View.GONE);
            auto_fill_layout.setVisibility(View.GONE);
            loding_layout.setVisibility(View.GONE);
            auto_fill_layout.setVisibility(View.GONE);
            empty_layout.setVisibility(View.VISIBLE);
            arraow_image.setVisibility(View.VISIBLE);
            text_message.setText("You'll need to go to the \n 'CONNECTIONS' tab and connect to \n your Spotify Premium account before \n you can browse your playlists.");
        }
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
        rect_prog.setVisibility(View.INVISIBLE);
        //retrivePlayList();

        //First time button should be disable with greayed color acording to new requirment
        btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        btn_sync.setEnabled(false);
        header_button_sync.setEnabled(false);
        //Coming from setup flow
        if (globalClass.mighty_ble_device != null){
            circleViewPink.setVisibility(View.VISIBLE);
            circleViewBlue.setVisibility(View.VISIBLE);
            rect_prog.setVisibility(View.VISIBLE);
            if(globalClass.wifi_status) {
                auto_fill_frame.setVisibility(View.VISIBLE);
                yourMusicAdapter.connection= true;
                yourMusicAdapter.notifyDataSetChanged();
            }
            else auto_fill_frame.setVisibility(View.INVISIBLE);
        }
    inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e(TAG,"Hide if condition after fragmenet");
                hideSoftKeboard();
                //beckButtonChangeSearch();
                return false;
            }
            return false;
        }
    });
        retrivePlayList(0);
        defineBroadCast(); //If Change the bitrate unselect plalist
        wifistatusBradCast();
        context.registerReceiver(receiver_wifi,intentFilter_wifi);
        context.registerReceiver(receiver_changebitrate,intentFilter_changebitrate);
        return view;
    }


    private void wifistatusBradCast(){
        intentFilter_wifi = new IntentFilter("wifi.connection.broadcast");
        receiver_wifi = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent wifistatus) {
                String value = wifistatus.getExtras().getString("wifi_status");
                Log.e(TAG,"wifi_connection :"+ value);
                if(value.equals("true")){
                    Log.e(TAG,"wifi_connection connected");
                    auto_fill_frame.setVisibility(View.VISIBLE);
                    yourMusicAdapter.connection= true;
                    yourMusicAdapter.notifyDataSetChanged();
                }else {
                    auto_fill_frame.setVisibility(View.INVISIBLE);
                    yourMusicAdapter.connection= false;
                    yourMusicAdapter.notifyDataSetChanged();
                    Log.e(TAG, "wifi_connection not connected");
                }
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.serchbar_btn :
                serchbar_btn.setVisibility(View.GONE);
                searchbar_focus.setVisibility(View.VISIBLE);
                //inputSearch.setFocusable(true);
                inputSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputSearch,1);
                loadingMore = true;
                loding_layout.setVisibility(View.GONE);
                auto_fill_layout.setVisibility(View.GONE);
                text_scroll_to_top.setVisibility(View.GONE);
                //imm.showSoftInput(inputSearch, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.img_view_autofill :
                String plus = "selected";
                if(!globalClass.syncing_status){
                    if(globalClass.battery_info.getStatus() == 1){
                        yourMusicAdapter.autofill(plus);
                        autofill_plus.setVisibility(View.INVISIBLE);
                        autofill_tick.setVisibility(View.VISIBLE);
                    }else{
                        fillerPlayList("We recommend that you plug your Mighty into a charger during Fill-er-up! sync. The sync process can take up to an hour to complete.","Proceed?");
                    }
                }else alertSDisplay(context.getString(R.string.playlist_selectwhile_sync_meaage),context.getString(R.string.auto_fill_sync_tittle));
                break;
            case R.id.img_blue_tick_autofill :
                String tick = "unselected";
                yourMusicAdapter.autofill(tick);
                autofill_plus.setVisibility(View.VISIBLE);
                autofill_tick.setVisibility(View.INVISIBLE);
                break;
            case R.id.button_sync :
                Log.e(TAG,"click on Btn Sync");
                sync_music_frag(1);
                break;
            case R.id.header_button_sync :
                sync_music_frag(1);
                listView.setSelection(0);
                break;
            case R.id.image_back_btn :
                beckButtonChangeSearch();
                break;
            case R.id.text_scroll_to_top :
                listView.setSelection(0);
                break;
        }
    }

    private void defineBroadCast(){
        intentFilter_changebitrate=new IntentFilter("bitrate.change.unselectplaylist");
        receiver_changebitrate=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tick = "unselected";
                yourMusicAdapter.autofill(tick);
                autofill_plus.setVisibility(View.VISIBLE);
                autofill_tick.setVisibility(View.INVISIBLE);
                yourMusicAdapter.notifyDataSetChanged();
                Log.e(TAG,"Unselect Playlist Broadcast");
            }
        };
    }

    public void beckButtonChangeSearch(){
        hideSoftKeboard();
        serchbar_btn.setVisibility(View.VISIBLE);
        searchbar_focus.setVisibility(View.GONE);
        inputSearch.setText("");
        onlyKeyboardHide();
    }
    public void onlyKeyboardHide(){
        loadingMore = false;
        Log.e(TAG,"count_offset value "+globalClass.count_offset+" "+globalClass.plalistTotal);
        if(globalClass.count_offset == globalClass.plalistTotal){
            loding_layout.setVisibility(View.GONE);
            auto_fill_layout.setVisibility(View.VISIBLE);
            text_scroll_to_top.setVisibility(View.VISIBLE);
        }else{
            loding_layout.setVisibility(View.VISIBLE);
            auto_fill_layout.setVisibility(View.GONE);
            text_scroll_to_top.setVisibility(View.GONE);
        }
    }



    public void hideSoftKeboard(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void sync_music_frag(int flag) {
        Log.e(TAG, "flag value " + flag);
        if (GlobalClass.mighty_ble_device != null) {
            browesFragmentSyning.browesSyncingTrigger(1);
            btn_sync.setText("Syncing");
            header_button_sync.setText("Syncing");
            btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
            header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
            btn_sync.setEnabled(false);
            header_button_sync.setEnabled(false);
            globalClass.syncing_status = true;
            progress_wait.setVisibility(View.VISIBLE);
            text_download_percentage.setVisibility(View.INVISIBLE);
            image_storage_diplay.setVisibility(View.VISIBLE);
            download_progress.setVisibility(View.INVISIBLE);
            download_progress.setProgress(0);
            pink_memory_level = 0.0f;
            circleViewPink.level = 0.0f;
            circleViewPink.setPath();
            circleViewPink.setVisibility(View.INVISIBLE);
            circleViewBlue.setVisibility(View.INVISIBLE);
            Log.e(TAG, "Check Focus " + viewFlipper.getDisplayedChild());
            if (viewFlipper.getDisplayedChild() == 1)
                AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
            if (flag != 2) {
                if (!downloading_mighty_playlist.isEmpty())
                    downloading_mighty_playlist.clear();
                for (Playlist value : yourMusicAdapter.selectedlist.values()) {
                    Log.e(TAG, "hashmap values" + value.getName());
                    downloading_mighty_playlist.add(value);
                    globalClass.spotify_playlist_obj_arraylist_selected.add(value);
                    yourMusicAdapter.selectedlist.get(value.getUri()).setOffline(Constants.DL_NONE);
                }
                send_download_request();
            }
            yourMusicAdapter.notifyDataSetChanged();
        }
    }

    private void spotifyLoginLogout(){
        intentFilter = new IntentFilter("spotify.login.logout");
        login_logout_broadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                login_status = spotify.getExtras().getString("login_status");
                Log.e(TAG,"Spotify Login Has Done "+login_status);
                if(login_status.equals("Login")){
                    Log.e(TAG,"Log In from spotify");
                    if(GlobalClass.mighty_ble_device == null)
                        auto_fill_frame.setVisibility(View.INVISIBLE);
                    else auto_fill_frame.setVisibility(View.VISIBLE);
                    if(!globalClass.arrayPlayList.isEmpty()){
                        auto_fill_layout.setVisibility(View.VISIBLE);
                        text_scroll_to_top.setVisibility(View.VISIBLE);
                    }
                    yourMusicAdapter.clearAdapter();
                }else{
                    Log.e(TAG,"Log Out from spotify");
                    serchbar_btn.setVisibility(View.VISIBLE);
                    searchbar_focus.setVisibility(View.GONE);
                    inputSearch.setText("");
                    search_layout.setVisibility(View.GONE);
                    auto_fill_layout.setVisibility(View.GONE);
                    loding_layout.setVisibility(View.GONE);
                    text_scroll_to_top.setVisibility(View.GONE);
                    yourMusicAdapter.clearAdapter();
                    globalClass.arrayPlayList.clear();
                    arrayPlaylist.clear();
                    globalClass.count_offset=0;
                    globalClass.count=0;
                    globalClass.plalistTotal =0;
                    empty_layout.setVisibility(View.VISIBLE);
                    arraow_image.setVisibility(View.VISIBLE);
                    text_message.setText("You'll need to go to the \n 'CONNECTIONS' tab and connect to \n your Spotify Premium account before \n you can browse your playlists.");
                    if(!yourMusicAdapter.selectedlist.isEmpty()){
                        yourMusicAdapter.selectedlist.clear();
                        playListSizeInfo(0.0f);
                        yourMusicAdapter.Track_size =0.0f;
                        yourMusicAdapter.consumedMemory =0.0f;
                    }


                }
            }
        };
    }

    private Runnable loadMoreListItems = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG,"check Main Thread "+globalClass.isFromMainThread());
            arrayPlaylist = new ArrayList<Playlist>();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            //Get 200 new list items
            Log.e(TAG,"Song Done before");
            globalClass.retrivePlayListFromSpotify(globalClass.count_offset,50,0);
            Log.e(TAG,"Song Done after");
            //Done! now continue on the UI thread
        }
    };



    public void retrivePlayList(int refreshFlag){
        Log.e(TAG,"refreshFlag "+refreshFlag+" "+pullTo_Refresh);
        if(refreshFlag == 1 | pullTo_Refresh){
            if(!arrayPlaylist.isEmpty()){
                arrayPlaylist.clear();
                yourMusicAdapter.clearAdapter();
            }
            Log.e(TAG,"refreshFlag in sidec"+refreshFlag+" "+pullTo_Refresh);
            pullTo_Refresh = false;
        }
        if(globalClass.mighty_playlist != null) {
            if (globalClass.mighty_playlist.size() != 0 && globalClass.arrayPlayList.size() != 0) {
                for (Map.Entry<String, Playlist> entry : globalClass.mighty_playlist.entrySet()) {
                    if (globalClass.arrayPlayList.get(entry.getKey()) != null) {
                        globalClass.arrayPlayList.get(entry.getKey()).setOffline(globalClass.mighty_playlist.get(entry.getKey()).getOffline());
                        Log.e(TAG, "get OffsetValue " + globalClass.arrayPlayList.get(entry.getKey()).getOffline());
                    }
                }
            }
        }
        int list_size = globalClass.count_offset - globalClass.count;
       // Log.e(TAG,"count list outer condition "+list_size+" "+globalClass.arrayPlayList.size()+"offset "+globalClass.count_offset);
    if(globalClass.count_offset == 50 && !globalClass.arrayPlayList.isEmpty()){
    arrayPlaylist.addAll(globalClass.arrayPlayList.values());
    }else if(globalClass.arrayPlayList != null && !globalClass.arrayPlayList.isEmpty()){
        Log.e(TAG,"calling else if ");
            for(int i = list_size;i< globalClass.arrayPlayList.size();i++){
                arrayPlaylist.add(new ArrayList<Playlist>(globalClass.arrayPlayList.values()).get(i));
                //Log.e(TAG,"in side if "+arrayPlaylist.get(i).getOffline());
            }
    }
    if(!globalClass.arrayPlayList.isEmpty()){
        empty_layout.setVisibility(View.GONE);
        search_layout.setVisibility(View.VISIBLE);
    }
        Log.e(TAG,"count  list"+list_size+" "+arrayPlaylist.size());
        //arrayPlaylist.addAll(globalClass.arrayPlayList.values());
        yourMusicAdapter.addPlayList(arrayPlaylist);
        yourMusicAdapter.notifyDataSetChanged();
        loadingMore = false;
        globalClass.count = 0;
        if(globalClass.count_offset == globalClass.plalistTotal){
                loding_layout.setVisibility(View.GONE);
            if(globalClass.count_offset != 0 && globalClass.plalistTotal != 0){
                auto_fill_layout.setVisibility(View.VISIBLE);
                text_scroll_to_top.setVisibility(View.VISIBLE);

            }
        }else {
                loding_layout.setVisibility(View.VISIBLE);
                auto_fill_layout.setVisibility(View.GONE);
            text_scroll_to_top.setVisibility(View.GONE);
        }
    }



    public void afterDeletePlayListMighty(){
        if(!globalClass.arrayPlayList.isEmpty()) {
            if (!globalClass.delete_playlist.isEmpty()) {
                for(int i=0;i<globalClass.delete_playlist.size();i++)
                globalClass.arrayPlayList.get(globalClass.delete_playlist.get(i).getUri()).setOffline(0);
            }
        }
        if (!arrayPlaylist.isEmpty())
            arrayPlaylist.clear();
        arrayPlaylist.addAll(globalClass.arrayPlayList.values());
        yourMusicAdapter.clearAdapter();
        yourMusicAdapter.addPlayList(arrayPlaylist);
    }


    public void bleDisconnected(){
        Log.e(TAG, "Disconnected_browse");
        btnEnable();
        image_battery_display_charging.setVisibility(View.INVISIBLE);
        playListSizeInfo(0.0f);
        pink_memory_level = 0.0f;
        rect_prog.level = 0.0f;
        rect_prog.setPaths();
        memory_level = 0.0f;
        circleViewBlue.level= 0.0f;
        circleViewBlue.setPath();
        circleViewPink.level = 0.0f;
        circleViewPink.setPath();
        auto_fill_frame.setVisibility(View.INVISIBLE);

        if (globalClass.mighty_playlist.size() == 0) {
            Log.e(TAG, "Disconnected Status ");
            Set<Map.Entry<String, Playlist>> entrySet = globalClass.arrayPlayList.entrySet();
            for(Map.Entry<String, Playlist> map_entry : entrySet ){
                map_entry.getValue().setOffline(0);
                Log.e(TAG,"my value "+map_entry.getKey()+" "+map_entry.getValue());
            }
            yourMusicAdapter.connection= false;
            yourMusicAdapter.notifyDataSetChanged();
        }
        Log.e(TAG, "ACTION_GATT_DISCONNECTED " + globalClass.mighty_playlist.size());
        storage.setText("0%");
        playback_hrs.setTextColor(defaultColors);
        playback_hrs.setTypeface(custom_bold_font);
        playback_hrs.setText("0%");
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
        rect_prog.setVisibility(View.INVISIBLE);
        autofill_plus.setVisibility(View.VISIBLE);
        autofill_tick.setVisibility(View.INVISIBLE);

            playListSizeInfo(0.0f);
            yourMusicAdapter.Track_size =0.0f;
            yourMusicAdapter.consumedMemory =0.0f;

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
        System.out.println("pink_level :"+pink_level);
        circleViewPink.setLevel(pink_level);
        circleViewPink.top=circleViewPink.bottom-pink_level;
        circleViewPink.setPath();
        memory_level = 0.0f;
        pink_memory_level = 0.0f;
        pink_level = 0.0f;
        circleViewPink.setVisibility(View.VISIBLE);
        circleViewBlue.setVisibility(View.VISIBLE);
        rect_prog.setVisibility(View.VISIBLE);
    }

    public void onReceiveMessage(int msgId, int msgType){
        Log.d(TAG, "Received Msg ID in browse = " + msgId +" msgType "+ msgType);
        handleBrowseMessage(msgId, msgType);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }


    @Override
    public void alertSDisplay(String message,String tittle) {
        if (mMaterialDialog != null)
            return;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(tittle);
        message_txt.setText(message);
        mMaterialDialog = new MaterialDialog(context)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        mMaterialDialog = null;

                    }
                });
        mMaterialDialog.show();
    }

    @Override
    public void playListSizeInfo(float Track_size) {
        btn_sync.setEnabled(true);
        header_button_sync.setEnabled(true);
        Log.e(TAG,"global storage "+ globalClass.memory.getStorage_total());
        consumedMemory = globalClass.memory.getStorage_total() - globalClass.memory.getStorage_free();
       // float convert_perc = (int) (Track_size * 100/ globalClass.memory.getStorage_total());//globalClass.memory.getStorage_total());
        pink_memory_level = (int) (((consumedMemory + Track_size) / globalClass.memory.getStorage_total()) * 100);
        Log.e(TAG,"covert_perc "+ pink_memory_level);
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

        if(!yourMusicAdapter.selectedlist.isEmpty()) {
            btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_color));
            header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_color));
            btn_sync.setEnabled(true);
            header_button_sync.setEnabled(true);
        }else{
            btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
            header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
            btn_sync.setEnabled(false);
            header_button_sync.setEnabled(false);
        }

    }


    public void send_download_request() {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 7;
        Log.d(TAG, "Set download structure");
        tcpClient.SendData(mightyMessage);
    }



    public void handleBrowseMessage(int msgId, int msgType) {
        if (msgId == 5){
            if (msgType== 102){
                if(globalClass.battery_info.getStatus() == 1){
                    image_battery_display_charging.setVisibility(View.VISIBLE);
                }else{
                    image_battery_display_charging.setVisibility(View.INVISIBLE);
                }

            }
        }
        //Receive the Download is happening
        if (msgId == MSG_ID_DOWNLOAD_ID) {
            if (msgType == 102) {
                if (globalClass.download.getStatus() != 0) {
                    globalClass.download_status = true;
                    Log.e(TAG, "download ID Events");
                    btndisable();
                }
                if(globalClass.download.getStatus() == Constants.DL_COMPLETED  && ((int)globalClass.download.getProgress() == 100)){
                    btnEnable();
                }
                if (globalClass.download.getStatus() == Constants.DL_REFRESH_COMPLETED | globalClass.download.getStatus() == Constants.DL_REFRESH_COMPLETED_WITH_ERROR){
                    btnEnable();
                }

                if (globalClass.download.getStatus() == Constants.DL_COMPLETED | globalClass.download.getStatus() == Constants.DL_COMPLETED_WITH_ERROR) {
                    if (globalClass.mighty_playlist.size() != 0) {
                            if (globalClass.arrayPlayList.get(globalClass.download.getGetPlaylist_url()) != null) {
                                globalClass.arrayPlayList.get(globalClass.download.getGetPlaylist_url()).setOffline(globalClass.download.getStatus());
                                Log.e(TAG, "get OffsetValue " + globalClass.arrayPlayList.get(globalClass.download.getGetPlaylist_url()).getOffline());
                            }
                        if(!arrayPlaylist.isEmpty())
                            arrayPlaylist.clear();

                        arrayPlaylist.addAll(globalClass.arrayPlayList.values());
                        yourMusicAdapter.clearAdapter();
                        yourMusicAdapter.addPlayList(arrayPlaylist);
                        yourMusicAdapter.notifyDataSetChanged();
                    }
                    if(downloading_mighty_playlist.isEmpty())
                        btnEnable();
                }
                switch(globalClass.download.getStatus()){
                    case Constants.DL_FAILED:
                        btnEnable();
                        break;
                    case Constants.DL_WIFI_ERROR:
                        btnEnable();
                        break;

                    case Constants.DL_SPOTIFY_LOGIN_ERROR:
                        btnEnable();
                        break;

                    case Constants.DL_FAILED_STORAGE_ERROR:
                        btnEnable();
                        break;

                    case Constants.DL_SP_PREFETCH_TIMEOUT:
                        btnEnable();
                        break;

                    case Constants.DL_FAILED_BIT_RATE_ERROR:
                        btnEnable();
                        break;
                }
            }
            if(msgType == 201){
                if(!downloading_mighty_playlist.isEmpty()) {
                    for(int j=0;j< downloaded_mighty_playlist.size();j++) {
                        globalClass.arrayPlayList.get(downloaded_mighty_playlist.get(j).getUri()).setOffline(0);
                    }
                    yourMusicAdapter.notifyDataSetChanged();
                    btnEnable();
                }
            }

        }

        // For Bit rate changed
        if(msgId == Constants.MSG_BIT_RATE_MODE && msgType == 200)
            yourMusicAdapter.notifyDataSetChanged();

        //Recive the Play List Form Mighty Device
        if (msgId == Constants.MSG_ID_PLAYLIST_ID) {
            if(msgType == 202){
                Log.e(TAG,"received playlist in mightybrowse "+msgId);
                if (globalClass.mighty_playlist.size() != 0) {
                    if (!globalClass.arrayPlayList.isEmpty()) {

                        for ( Map.Entry<String, Playlist> entry : globalClass.mighty_playlist.entrySet()) {
                            if(globalClass.arrayPlayList.get(entry.getKey()) != null){
                                globalClass.arrayPlayList.get(entry.getKey()).setOffline(globalClass.mighty_playlist.get(entry.getKey()).getOffline());
                                Log.e(TAG,"getOffline "+ globalClass.arrayPlayList.get(entry.getKey()).getOffline());
                            }
                        }
                        if (!arrayPlaylist.isEmpty())
                            arrayPlaylist.clear();

                        arrayPlaylist.addAll(globalClass.arrayPlayList.values());
                        yourMusicAdapter.clearAdapter();
                        yourMusicAdapter.addPlayList(arrayPlaylist);
                        yourMusicAdapter.notifyDataSetChanged();

                    } else Log.e(TAG, "No Data Array Play List");
                } else yourMusicAdapter.notifyDataSetChanged();
            }
            else if(msgType == 200) {

                if (downloaded_mighty_playlist.isEmpty())
                    btnEnable();

                if (globalClass.mighty_playlist.isEmpty())
                    btnEnable();

                yourMusicAdapter.notifyDataSetChanged();
            }else if(msgType == 201){
/*                if(!downloading_mighty_playlist.isEmpty()) {
                    for(int j=0;j< downloaded_mighty_playlist.size();j++) {
                        globalClass.arrayPlayList.get(downloaded_mighty_playlist.get(j).getUri()).setOffline(0);
                    }
                    yourMusicAdapter.notifyDataSetChanged();
                   // btnEnable();
                    Log.e(TAG,"201_is_called ");
                }*/
                yourMusicAdapter.notifyDataSetChanged();
            }
        }

        if (msgId == MSG_ID_MEMORY_ID) {
            if (msgType == 202) {
                memory_info();

            }
        }
        if (msgId == MSG_ID_BATTERY_INFO_ID) {
            battery_info();
        }

    }

    private void memory_info(){
        storage.setText(globalClass.memory.getStorageFullPercent() + "% full");
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

    private void battery_info(){
        if(globalClass.battery_info.getStatus() == 1){
            image_battery_display_charging.setVisibility(View.VISIBLE);
        }else{
            image_battery_display_charging.setVisibility(View.INVISIBLE);
        }
        rect_prog.left = width / 4;
        rect_prog.right = 3 * width / 4;
        rect_prog.bottom = 5 * height / 6;
        //    float input_level = 70.95f;
        battery_level =  globalClass.battery_info.getAvailablePercentage();
        float level = battery_level * (rect_prog.bottom - height / 6) / 100;
        float value = 5 * height / 6;
        rect_prog.setValue(value);
        rect_prog.level = level;
        rect_prog.setPaths();
        rect_prog.setVisibility(View.VISIBLE);

        battery_level =  globalClass.battery_info.getAvailablePercentage();
        playback_hrs.setText(globalClass.battery_info.getAvailablePercentage() + "%");
        if (globalClass.battery_info.getAvailablePercentage() <= 10) {
            playback_hrs.setTextColor(Color.parseColor("#FFFB908C"));
        } else {
            playback_hrs.setTextColor(defaultColors);
            playback_hrs.setTypeface(custom_bold_font);
        }
    }

    private void btnEnable(){
        Log.e(TAG,"btnenable_calling");
        browesFragmentSyning.browesSyncingTrigger(0);
        progress_wait.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        image_storage_diplay.setVisibility(View.VISIBLE);
        download_progress.setVisibility(View.INVISIBLE);
        download_progress.setProgress(0);
        circleViewPink.setVisibility(View.VISIBLE);
        circleViewBlue.setVisibility(View.VISIBLE);
        globalClass.syncing_status = false;

        btn_sync.setText("SYNC");
        header_button_sync.setText("SYNC");
        btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        btn_sync.setEnabled(false);
        header_button_sync.setEnabled(false);

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
            //clearing Adapter
        if(!YourMusicAdapter.selectedlist.isEmpty())
            YourMusicAdapter.selectedlist.clear();

        if (!globalClass.spotify_playlist_obj_arraylist_selected.isEmpty())
            globalClass.spotify_playlist_obj_arraylist_selected.clear();

    }

    private void btndisable(){
        playListSizeInfo(0.0f);
        pink_memory_level = 0.0f;
        circleViewPink.setLevel(0);
        circleViewPink.level = 0.0f;
        circleViewPink.setPath();
        progress_wait.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.VISIBLE);
        image_storage_diplay.setVisibility(View.VISIBLE);
        download_progress.setVisibility(View.VISIBLE);
        circleViewPink.setVisibility(View.INVISIBLE);
        circleViewBlue.setVisibility(View.INVISIBLE);
        download_progress.setProgress((int) globalClass.download.getProgress());
        text_download_percentage.setText((int) globalClass.download.getProgress()+"%");
        btn_sync.setText("Syncing");
        header_button_sync.setText("Syncing");
        btn_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        header_button_sync.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        btn_sync.setEnabled(false);
        header_button_sync.setEnabled(false);
        globalClass.syncing_status =true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        globalClass.count_offset =0;
        globalClass.count = 0;
        context.unregisterReceiver(receiver_changebitrate);
        context.unregisterReceiver(receiver_wifi);
    }

    private void fillerPlayList(String tittle,String message){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(tittle);
        message_txt.setText(message);
        mMaterialDialog = new MaterialDialog(context)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        yourMusicAdapter.autofill("selected");
                        autofill_plus.setVisibility(View.INVISIBLE);
                        autofill_tick.setVisibility(View.VISIBLE);
                        mMaterialDialog.dismiss();

                    }
                });
        mMaterialDialog.show();
    }

    public void playListRetriveFail(String playlistStatus){
        loadingMore = false;
        if(pullTo_Refresh){
            globalClass.count_offset = count_offsetFail;
            globalClass.count = count_Fail;
            globalClass.plalistTotal = count_TotalFail;
            globalClass.arrayPlayList.putAll(temp_plalist);
            count_offsetFail = 0;
            count_Fail = 0;
            count_TotalFail = 0;
            temp_plalist.clear();
            pullTo_Refresh = false;
        }
        playListfailText(playlistStatus);
    }

    @Override
    public void onRefresh() {
        hideSoftKeboard();
        Log.e(TAG,"sync satatus "+globalClass.syncing_status+" "+globalClass.mighty_ble_device);
        if(!globalClass.syncing_status){
            isConnected = ConnectivityReceiver.isConnected();
            if(isConnected){
                if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)){
                    if(!loadingMore){
                        //loding_layout.setVisibility(View.INVISIBLE);
                        serchbar_btn.setVisibility(View.VISIBLE);
                        searchbar_focus.setVisibility(View.GONE);
                        inputSearch.setText("");
                        count_offsetFail = globalClass.count_offset;
                        count_Fail = globalClass.count;
                        count_TotalFail = globalClass.plalistTotal;
                        temp_plalist.putAll(globalClass.arrayPlayList);
                        globalClass.count_offset=0;
                        globalClass.count=0;
                        globalClass.plalistTotal =0;
                        globalClass.arrayPlayList.clear();
                        pullTo_Refresh = true;
                        loadingMore = true;
                        globalClass.retrivePlayListFromSpotify(0,50,0);
                        if(!yourMusicAdapter.selectedlist.isEmpty()){
                            Intent spotify_login_tick = new Intent();
                            spotify_login_tick.setAction("bitrate.change.unselectplaylist");
                            context.sendBroadcast(spotify_login_tick);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeLayout.setRefreshing(false);
                            }
                        }, 4000);
                    }else swipeLayout.setRefreshing(false);
                }else{
                    swipeLayout.setRefreshing(false);
                    globalClass.toastDisplay("Please login to your Spotify account");
                }
            } else {
                swipeLayout.setRefreshing(false);
                globalClass.toastDisplay(getString(R.string.check_internet));
            }
        }else{
            swipeLayout.setRefreshing(false);
            alertSDisplay(context.getString(R.string.playlist_selectwhile_sync_meaage),context.getString(R.string.pullto_reresh_doring_sync));
        }
    }

    private void playListfailText(String status){
        isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
            if(globalClass.arrayPlayList.isEmpty()){
                empty_layout.setVisibility(View.VISIBLE);
                arraow_image.setVisibility(View.INVISIBLE);
                if(status.equals("ZEROPLAYLIST")){
                    text_message.setText("There are no playlists in your Spotify account. \n Please add some playlists in your \n Spotify account and do a pull to refresh.");
                }else{
                    text_message.setText("Slow internet connectivity \n Wait for your playlist to load or do a \n Spotify logout and login again.");
                }
            }
        }else{
            empty_layout.setVisibility(View.VISIBLE);
            arraow_image.setVisibility(View.INVISIBLE);
            text_message.setText("Please check your data connectivity \n and do a pull to refresh");
        }
    }

}
