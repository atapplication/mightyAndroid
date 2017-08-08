package mightyaudio.core;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.Model.App_Info;
import mightyaudio.Model.BT_Configuration;
import mightyaudio.Model.BT_Scan_List;
import mightyaudio.Model.Battery_Info;
import mightyaudio.Model.Bit_Rate;
import mightyaudio.Model.Debug_feature;
import mightyaudio.Model.Device_Info;
import mightyaudio.Model.Download;
import mightyaudio.Model.Events;
import mightyaudio.Model.FirmWare_Download;
import mightyaudio.Model.FirmWare_Upgrade;
import mightyaudio.Model.Headset_History;
import mightyaudio.Model.Memory;
import mightyaudio.Model.MightyLogin;
import mightyaudio.Model.Playlist;
import mightyaudio.Model.SpotifyLogin;
import mightyaudio.Model.WiFi_Configuration;
import mightyaudio.Model.Wifi_Status;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mightyaudio.activity.AllDoneActivty;
import mightyaudio.activity.LaunchTabActivity;
import mightyaudio.activity.MightyLoginActivity;
import mightyaudio.activity.MightySoftwareUpdateActivity;
import mightyaudio.activity.SetupConnectActivity;
import mightyaudio.ble.BluetoothLeService;
import mightyaudio.receiver.ConnectivityReceiver;
import mightyaudio.receiver.SpotifyAlarmReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;

import static mightyaudio.activity.MightyCreateAccountActivity.getDeviceName;


public class GlobalClass extends Application
{
    //public static final String spotifyUrl = "http://192.168.1.100:8089/test1/";
    //public static final String baseUrl = "http://192.168.1.100:8089/test1/rest/";

    public static final String spotifyUrl = "https://mighty2.cloudaccess.host/test1/";
    public static final String baseUrl = "https://mighty2.cloudaccess.host/test1/rest/";
    public static final String consumer = "consumer/";
    public static final String TAG = GlobalClass.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static GlobalClass instance;
    private SessionManager session;
    private SharedPreferences pref;
    private Dialog dialog;
    private String accesstoken,basetoken;
    private long timeNow;
    public String reqLatestVersion;
    private String username,user_pass,userIndicator;
    private Timer timer ;
    public boolean diffrent_user_block;
    public boolean lower_version;
    private  TimerTask  timerTask;
    public boolean action_send = false;
    AlertDialog alert;
    /*-----------Define for retriving spotify plalist----------------*/
    private SpotifyApi spotifyApi= new SpotifyApi();
    private Map<String, Object>  options = new HashMap<>();
    private SpotifyService spotifyService;
    public boolean download_error = false;
    public Map<String,Playlist> arrayPlayList = new LinkedHashMap<String,Playlist>();
    /*-----------Define for retriving spotify plalist----------------*/
    final Handler handler = new Handler();

    public static TCPClient tcpClient ; // = new TCPClient() ;

    //For Close login Activity
    private Activity activity;

    //public static BluetoothLeService mBluetoothLeService_global;

    public static String device_name;

    public Device_Info device_info = new Device_Info();

    /******************* BLE DEVICES ***********************************/
    public static BluetoothDevice mighty_ble_device = null;
    public static String ble_device_name;
    public static String ble_device_address = null;

    /***********************  Battery_Info ********************************/

    public static Battery_Info battery_info = new Battery_Info();

    /*******************************************************************/

    public static Wifi_Status wifi_status_global = new Wifi_Status();
    public  Wifi_Status wifi_connected_global = new Wifi_Status();
    public static ArrayList<Wifi_Status> wifi_lists_global = new ArrayList<Wifi_Status>();
    public static FirmWare_Upgrade global_firm_ware = new FirmWare_Upgrade();
    public static FirmWare_Download global_firm_download = new FirmWare_Download();

    /************************** EVENT **********************************/

    public static Events events_global = new Events();

    /************************** WiFi_Configuration **********************************/

    public static WiFi_Configuration wiFi_configuration_global = new WiFi_Configuration();

    /****************** BT_SCAN ***********************/

    public static String[] BT_list = null;
    public static HashMap<String,Headset_History> hasmap_bt_scan_headset_lists=new HashMap<String,Headset_History>();
    public static BT_Configuration sel_bt_headphone_model = new BT_Configuration();
    public static BT_Scan_List scan_sel_bt_headphone_model = new BT_Scan_List();

    /****************************************/

    /******************** Memory ********************/
    public Memory memory = new Memory();
    /*************************************************/

    /****************** Ticks in Connection Module *********************/

    public static boolean ble_status;
    public static boolean wifi_status;
    public static boolean spotify_status;
    public static boolean setup_status;
    public static boolean mighty_logout_status = false;

    /*******************************************************************/

    /******************  Bluetooth connection status *********************/

    public boolean mScanning = true;
    public boolean Status = false;

    /*******************************************************************/

    /******************  Bit_Rate_Mode_status *********************/

    public Bit_Rate global_bit_rate = new Bit_Rate();
    public static int global_set_bitrate;

    /*********************** SpotifyLogin ***************************/

    public static SpotifyLogin spotifyLogin = new SpotifyLogin();
    public static String user_id = " ";
    public static boolean spotify_frag_status = false;

    /**************** Playlist ******************/


    public static LinkedHashSet<String> Play_list_name = new  LinkedHashSet<String>(); // 25/7/16  public static ArrayList<String> Play_list_name  = new ArrayList<String>();
    public static ArrayList<String> Play_list_uri = new ArrayList<String>();   // 25/7/16   ArrayList<String> Play_list_uri = new ArrayList<String>();   LinkedHashSet<String> Play_list_uri = new  LinkedHashSet<String>();



    public static ArrayList test_playlist = new ArrayList();

    public Map<String,Playlist> mighty_playlist = new LinkedHashMap<String,Playlist>();
    public static ArrayList<Playlist> update_playlist_array  = new ArrayList<Playlist>();
    public static ArrayList<Playlist> spotify_playlist_obj_arraylist_selected = new ArrayList<Playlist>();
    public static ArrayList<Playlist> delete_playlist = new ArrayList<Playlist>();

    /**********************************************/

    /************************** Download list ******************/
    public static Download download = new Download();
    public boolean update_playlist = false;
    public boolean download_status = false;
    public boolean syncing_status = false;
    public boolean setgetplaylist = false;
    public boolean autoupgradeactivity = false;


    /*************************Mighty_Login*****************/
    public static MightyLogin mightyLogin = new MightyLogin();

    /***********************APP_Information**************/
    public static App_Info global_app_Info = new App_Info();


    // Bottom toolbar status for displaying visibility of toolbar in all fragments
    public static int userActivityMode = 0;
    public static int tool_bar_status = 0 ;

    public static void setTool_bar_status(int tool_bar_status) {
        GlobalClass.tool_bar_status = tool_bar_status;
    }

    public static int connect_upper_tool_bar_status = 0 ;
    public static int lower_tool_bar_status = 0 ;

   public Debug_feature debug_data= new Debug_feature();

    private static Context context;
    public static int count= 0;
    public static int count_offset=0;
    public int plalistTotal;
    //For Auto Connected Save mighty info
    public SharedPreferences mightyinfopref;
    public SharedPreferences.Editor mighty_info_editor;
    public static final String MIGHTY_INFO_PREF = "MightyInfoPref";

    public static final String USING_PREF = "MightyUsingPref";
    public static final String FLOW_PREF = "MightyFlowPref";
    public static final String FLOW_WAY = "FlowWAY";

    //Spotify Page
    public SpotifySessionManager spotifySessionManager;
    public SharedPreferences spotifyPref;
    private Long spotify_id;
    private String refreshToken,accessToken,expire_time,spotify_username,account_type;
    private  JSONObject token_obj;
    String message;
    int color;
    public BluetoothAdapter mBluetoothAdapter;
    //  BluetoothManager bluetoothManager ;
    UUID[] uuids = {UUID.fromString("0000a200-0000-1000-8000-00805f9b34fb")};
    public static final String AUTO_CONNECT = "autoConnect";
    public int auto_connect_force_cancel = 1;
    public Map<String,BluetoothDevice> scan_ble_list = new LinkedHashMap<String,BluetoothDevice>();
    private Typeface custom_font,custom_bold;
    public SharedPreferences flowpref;
    public SharedPreferences.Editor flow_editor;

    //Spotify New Intrgration
    public final String CLIENT_ID =  "8625198a5ee54c7aa603cd4714d3e639";
    public final String Client_Secret = "b69ccf0c9f2147d69b60de20446f8385";
    public final String REDIRECT_URI = "mighty.login.callback://";
    public static final int REQUEST_CODE = 6752;
    public static final String LOCAL_STATE = "someID=asdasdas&someApp=myApp";
    public static final String REQUESTED_SCOPES = "playlist-read-private playlist-read user-read-private playlist-read-collaborative user-follow-read user-library-read user-top-read streaming";
    public static final String AUTHORIZATION_CODE = "code";
    public static final String[] REQUESTED_SCOPES_ARRAY = REQUESTED_SCOPES.split(" ");
    public boolean process_GoingOn= false;  // Somthing is goning in ConnectActivity it's will not trigger
    public boolean upgradeProcess = false;
    private ImageLoader mImageLoader;
    private LruBitmapCache mLruBitmapCache;
    //public BluetoothComunication bluetoothComunication;
    public boolean autoConnectedTrigger;
    private boolean isConnected;

    /*-------------------------------------For Software Update--------------------------------------------------*/
    private JSONObject cloud_jsonObject;
    private Dialog mighty_update_dialog;
    private Activity myactivity;
    AlertDialog alert1;
    public HashMap<Integer,Integer> progressTimeOut = new HashMap<Integer,Integer>();
    public int time_count=0;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public BluetoothLeService mBluetoothLeService_global;
    private PackageInfo pinfo;
    private MaterialDialog  mMaterialDialog,swithMeterial,alrtMaterialDailog,anotherMaterial,bleDisconnectMaterial;
    private View promptView,switchpromtView,alertpopup,anotherpopup,bleDisconnectpopup;
    private TextView header_txt,switchheader_text,alert_header_txt,anotherheader_text,bleDisconnect_header_text;
    private TextView message_txt,switchmessage_text,alert_message,bleDisconnect_message;
    public SharedPreferences.Editor lastlogin_editor;
    public SharedPreferences lastloginpref;
    public String last_login_pref= "USERNAME_PASSWORD";
    public boolean softwareupdateTrriger;
    public String playlist_status = "nosatus";
    private WebSservices singleton=WebSservices.getInstance();



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService_global = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService_global.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService_global = null;
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e(TAG,"onCreate");
        context = getApplicationContext();
        instance=this;
        lastloginpref = getSharedPreferences(last_login_pref, Context.MODE_PRIVATE);
        lastlogin_editor = lastloginpref.edit();

        spotifySessionManager = new SpotifySessionManager(context);
        spotifyPref = context.getSharedPreferences(spotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);

        flowpref = context.getSharedPreferences(FLOW_PREF, Context.MODE_PRIVATE);
        flow_editor = flowpref.edit();

        session = new SessionManager(context);
        pref = context.getSharedPreferences(session.PREF_NAME , Context.MODE_PRIVATE);

        //Define Pakege For getting app info
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        printHashKey();
        if(pref.getBoolean(SessionManager.IS_LOGIN,false)){
            Log.e(TAG,"mighty login store");
            storeMightyAppInf();
            if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN, false))
                storeSpotifyInfo();

            //if Spotify is not Login

        }

        //Checking Blutooth is There are not
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        //mBluetoothAdapter = bluetoothManager.getAdapter();

        mightyinfopref = context.getSharedPreferences(MIGHTY_INFO_PREF , Context.MODE_PRIVATE);
        mighty_info_editor = mightyinfopref.edit();

        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        Log.e(TAG,"userActivityMode "+userActivityMode);
        if( userActivityMode != 1){
            //scanLeDevice(true);
        }
        //App_information
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String DeviceOSVersion = Build.VERSION.RELEASE;
            String DeviceType = getDeviceName();
            Field[] fields = Build.VERSION_CODES.class.getFields();
            String OsName = fields[Build.VERSION.SDK_INT + 1].getName();
            //Rtriveing ISO no
            TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            String countryCode_default= "null";
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    Log.e(TAG,"Sim Locked");
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    countryCode_default = telMgr.getNetworkCountryIso();
                    Log.e(TAG,"Sim State Ready "+countryCode_default.toUpperCase());
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    Log.e(TAG,"Sim State Unknown");
                    // do something
                    break;
            }
            if(countryCode_default.equals("null"))
            {
                countryCode_default = Locale.getDefault().getCountry();
                Log.e(TAG,"Sim No "+countryCode_default.toUpperCase());
            }
            global_app_Info.setOS(OsName);
            global_app_Info.setOS_Version(DeviceOSVersion);
            global_app_Info.setSerial_Number(Build.SERIAL);
            global_app_Info.setIMEI(countryCode_default.toUpperCase());
            global_app_Info.setApp_Version(pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(Bluetooth_BroadcastReceiver, filter);
        if(pref.getBoolean(SessionManager.IS_LOGIN,false)){
            if (mBluetoothAdapter.isEnabled()) {
                //startTimer();
                //startservice();
                bleComunicationInitilize();

            }
        }
    }

    public void bleComunicationInitilize(){
        Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        getApplicationContext().startService(gattServiceIntent);
        getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.e(TAG,"Single tone class ");
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(getRequestQueue(), mLruBitmapCache);
        }
        return mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return mLruBitmapCache;
    }

    public static Context getContext()
    {
        return  context;
    }

    public static void setBT_list(String[] BT_list)
    {
        GlobalClass.BT_list = BT_list;
    }


    public static synchronized GlobalClass getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    //intilize timer for refreshing accesstoke and basetoken
    public void refreshAccesssToken(){

                Log.e(TAG,"Global class refreshAccesssToken");
                username = pref.getString(SessionManager.USER_NAME,"");
                user_pass = pref.getString(SessionManager.PASSWORD,"");
                userIndicator = pref.getString(SessionManager.USERINDICATOR,"");
                accesstoken =  pref.getString(SessionManager.ACCESSTOKENEXPDATE,"");
                basetoken = pref.getString(SessionManager.BASETOKENEXPDATE,"");

                timer = new Timer();
                timerTask = new TimerTask() {
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                timeNow = System.currentTimeMillis();
                                accesstoken =  pref.getString(SessionManager.ACCESSTOKENEXPDATE,"");
                                basetoken = pref.getString(SessionManager.BASETOKENEXPDATE,"");
                                if(pref.getBoolean(SessionManager.IS_LOGIN,false)){
                                    if(basetoken != "" && Long.parseLong(basetoken) > timeNow){
                                        isConnected = ConnectivityReceiver.isConnected();
                                        if(accesstoken != null && Long.parseLong(accesstoken) > timeNow){
                                        }else if(isConnected){
                                            callAccessTokenapi(baseUrl+consumer+"getRefreshToken");
                                        }
                                    }else{
                                        //here need to call base toke
                                        //Log.e(TAG,"coming for first else");
                                        JSONObject jsonObject =  new JSONObject();
                                        try {
                                            jsonObject.put("UserIndicator",userIndicator);
                                            jsonObject.put("UserName",username.trim());
                                            jsonObject.put("Password",user_pass.trim());
                                            refreshBaseTokenLogin(jsonObject,0,null);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    }
                };
                timer.schedule(timerTask,1,10000);
    }

    //Stop timer
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            if (timerTask != null){
                timerTask.cancel();
            }

        }
    }

    public void callAccessTokenapi(String accesstokenapi){
        Log.e(TAG,"accesstokenapi " +accesstokenapi);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, accesstokenapi, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"login response "+ response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            Log.e(TAG,"res "+res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-BASE-MIGHTY-TOKEN",pref.getString(SessionManager.BASETOKEN,""));
                    Log.e(TAG,"header "+header);
                    return header;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                        if(Integer.parseInt(responseString) == 200){
                            try {
                                JSONObject jsonObject =new JSONObject(new String(response.data,HttpHeaderParser.parseCharset(response.headers)));
                                Log.e(TAG,"access Info "+jsonObject.toString());
                                restoreLoginInfo(jsonObject);
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else Log.e(TAG, "Some things wrong");
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshBaseTokenLogin(JSONObject login_json_Obj, final int flag, Activity activity){
        try {
            this.activity=activity;
            userIndicator = login_json_Obj.getString("UserIndicator");
            username = login_json_Obj.getString("UserName");
            user_pass = login_json_Obj.getString("Password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"Coming Global Login");
        Log.e(TAG,"login json "+login_json_Obj);


        try {
            final String requestBody = login_json_Obj.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+consumer+"mightyAppLogin", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"login response "+ response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "My VOLLEY"+ error.toString());
                    NetworkResponse response = error.networkResponse;
                    if(flag == 1) {
                        dismissProgressBar();
                        if( error instanceof NetworkError) {
                            toastDisplay("Please check your network connection");
                        }else if(error instanceof ServerError && response != null){
                            try {
                                String res = new String(response.data,HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                if(flag == 1){
                                    toastDisplay(res);
                                }
                                Log.e(TAG,"res "+res);
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            }
                        }
                        else if( error instanceof ServerError && response == null) {
                            Log.e(TAG,"Server Error "+"ServerError");
                            toastDisplay("Server under maintenance \n Please try after some time.");
                        } else if( error instanceof AuthFailureError) {
                        } else if( error instanceof ParseError) {
                        } else if( error instanceof NoConnectionError) {
                        } else if( error instanceof TimeoutError) {
                            toastDisplay("Something went wrong \n Please try again in a few minutes");
                        }
                        Intent intent = new Intent(getApplicationContext(), MightyLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                        if(Integer.parseInt(responseString) == 200){
                            try {
                                String user_id = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("userId", user_id);
                                login(jsonObject,flag);
                            }catch(UnsupportedEncodingException e){
                                return Response.error(new ParseError(e));
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void login(JSONObject login_json_Obj, final int flag ){
        try {
            Log.e(TAG,"login data "+login_json_Obj+" "+flag);
            final String requestBody = login_json_Obj.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+consumer+"login", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"login response "+ response+" "+flag);
                    if(flag == 1){
                        bleComunicationInitilize();
                        scanLeDevice(true);
                        Log.e(TAG,"before_handler");
                        //For First Time Use the app
                        refreshAccesssToken();
                        dismissProgressBar();
                        toastDisplay(getString(R.string.login_message));
                        Log.e(TAG,"First Time condition true "+userActivityMode+" "+flowpref.getString(FLOW_WAY,"").equals("setup")+" "+flowpref.getString(FLOW_WAY,""));
                        if(flowpref.getString(FLOW_WAY,"").equals("setup")){
                            Intent intent = new Intent(getApplicationContext(), SetupConnectActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else if(flowpref.getString(FLOW_WAY,"").equals("nothanks") ){
                            lower_tool_bar_status = 1;
                            Intent intent = new Intent(getApplicationContext(), LaunchTabActivity.class);
                            intent.putExtra("onBackPressed","onBackPressed");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        //Mighty_login set request
                        storeMightyAppInf();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scanLeDevice(true);
                                Log.e(TAG,"In_handler");
                            }
                        },2000);

                        Log.e(TAG,"after_handler");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    if (flag == 1)
                        dismissProgressBar();

                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            if (flag == 1){
                                dismissProgressBar();
                                toastDisplay(res);

                            }
                            Log.e(TAG,"res "+res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        }
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                        if(Integer.parseInt(responseString) == 200){
                            try {
                                JSONObject jsonObject =new JSONObject(new String(response.data,HttpHeaderParser.parseCharset(response.headers)));
                                Log.e(TAG,"Login Info "+jsonObject.toString());
                                saveLoginInfo(jsonObject,flag);
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else Log.e(TAG, "Some things wrong");
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            //requestQueue.add(stringRequest);
            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLoginInfo(final JSONObject loginInfo,final int flag){
                if(flag != 1){
                    username = pref.getString(SessionManager.USER_NAME,"");
                    user_pass = pref.getString(SessionManager.PASSWORD,"");
                    userIndicator = pref.getString(SessionManager.USERINDICATOR,"");
                }
                try {
                    String  statusDesc = loginInfo.getString("statusDesc");
                    String  statusCode = loginInfo.getString("statusCode");
                    String  apiToken = loginInfo.getString("apiToken");
                    String  baseToken = loginInfo.getString("baseToken");
                    String  accessToken = loginInfo.getString("accessToken");
                    String  accessTokenExpDate = loginInfo.getString("accessTokenExpDate");
                    String  baseTokenExpDate = loginInfo.getString("baseTokenExpDate");
                    String  userStatus = loginInfo.getString("userStatus");
                    int  userId = loginInfo.getInt("userId");
                    String  mightyDiviceId = loginInfo.getString("deviceId");
                    session.createLoginSession(username, user_pass,userIndicator, userId,statusDesc,statusCode,apiToken,baseToken,accessToken,
                            accessTokenExpDate,baseTokenExpDate,userStatus,mightyDiviceId);
                    if(userIndicator != "F"){
                        lastlogin_editor.putString("LAST_USERNAME",username);
                        lastlogin_editor.putString("LAST_PASSWORD",user_pass);
                        lastlogin_editor.commit();
                    }else{
                        lastlogin_editor.clear();
                        lastlogin_editor.commit();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
    }

    private void restoreLoginInfo(JSONObject accessToke){
        Log.e(TAG,"refrsh accessToken "+accessToke);
        String user_name = pref.getString(SessionManager.USER_NAME,"");
        String user_password = pref.getString(SessionManager.PASSWORD,"");
        String userIndicator = pref.getString(SessionManager.USERINDICATOR,"");
        String  statusDesc = pref.getString(SessionManager.STATUSDESC,"");
        String  statusCode = pref.getString(SessionManager.STATUSCODE,"");
        String  apiToken = null;
        String accessTokenExpDate = null;
        String  baseToken = null;
        String  baseTokenExpDate = null;
        try {
            apiToken = accessToke.getString("apiToken");
            accessTokenExpDate = accessToke.getString("accessTokenExpDate");
            baseToken = accessToke.getString("baseToken");
            baseTokenExpDate = accessToke.getString("baseTokenExpDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String  accessToken = pref.getString(SessionManager.ACCESSTOKEN,"");
        String  userStatus = pref.getString(SessionManager.USERSTATUS,"");
        int  userId = pref.getInt(SessionManager.USERID,0);
        String  mightyDiviceId = pref.getString(SessionManager.MIGHTYFEVICE_ID,"");
        //if(session.isLoggedIn())
        //session.clearSession();

        session.createLoginSession(user_name, user_password,userIndicator, userId,statusDesc,statusCode,apiToken,baseToken,accessToken,
                accessTokenExpDate,baseTokenExpDate,userStatus,mightyDiviceId);

    }

    public void toastDisplay(String massage){
        Toast toast = Toast.makeText(getApplicationContext(),  massage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    //FaceBook Integation Getting HashKey
    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("unizen.mighty", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(TAG,"HASHKEY "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void retrivePlayListFromSpotify(final int offset, final int limit, final int flag){
        Log.e(TAG,"retrivePlayListFromSpotify");
        new Thread(new Runnable() {
            @Override
            public void run() {
                playListFromSpotify(offset,limit,flag);
            }
        }).start();
    }


    public void playListFromSpotify(final int offset, final int limit, final int flag){
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        spotifyApi.setAccessToken(spotifyPref.getString(SpotifySessionManager.ACCESSTOKEN,""));
        spotifyService= spotifyApi.getService();
        Log.e(TAG,"checking access Token "+spotifyPref.getString(SpotifySessionManager.ACCESSTOKEN,""));
        //
        spotifyService.getMyPlaylists(options,new SpotifyCallback<Pager<PlaylistSimple>>()
        {
            @Override
            public void failure(SpotifyError error)
            {
                dismissProgressBar();
                Log.e(TAG,"My Error "+error);
                Intent fail_playlist = new Intent();
                fail_playlist.setAction("spotify.playlist.retrievefail");
                fail_playlist.putExtra("flag_value","RETRIVEFAIL");
                getContext().sendBroadcast(fail_playlist);
                playlist_status = "RETRIVEFAIL";
                //toastDisplay("Something went wrong \n Please try again in a few minutes");
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, retrofit.client.Response response)
            {
                if(spotify_frag_status == true){
                    plalistTotal =playlistSimplePager.total;
                    Log.e(TAG,"playlistSimplePager.items  "+playlistSimplePager.items.size());
                    int i =0;
                    while(i <playlistSimplePager.items.size()){
                        Playlist playlist=new Playlist();
                        if(playlistSimplePager.items.get(i).images.size() != 0) {
                            Log.e(TAG, "Tarck image  " + playlistSimplePager.items.get(i).name + " " + playlistSimplePager.items.get(i).images.get(0).url + " " + playlistSimplePager.items.get(i).tracks.total);
                            playlist.setPlayListUrl(playlistSimplePager.items.get(i).images.get(0).url);
                        }

                        if(playlistSimplePager.items.get(i).owner.display_name != null && !playlistSimplePager.items.get(i).owner.id.equals(spotifyPref.getString(SpotifySessionManager.USER_NAME,"")))
                            playlist.setSpotifFollowup(playlistSimplePager.items.get(i).owner.display_name);
                        else if(!playlistSimplePager.items.get(i).owner.id.equals(spotifyPref.getString(SpotifySessionManager.USER_NAME,""))){
                            String folloup_name = Character.toUpperCase(playlistSimplePager.items.get(i).owner.id.charAt(0)) + playlistSimplePager.items.get(i).owner.id.substring(1);
                            playlist.setSpotifFollowup(folloup_name);
                            Log.e(TAG,"Tarck image  out side"+playlistSimplePager.items.get(i).owner.id+" "+folloup_name);
                        }
                        playlist.setName(playlistSimplePager.items.get(i ).name);
                        playlist.setUri(playlistSimplePager.items.get(i ).uri);
                        playlist.setSnapshotId(playlistSimplePager.items.get(i ).snapshot_id);
                        playlist.setTracks_count(String.valueOf(playlistSimplePager.items.get(i ).tracks.total));
                        playlist.setHash_key(playlistSimplePager.items.get(i).snapshot_id);
                        //arrayPlayList.add(playlist);
                        arrayPlayList.put(playlistSimplePager.items.get(i ).uri.toString().trim(),playlist);
                        i++;
                        count++;
                        count_offset++;
                    }
                    Log.e(TAG,"plalistTotal "+plalistTotal+" "+count+ " "+count_offset+" "+arrayPlayList.size()+" "+flag);
                    if (plalistTotal != 0 )
                    {
                        int my_limit = plalistTotal - count_offset;
                        if(count != 50 && plalistTotal != count_offset && flag == 0){
                            Log.e(TAG,"if part "+my_limit);
                            if(my_limit < 50)
                                retrivePlayListFromSpotify(count_offset, my_limit,0);
                            else retrivePlayListFromSpotify(count_offset, 50,0);
                        }else if(plalistTotal != count_offset && flag == 1){
                            Log.e(TAG,"else if condition ");
                            if(my_limit < 50)
                                retrivePlayListFromSpotify(count_offset, my_limit,flag);
                            else retrivePlayListFromSpotify(count_offset, 50,flag);
                        }else  {
                            Log.e(TAG,"else part it over" +arrayPlayList.size());
                            Intent browse_playlist = new Intent();
                            browse_playlist.putExtra("update",flag);
                            browse_playlist.setAction("com.spotify.playlist");
                            getContext().sendBroadcast(browse_playlist);
                        }
                    }else{
                        Intent fail_playlist = new Intent();
                        fail_playlist.setAction("spotify.playlist.retrievefail");
                        fail_playlist.putExtra("flag_value","ZEROPLAYLIST");
                        getContext().sendBroadcast(fail_playlist);
                        playlist_status = "ZEROPLAYLIST";
                    }
                    dismissProgressBar();
                }


            }
        });

    }

    //For Broadcast receiver only
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void showSnack(boolean isConnected,View view) {
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Please check your Internet Connection";
            color = Color.RED;
        }
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }




    public  void spotifyLoginUsingMightyCloud(JSONObject jsonObject, final Activity activity){
        try {
            Log.e(TAG,"json data "+jsonObject); //globalClass.spotifyUrl //getTokens
            final String requestBody = jsonObject.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, spotifyUrl+"spotifyaccess/spotifyToken",  new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"spotify login response "+ response);
                    if(Integer.parseInt(response) == 200) {
                        jsonParse(token_obj,activity);
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"Token VOLLEY"+error);
                    dismissProgressBar();
                    if( error instanceof NetworkError) {
                        toastDisplay("Please check your network connection");
                    }else if( error instanceof ServerError ) {
                        Log.e(TAG,"Server Error "+"ServerError");
                        toastDisplay("Server under maintenance \n Please try after some time.");
                    } else if( error instanceof AuthFailureError) {
                    } else if( error instanceof ParseError) {
                    } else if( error instanceof NoConnectionError) {
                    } else if( error instanceof TimeoutError) {
                        Log.e(TAG,"Time out Error");
                        toastDisplay("Poor network connectivity \n Please try again later.");
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        Log.e(TAG,"parseNetworkResponse "+responseString +" "+response.headers.containsKey("location"));
                        if(Integer.parseInt(responseString) == 200) try {
                            try {
                                token_obj = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (UnsupportedEncodingException e) {
                            return com.android.volley.Response.error(new ParseError(e));
                        }
                        else Log.e(TAG, "Some things wrong");
                    }
                    return com.android.volley.Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            Log.d(TAG,"Exception "+e.toString());
            e.printStackTrace();
        }
    }

    private void jsonParse(JSONObject jsonObject, final Activity activity){
        try {
            Log.e(TAG, "Spotify Info form cloud " + token_obj.toString());
            spotify_id = jsonObject.getLong("id");
            accessToken = jsonObject.getString("accessToken");
            expire_time = jsonObject.getString("expiresIn");
            refreshToken = jsonObject.getString("refreshToken");
            //expire_time = expire_time + System.currentTimeMillis();
            //SpotifyApi api = new SpotifyApi();
            spotifyApi.setAccessToken(accessToken);
            spotifyService = spotifyApi.getService();
            spotifyService.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, retrofit.client.Response response) {

                    Log.e(TAG,"user_id"+ userPrivate.id+" "+userPrivate.product);
                    spotify_username = userPrivate.id;
                    account_type = userPrivate.product;
                    if(userPrivate.product != null){
                        if(userPrivate.product.equals("premium")){
                            Log.e(TAG,"Account Type "+account_type+" "+userPrivate.id+" "+spotify_username);
                            spotifySessionManager.createSpotifySession(spotify_username,spotify_id,accessToken,refreshToken,expire_time,System.currentTimeMillis());
                            storeSpotifyInfo();
                            afterLoginSucc();
                            send_set_spotify();

                        }else{
                            dismissProgressBar();
                            premiumCheckDailog(getString(R.string.mighty_premium_tittle),getString(R.string.mighty_premium_message),activity);
                        }
                    }else {
                        dismissProgressBar();
                        hardwarecompatibility(activity,"Spotify connection error","Please try again in a few minutes");
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    dismissProgressBar();
                    Log.e(TAG,"Spotify problem while login");
                            hardwarecompatibility(activity,"Spotify connection error","Please try again in a few minutes");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void premiumCheckDailog(String myTitle,String myMsg,Activity activity){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(myTitle);
        message_txt.setText(myMsg);
        mMaterialDialog = new MaterialDialog(activity)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spotifySessionManager.clearSession();
                        mMaterialDialog.dismiss();

                    }
                });
        mMaterialDialog.show();
    }

    //Calling Once login Successful
    private void afterLoginSucc(){
        Log.e(TAG,"etrivePlayListFromSpotify afterLoginSucc");
        retrivePlayListFromSpotify(0,50,0);
        notifySpotifyFragment();
        notifyBrowesFragment("Login"); //Fire BroadCast to MusicBrowesFragment
        if(!arrayPlayList.isEmpty())
            arrayPlayList.clear();

        GlobalClass.spotify_status=true;
        GlobalClass.spotify_frag_status=true;
        spotify_tick();
        refreshTokeAlarm();
    }
    public void notifyBrowesFragment(String login_status){
        Intent spotify_login = new Intent();
        spotify_login.setAction("spotify.login.logout");
        spotify_login.putExtra("login_status",login_status);
        context.sendBroadcast(spotify_login);
    }

    private void notifySpotifyFragment(){
        Log.e(TAG,"userActivityMode "+userActivityMode);
        if(userActivityMode == 1 && mighty_ble_device == null){
            Log.e(TAG,"userActivityMode in side");
            Intent intent = new Intent(getApplicationContext(), AllDoneActivty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }/*else if( usingpref.getInt("using_satatus",0) == 1 && userActivityMode == 0  && mighty_ble_device == null){
            Intent intent = new Intent(getApplicationContext(), AllDoneActivty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
        Log.e(TAG,"calling Progressbar");
        dismissProgressBar();
        spotifyloginlogout("Login");

    }
    public void spotifyloginlogout(String login_status){
        Intent spotify_login = new Intent();
        spotify_login.setAction("spotify.login.successful");
        spotify_login.putExtra("login_status",login_status);
        context.sendBroadcast(spotify_login);
    }

    private void refreshTokeAlarm(){
        Log.e(TAG,"Start Refresh Spotify Token");
        Intent intent = new Intent(getApplicationContext(), SpotifyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), SpotifyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,3300000, pIntent);
    }

    public void stopRefreshToken() {
        Log.e(TAG,"Stop Refresh Spotify Token");
        Intent intent = new Intent(getApplicationContext(), SpotifyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), SpotifyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    public void send_set_spotify(){
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 14;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG," spotify_set_structure_done "+spotifyLogin.getUsername()+" "+spotifyLogin.getAuthentication_Token());
    }

    public void spotify_tick(){
        Intent spotify_login_tick = new Intent();
        spotify_login_tick.setAction("com.spotify.connect");
        spotify_login_tick.putExtra("value","connected");
        context.sendBroadcast(spotify_login_tick);
    }

    public void storeMightyAppInf(){
        mightyLogin.setUsername(pref.getString(SessionManager.USER_NAME,""));
        mightyLogin.setHashPassword(pref.getString(SessionManager.PASSWORD,""));
        mightyLogin.setLogin_mode(0);
    }

    public void storeSpotifyInfo(){
        Log.e(TAG,"Account Type user anme of spotify "+spotifyPref.getString(spotifySessionManager.USER_NAME,""));
        Log.e(TAG,"Account Type refresh of spotify "+spotifyPref.getString(SpotifySessionManager.ACCESSTOKEN,""));
        spotifyLogin.setAuthentication_Token(spotifyPref.getString(SpotifySessionManager.ACCESSTOKEN,""));
        spotifyLogin.setOfflineToken("dummy_values");
        spotifyLogin.setExperity(1);
        spotifyLogin.setFacebookLinked("dummy_values");
        spotifyLogin.setPrivateSession(1);
        spotifyLogin.setPublishActivity(1);
        spotifyLogin.setLogin_mode(0);
        spotifyLogin.setUsername(spotifyPref.getString(spotifySessionManager.USER_NAME,""));
    }

    public void bleConnect(final BluetoothDevice device){
        bleComunicationInitilize();
        Log.e(TAG,"Connect Devive"+device.getAddress()+" "+mBluetoothLeService_global);
        if(mBluetoothLeService_global != null) {
            time_count++;
            progressTimeOut.put(time_count,time_count);
            scanLeDevice(false);
            mBluetoothLeService_global.connect(device.getAddress().toString());
            Log.e(TAG,"time_count "+time_count);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    mighty_info_editor.clear();
                    mighty_info_editor.commit();
                    Gson gson = new Gson();
                    String json = gson.toJson(device);
                    mighty_info_editor.putString(AUTO_CONNECT, json);
                    mighty_info_editor.commit();
                }
            });
        }
    }

    public  void bleDisconnect() {
        Log.e(TAG,"Disconnect Triger");
        if (mBluetoothLeService_global != null){
            Log.e(TAG,"Disconnect Triger if side condition");
            mBluetoothLeService_global.disconnect(mBluetoothLeService_global.mBluetoothGatt);
        }
    }

    public static boolean isFromMainThread(){
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }



    public void scanLeDevice(final boolean enable)
    {
                if(mBluetoothAdapter.isEnabled()) {
                 BluetoothManager   bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
                 mBluetoothAdapter = bluetoothManager.getAdapter();
                 Log.e(TAG, "Triggered "+ mBluetoothAdapter+ " "+enable);
                  //  mScanning = enable;
                    if (enable) {
                        try {
                            mBluetoothAdapter.startLeScan(mLeScanCallback);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Scaning True Condition  ");
                    } else {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        Log.e(TAG, "Scaning false Condition  ");
                    }
                }
    }

    BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //if(getActivity() == null ) return;
           // if (mScanning == false) return;
            String perfectuuid;
            perfectuuid = String.valueOf(parseUUIDs(scanRecord));
            String serviceuuidlong = "dummy";
            String serviceuuid = "dummy_value";
            Log.e(TAG, "perfect_uuid" + "full_uuid " + perfectuuid + "name " + device.getName());
            if (perfectuuid.length() > 4 ) {
                serviceuuid = perfectuuid.substring(5, 9);
                if(perfectuuid.length() > 270)
                    serviceuuidlong = perfectuuid.substring(271, 275);
                if (serviceuuid.equals("a200") | serviceuuidlong.equals("a200")) {
                    Log.e(TAG," auto-connect "+ auto_connect_force_cancel+" "+device.getAddress() + " name "+device.getName());

                    if (mighty_ble_device == null) {
                        Gson gson = new Gson();
                        String mighty_info = mightyinfopref.getString(AUTO_CONNECT, "");
                        final BluetoothDevice device_obj = gson.fromJson(mighty_info, BluetoothDevice.class);
                        if (device_obj != null  && auto_connect_force_cancel == 1) {
                            Log.e(TAG, "Auto Connected Device " + device_obj.getName() + " " + mighty_info + " " + device_obj.getAddress().equals(device.getAddress()));
                            if (device_obj.getAddress().equals(device.getAddress())) {
                                Log.e(TAG,"Auto connected Trigger before "+autoConnectedTrigger);
                                if(!autoConnectedTrigger){
                                   // scanLeDevice(false);
                                    if(!scan_ble_list.isEmpty()){
                                        scan_ble_list.clear();
                                    }
                                    autoConnectedTrigger = true;
                                    bleConnect(device_obj);
                                    Status = true;
                                    scan_ble_list.put(device.getAddress(), device);
                                    Intent ble_list_req1 = new Intent();
                                    ble_list_req1.setAction("ble.scan.list");
                                    ble_list_req1.putExtra("blelist", device);
                                    context.sendBroadcast(ble_list_req1);
                                    Log.e(TAG,"Auto connected Trigger "+autoConnectedTrigger+" "+Status);
                                }
                            }
                        }
                        if(!autoConnectedTrigger){
                            scan_ble_list.put(device.getAddress(), device);
                            Intent ble_list_req = new Intent();
                            ble_list_req.setAction("ble.scan.list");
                            ble_list_req.putExtra("blelist", device);
                            context.sendBroadcast(ble_list_req);
                            Log.e(TAG, "On Receive hit " + device.getName() + " " + scan_ble_list.size());
                        }
                    }
                }
            }
        }
    };


    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,offset++, 16).order(ByteOrder.BIG_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit, mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            Log.e(TAG, e.toString());
                            //continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }

        return uuids;
    }

    //Switching User Mighty cloud and spotify switch user flag 1 mighty && 2 flag 2 spotify
    public void alertdialogmighty(Activity activity, final int flag) {
        Log.e(TAG,"flag "+flag+"");
        if (swithMeterial != null)
            return;

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        switchpromtView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        switchheader_text=(TextView)switchpromtView.findViewById(R.id.text_header);
        switchmessage_text=(TextView)switchpromtView.findViewById(R.id.text_message);
        switchheader_text.setText("Are you sure?");
        switchmessage_text.setText("All playlists will be removed from this Mighty if you proceed.");
        swithMeterial = new MaterialDialog(activity)
                .setView(switchpromtView)
                .setPositiveButton(getString(R.string.continue__button), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag == 1){
                            mightyLogin.setLogin_mode(1);
                            TCPClient tcpClient = new TCPClient();
                            MightyMessage mightyMessage = new MightyMessage();
                            mightyMessage.MessageType = Constants.MSG_TYPE_SET;
                            mightyMessage.MessageID = 15;
                            tcpClient.SendData(mightyMessage);
                            Log.e(TAG, " MightyLogin_set_structure_done ");
                        }else if(flag == 2){
                            spotify_status=true;
                            spotifyLogin.setLogin_mode(1);
                            ClearingPlaListBroadCast();
                            spotify_tick();
                            send_set_spotify();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    send_set_spotify();
                                }
                            },3000);
                        }
                        swithMeterial.dismiss();
                        swithMeterial= null;

                    }
                })
        .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1){
                    mighty_info_editor.clear();
                    mighty_info_editor.commit();
                    session.logoutUser();
                    bleDisconnect();
                }else if(flag == 2){
                    spotifySessionManager.clearSession();
                    send_set_spotifylogout();
                    notifyBrowesFragment("Logout");
                    spotifyloginlogout("Logout");
                    GlobalClass.spotify_frag_status=false;
                    spotify_status=false;
                    spotify_tick();
                }
                swithMeterial.dismiss();
                swithMeterial= null;
            }
        });
        swithMeterial.show();
    }


    public void ClearingPlaListBroadCast(){
        Intent spotify_loginswitch = new Intent();
        spotify_loginswitch.setAction("spotify.login.switch");
        context.sendBroadcast(spotify_loginswitch);
    }

    public void send_set_spotifylogout(){
        events_global.setBT_Status(255);
        events_global.setWiFi_status(events_global.getWiFi_status());
        events_global.setInternet_Connection(255);
        events_global.setSpotify_Status(0);
        events_global.setMighty_Cloud_Status(255);
        events_global.setOffline_Status(255);
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 16;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG," Event_set_structure_done ");
    }

    public void showProgressBar(Activity activity){
        Log.e(TAG,"Prgressbar clicked");
        if(dialog != null){
            if(dialog.isShowing())
                return;
        }

        dialog = new Dialog (activity);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();
    }
    public void dismissProgressBar(){
        if(dialog != null){
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }

    }

    public void mightyRegistration(final String from_where){
        final String  accesstoke_token = pref.getString(SessionManager.APITOKEN,"");
        int userid = pref.getInt(SessionManager.USERID,0);

        JSONObject jsonObject = new JSONObject();
        try {

            if(mighty_ble_device != null){

                jsonObject.put("UserID",String.valueOf(userid));
                jsonObject.put("HWSerialNumber",device_info.getHW_Serial_Number());
                jsonObject.put("DeviceName",device_info.getDevice_Name());
                jsonObject.put("DeviceType",device_info.getDevice_Type());
                jsonObject.put("SWVersion",device_info.getSW_Version());
                jsonObject.put("AppVersion",pinfo.versionName);
                jsonObject.put("AppBuild",pinfo.versionCode);
                Log.e(TAG,"json data "+jsonObject);
                Log.e(TAG,"Registration "+baseUrl+consumer+"mightyRegistration "+from_where);
            }
            final String requestBody = jsonObject.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+consumer+"mightyRegistration", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    upgradeProcess = true;
                    Log.e(TAG,"Registration response "+ response);
                    if(from_where.equals("LaunchTab"))
                    softwareUpdateChecked(null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Registration VOLLEY ", error.toString());
                        NetworkResponse response = error.networkResponse;
                        if( error instanceof NetworkError) {
                            toastDisplay("Please check your network connection");
                        }else if( error instanceof ServerError && response != null) {
                            Log.e(TAG,"Server Error "+"ServerError");
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                Log.e(TAG,"Status "+response.statusCode);
                                if(response.statusCode == 409){
                                    upgradeProcess = true;
                                    if(from_where.equals("LaunchTab"))
                                        softwareUpdateChecked(null);
                                }else if(response.statusCode == 500){
                                    Log.e(TAG,"Server Error "+response.statusCode);
                                    //toastDisplay("Something went wrong \n Please try again in a few minutes");
                                }
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            }
                        }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",accesstoke_token);
                    Log.e(TAG,"header "+header);
                    return header;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void softwareUpdateChecked(Activity activity){
        String  accesstoke_token = pref.getString(SessionManager.APITOKEN,"");
        JSONObject mightyjsonObject = new JSONObject();
        try {
            if(device_info != null){
                mightyjsonObject.put("HWSerialNumber",device_info.getHW_Serial_Number());
                mightyjsonObject.put("SWVersion",device_info.getSW_Version());//GlobalClass.device_info.SW_Version);
                mightyjsonObject.put("AppVersion",pinfo.versionName);
                mightyjsonObject.put("AppBuild",pinfo.versionCode+"");
                deviceFirmware(mightyjsonObject,accesstoke_token,activity);
            } else Log.e(TAG," No HWSerialNumber");

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private BroadcastReceiver Bluetooth_BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG,"From_Broad Cast STATE_OFF");
                        //if(bluetoothComunication!=null)
                        //  bluetoothComunication.stopSelf();
                        scanLeDevice(false);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        bleComunicationInitilize();
                        scanLeDevice(true);
                        //startTimer();
                        Log.e(TAG,"Service_started");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }
            }
        }
    };

    //For Software Update............................
    public void deviceFirmware(JSONObject jsonObject, final String accToken, final Activity activity){
        try {
            Log.e(TAG,"device json data "+jsonObject);
            //software_update.setEnabled(false);
            final String requestBody = jsonObject.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.baseUrl+"admin/deviceFirmware", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"device response "+ response);
                    softwareupdateTrriger = true;
                    if(activity != null)
                        dismissProgressBar();
                    deviceFirmwareMighty(cloud_jsonObject,activity);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    if(activity != null)
                        dismissProgressBar();
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            try {
                                JSONObject res = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8")));
                                Log.e(TAG,"Error field res "+res);
                                if(res.getString("statusCode") == "400" | res.getString("statusCode") == "405"){
                                    mightyRegistration("LaunchTab");
                                    toastDisplay(res.getString("statusDesc"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }
                    if(activity != null){
                        if( error instanceof NetworkError) {
                        }else if( error instanceof ServerError) {
                            Log.e(TAG,"Server Error "+"ServerError");
                            toastDisplay("Server under maintenance \n Please try after some time.");
                        } else if( error instanceof AuthFailureError) {
                        } else if( error instanceof ParseError) {
                        } else if( error instanceof NoConnectionError) {
                        } else if( error instanceof TimeoutError) {
                            toastDisplay("Something went wrong \n Please try again in a few minutes");
                        }
                    }
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",accToken);
                    Log.e(TAG,"header "+header);
                    return header;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                        if(Integer.parseInt(responseString) == 200){
                            try {
                                cloud_jsonObject =new JSONObject(new String(response.data,HttpHeaderParser.parseCharset(response.headers)));
                                Log.e(TAG,"deviceFirmware Info "+cloud_jsonObject.toString());
                                //passing json Data

                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else Log.e(TAG, "Some things wrong");
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            GlobalClass.getInstance().getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deviceFirmwareMighty(JSONObject deviceJson,Activity activity){
        try {
            String latestVersion = deviceJson.getString("latestVersion");
            String latestRequired = deviceJson.getString("latestRequired");
            String compatibleAND = deviceJson.getString("compatibleAND");
            String compatibleHW = deviceJson.getString("compatibleHW"); // requiredComtible latesthardware

            String upgraReqired = deviceJson.getString("requires");
            String fileDownloadUrl = deviceJson.getString("fileDownloadUrl");
            String fileSize = deviceJson.getString("fileSize");
            String hashValue = deviceJson.getString("hashValue");
            String hashType = deviceJson.getString("hashType");
            String ht = deviceJson.getString("ht");
            reqLatestVersion =deviceJson.getString("reqLatestVersion");
            String reqHashValue = deviceJson.getString("reqHashValue");
            String reqHT = deviceJson.getString("reqHT");

            if(mighty_ble_device != null){
                FirmWare_Upgrade firmWare_upgrade = new FirmWare_Upgrade();

                firmWare_upgrade.setAvailable_Status(1);
                firmWare_upgrade.setLast_Upgrade_Status(1);
                firmWare_upgrade.setUpgrade_Action(1);
                firmWare_upgrade.setCurrent_Version(device_info.getSW_Version());
                firmWare_upgrade.setNew_Version(reqLatestVersion);
                firmWare_upgrade.setLast_Update_Date("anupam");

                firmWare_upgrade.setURL(fileDownloadUrl);
                firmWare_upgrade.setHash_Value(reqHashValue);
                if(reqHT != "null" && fileSize != "null"){
                    firmWare_upgrade.setHash_Type(Integer.parseInt(reqHT));
                    firmWare_upgrade.setFW_Size(Integer.parseInt(fileSize));
                }
                global_firm_ware= firmWare_upgrade;
            }



            if(activity == null && reqLatestVersion != "null" && Float.valueOf(device_info.getSW_Version()) < Float.valueOf(reqLatestVersion) && mighty_ble_device != null){
                Intent sharingIntent = new Intent(getApplicationContext(), MightySoftwareUpdateActivity.class);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sharingIntent);
            }else if(activity != null && latestVersion != "null"){
                //firmWare_upgrade.s
                Log.e(TAG,"data came in function "+latestVersion+" "+latestRequired+" "+compatibleAND+" "+compatibleHW+" "+hashValue+" "+hashType+" "+ht);
                //globalClass.device_info.SW_Version = "0.62";
                Log.e(TAG,"check value "+ Float.valueOf(device_info.getSW_Version())+" "+Float.valueOf(latestVersion)+" "+battery_info.AvailablePercentage);

                if(latestVersion.trim().equals(device_info.getSW_Version().trim())){
                    if(mighty_ble_device != null)
                    toastDisplay("Your Mighty is up-to-date");
                    Log.e(TAG,"if condition");
                }else if(reqLatestVersion != "null" && Float.valueOf(device_info.getSW_Version()) < Float.valueOf(reqLatestVersion)){
                    //software_update.setEnabled(true);
                    Intent sharingIntent = new Intent(getApplicationContext(), MightySoftwareUpdateActivity.class);
                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(sharingIntent);
                }else{
                    toastDisplay("Your Mighty is up-to-date");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void marshMellowLocationPermission(Activity activity_obj){
        ConnectivityManager connectionManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            toastDisplay(getString(R.string.ble_not_supported));
        Log.e(TAG,"Build.VERSION.SDK_INT "+Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT )
        {
            // Android M Permission check
            ActivityCompat.requestPermissions(activity_obj, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

    }
    public void cleaAutoConnectData(){
        mighty_info_editor.clear();
        mighty_info_editor.commit();
        mighty_info_editor.clear();
        mighty_info_editor.commit();
        String mighty_info = mightyinfopref.getString(AUTO_CONNECT, "ghanta");
        Log.e(TAG,"Auto value "+mighty_info);
    }

    public boolean internetData(){
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            Log.e(TAG," no internet if condition "+urlc.getResponseCode());
            return (urlc.getResponseCode() == 200);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"no internet catch"+e);
        }
        Log.e(TAG,"no internet else out");
        return false;
    }


    public void hardwarecompatibility(Activity activity,String tittle,String meassge){
        if(alrtMaterialDailog != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        alertpopup = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        alert_header_txt=(TextView)alertpopup.findViewById(R.id.text_header);
        alert_message=(TextView)alertpopup.findViewById(R.id.text_message);
        alert_header_txt.setText(tittle);
        alert_message.setText(meassge);
        alrtMaterialDailog = new MaterialDialog(activity)
                .setView(alertpopup)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alrtMaterialDailog.dismiss();
                        alrtMaterialDailog= null;

                    }
                });
        alrtMaterialDailog.show();
    }
    public void alertDailogSingleText(String message,Activity activity){
        if(anotherMaterial != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        anotherpopup = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        anotherheader_text=(TextView)anotherpopup.findViewById(R.id.text_header);
        anotherheader_text.setText(message);
        anotherMaterial = new MaterialDialog(activity)
                .setView(anotherpopup)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        anotherMaterial.dismiss();
                        anotherMaterial= null;

                    }
                });
        anotherMaterial.show();
    }

    /*public void alertdailogBleDisconnect(Activity activity,String tittle,String meassge){
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
                        bleDisconnect();
                        bleDisconnectMaterial.dismiss();
                        bleDisconnectMaterial= null;

                    }
                });

        bleDisconnectMaterial.show();
    }*/

}
