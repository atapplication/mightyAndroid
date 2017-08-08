package mightyaudio.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mighty.audio.R;
import mightyaudio.Model.Wifi_Status;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;

import static android.view.View.GONE;

public class SetupWifiActivity extends RootActivity implements SwipeRefreshLayout.OnRefreshListener  {
    public final static String TAG = SetupWifiActivity.class.getSimpleName();
    ListView list;
    LinearLayout wifi_cant_detect,mighty_not_connect,wifi_cant_detect_prog;
    boolean wifi_cant = false;
    //WifiManager wifi;
    int index = 0;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    MightyMsgReceiver mightyMsgReceiver;
    public WifiScanListAdapter Wifi_Scanlist;
    public SwipeRefreshLayout swipeLayout;
    Typeface custom_font, custom_font_bold;
    private List<Wifi_Status> wifi_list;
    public  List<Wifi_Status> Temp_Wifi_List = new ArrayList<Wifi_Status>();
    Wifi_Status temp;
    GlobalClass globalClass = GlobalClass.getInstance();
    private TextView wifi_footer_setup,cancel_txt,header_txt,save_txt,mighty_not_connect1,mighty_not_connect2,wifi_cant_detect1,wifi_cant_detect2,wifi_cant_detect3,searching_wifitext,wifi_cant_detect4;
    Context context;
    private Button button;
    private LinearLayout wifi_footer_setup_linear;
    //private View footerView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn_wifi);
        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");

        wifi_footer_setup=(TextView) findViewById(R.id.wifi_footer_setup);
        wifi_footer_setup.setTypeface(custom_font);
        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font_bold);
        save_txt.setTypeface(custom_font);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);

        list = (ListView) findViewById(R.id.listView);
        wifi_footer_setup_linear = (LinearLayout) findViewById(R.id.wifi_footer_setup_linear);
        //footerView= ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.wifi_continue_setup, null, false);
        button = (Button)findViewById(R.id.btn_continue);
        //list.addFooterView(footerView);
        button.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
        button.setEnabled(false);
        wifi_footer_setup_linear.setVisibility(View.VISIBLE);
        wifi_cant_detect = (LinearLayout)  findViewById(R.id.wifi_cant_detect);
        wifi_cant_detect_prog = (LinearLayout)  findViewById(R.id.wifi_cant_detect_prog);
        wifi_cant_detect_prog.setVisibility(GONE);
        wifi_cant_detect.setVisibility(GONE);
        mighty_not_connect = (LinearLayout) findViewById(R.id.mighty_not_connect);
        wifi_list = new ArrayList<Wifi_Status>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SpotifyActivity.class));
            }
        });
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.wifi_swipe_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setEnabled(false);
        Wifi_Scanlist = new WifiScanListAdapter(this,wifi_list);
        list.setAdapter(Wifi_Scanlist);
        if (globalClass.mighty_ble_device ==null) {
            mighty_not_connect.setVisibility(View.VISIBLE);
        }else{
            mighty_not_connect.setVisibility(View.GONE);
        }
        mighty_not_connect1 = (TextView) findViewById(R.id.mighty_not_connect1);
        mighty_not_connect2 = (TextView) findViewById(R.id.mighty_not_connect2);
        wifi_cant_detect1 = (TextView) findViewById(R.id.wifi_cant_detect1);
        wifi_cant_detect2 = (TextView) findViewById(R.id.wifi_cant_detect2);
        wifi_cant_detect3 = (TextView) findViewById(R.id.wifi_cant_detect3);
        wifi_cant_detect4 = (TextView) findViewById(R.id.wifi_cant_detect4);
        searching_wifitext = (TextView)findViewById(R.id.searching_wifitext);
        searching_wifitext.setTypeface(custom_font_bold);
        mighty_not_connect1.setTypeface(custom_font_bold);
        mighty_not_connect2.setTypeface(custom_font);
        wifi_cant_detect1.setTypeface(custom_font_bold);
        wifi_cant_detect2.setTypeface(custom_font);
        wifi_cant_detect3.setTypeface(custom_font);
        wifi_cant_detect4.setTypeface(custom_font);
        button.setTypeface(custom_font);
        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousActivity();
            }
        });

        if (globalClass.mighty_ble_device ==null) {
            mighty_not_connect.setVisibility(View.VISIBLE);
        }else {
            mighty_not_connect.setVisibility(View.GONE);
            if (globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals("")) {
                if (!wifi_list.contains(globalClass.wifi_connected_global)) {
                    wifi_list.add(globalClass.wifi_connected_global);
                    if (Float.valueOf(globalClass.device_info.getSW_Version()) != 0.64f) {
                        button.setBackgroundColor(getResources().getColor(R.color.btn_color));
                        button.setEnabled(true);
                        wifi_footer_setup_linear.setVisibility(GONE);
                    }
                }
            } else if(GlobalClass.wifi_lists_global.isEmpty()) {
                //Initially showing progress bar
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        get_wifi_scan_list();
                        progress_disable();
                    }
                }, 15000);
                wifiscansequence();
                Log.e(TAG, "my else part_");
                wifi_cant_detect_prog.setVisibility(View.VISIBLE);
                wifi_cant_detect.setVisibility(View.GONE);
                mighty_not_connect.setVisibility(GONE);
                swipeLayout.setEnabled(false);
                Wifi_Scanlist.notifyDataSetChanged();
                button.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                button.setEnabled(false);
                wifi_footer_setup_linear.setVisibility(View.VISIBLE);
            }else {
                Log.e(TAG, "wifiscan_list ");
                wifiscansequence();
                wifi_cant_detect_prog.setVisibility(View.GONE);
                wifi_cant_detect.setVisibility(View.GONE);
                mighty_not_connect.setVisibility(GONE);
                swipeLayout.setEnabled(true);
            }
        }
        Wifi_Scanlist.notifyDataSetChanged();
        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }

                }, 8000);

                get_wifi_scan_list();
             //   Toast.makeText(getApplicationContext(), "Scanning WiFi Networks", Toast.LENGTH_LONG).show();
            }
        });

        swipeLayout.post(new Runnable() {
                             @Override
                             public void run() {
                                // get_wifi_scan_list();
                             }
                         }
        );

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView View, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView View, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (list == null || list.getChildCount() == 0) ?
                                0 : list.getChildAt(0).getTop();
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        Wifi_Scanlist.notifyDataSetChanged();
        mightyMsgReceiver = new MightyMsgReceiver(getBaseContext()) {
            @Override
            public void onConnected() {
                Log.e(TAG, "Connected my");
            }

            @Override
            public void onDiscovered() {
                Log.e(TAG, "Discovered");
                wifi_cant = false;
                wifi_cant_detect_prog.setVisibility(View.VISIBLE);
                mighty_not_connect.setVisibility(View.GONE);
                swipeLayout.setVisibility(View.VISIBLE);
                list.setVisibility(GONE);
                wifi_cant_detect.setVisibility(View.GONE);
                swipeLayout.setEnabled(false);
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "Disconnected");
                globalClass.wifi_connected_global = null;
                // swipeLayout.setRefreshing(false);
                swipeLayout.setEnabled(false);
                Wifi_Scanlist.clear();
                wifi_cant_detect.setVisibility(View.GONE);
                swipeLayout.setVisibility(GONE);
                list.setVisibility(GONE);
                wifi_cant_detect_prog.setVisibility(View.GONE);
                GlobalClass.wifi_lists_global.clear();
                mighty_not_connect.setVisibility(View.VISIBLE);
                previousActivity();  // It will previous Activity if mighty is disconnected

            }

        };
                mightyMsgReceiver.RegisterReceiver(getApplicationContext());




    }
    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                handlemightymsg(msgId, msgType);
                Log.e(TAG,"on_Connected_wifi_activity :" +msgId +" msgType "+ msgType);
            }
        };
    }


    public void handlemightymsg(int msgId, int msgType) {
        Log.e(TAG, "MsgID_" + msgId + " MsgType_ " + msgType);

        if (msgId == 20 | msgId == 23) {
            wifiscansequence();
        }
        if (msgId == 16 && msgType == 102) {
            if (globalClass.events_global.getWiFi_status() == 1 | globalClass.events_global.getWiFi_status() == 12)
                get_wifi_status();
            enum_handler(globalClass.events_global.getWiFi_status());

            if(globalClass.events_global.getWiFi_status() == 11 && GlobalClass.wifi_lists_global.isEmpty()) {
                wifi_cant_detect_prog.setVisibility(View.GONE);
                wifi_cant_detect.setVisibility(View.VISIBLE);
                swipeLayout.setEnabled(true);
            }
        }
    }
    public void wifiscansequence(){
        Log.e(TAG, "GlobalClass.wifi_lists_global " + GlobalClass.wifi_lists_global + " " + GlobalClass.wifi_lists_global.size());
        swipeLayout.setVisibility(View.VISIBLE);
        if (!GlobalClass.wifi_lists_global.isEmpty()) {
            wifi_cant = true;
            wifi_cant_detect.setVisibility(GONE);
            wifi_cant_detect_prog.setVisibility(GONE);
            list.setVisibility(View.VISIBLE);
            if (!wifi_list.isEmpty())
                wifi_list.clear();
            wifi_list.addAll(GlobalClass.wifi_lists_global);
            Collections.sort(wifi_list, Wifi_Status.statusComparator);
            globalClass.wifi_connected_global = null;
            // refreshing_list();
            if (!Temp_Wifi_List.isEmpty())
                Temp_Wifi_List.clear();
            Temp_Wifi_List.addAll(wifi_list);
            if (!Temp_Wifi_List.isEmpty()) {
                for (int j = 0; j < Temp_Wifi_List.size(); j++) {
                    Log.e(TAG, "Wifi list in for loop" + Temp_Wifi_List.get(j).getAp_name());
                    if (Temp_Wifi_List.get(j).getStatus() == 1) {
                        globalClass.wifi_connected_global = Temp_Wifi_List.get(j);
                        if (Temp_Wifi_List.size() == 1) {
                            Log.e(TAG, "size is 1" + Temp_Wifi_List.get(0).getAp_name());
                            wifi_list.clear();
                            wifi_list.addAll(Temp_Wifi_List);
                            Temp_Wifi_List.clear();
                        } else {
                            temp = Temp_Wifi_List.get(j);
                            Temp_Wifi_List.set(Temp_Wifi_List.indexOf(Temp_Wifi_List.get(j)), Temp_Wifi_List.get(0));
                            Temp_Wifi_List.set(0, temp);
                            wifi_list.clear();
                            wifi_list.addAll(Temp_Wifi_List);
                            Temp_Wifi_List.clear();
                        }
                    }
                }
                if ((globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals(""))) {
                    globalClass.wifi_status = true;
                    if (Float.valueOf(globalClass.device_info.getSW_Version()) != 0.64f) {
                        button.setBackgroundColor(getResources().getColor(R.color.btn_color));
                        button.setEnabled(true);
                        wifi_footer_setup_linear.setVisibility(GONE);
                    }
                }else{
                    globalClass.wifi_status = false;
                    button.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                    button.setEnabled(false);
                    wifi_footer_setup_linear.setVisibility(View.VISIBLE);
                }
            }
            //swipeLayout.setRefreshing(false);
            Wifi_Scanlist.notifyDataSetChanged();
            swipeLayout.setEnabled(true);
            //footerView.setVisibility(View.VISIBLE);
            Log.e(TAG, "my if part");
            //   text_wifi.setText("Tap the + to add a WiFi network");
        } else {
            progress_disable();
            Log.e(TAG, "my else part");
            // swipeLayout.setRefreshing(false);
            // get_wifi_scan_list();
            wifi_cant_detect_prog.setVisibility(View.VISIBLE);
            wifi_cant_detect.setVisibility(View.GONE);
            mighty_not_connect.setVisibility(GONE);
            list.setVisibility(GONE);
            swipeLayout.setEnabled(false);
        }
    }

    public void progress_disable(){
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if(!wifi_cant) {
                    wifi_cant_detect.setVisibility(View.VISIBLE);
                    // swipeLayout.setRefreshing(false);
                    swipeLayout.setEnabled(true);
                }
                wifi_cant_detect_prog.setVisibility(View.GONE);
                Log.e(TAG,"progressBar_disable ");
            }
        };
        h.sendMessageDelayed(new Message(), 15000);
    }
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause()");
        index = list.getFirstVisiblePosition();
        Log.e(TAG, "onPause() ");
     //   unregisterReceiver(mGattUpdateReceiver);
        mightyMsgReceiver.unRegisterReceiver(getApplicationContext());

    }

    public void onResume() {
        super.onResume();
        if (globalClass.mighty_ble_device ==null)
            mighty_not_connect.setVisibility(View.VISIBLE);

        if(list != null){
            if(list.getCount() > index)
                list.setSelectionFromTop(index, 0);
            else
                list.setSelectionFromTop(0, 0);
        }
        mightyMsgReceiver.RegisterReceiver(getApplicationContext());

        //getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.e(TAG, "onResume() ");
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
             //   get_wifi_scan_list();
            }
        }, 4000);
    }


    // Adapter for holding Wifi_Scan_devices found through scanning.
    private class WifiScanListAdapter extends BaseAdapter {
        private List<Wifi_Status> Wifi_List;
        private ViewHolders viewHolder;
        private Activity adapterContext;

        public WifiScanListAdapter(Activity context, List<Wifi_Status> wifi_list) {
            super();
            this.adapterContext = context;
            this.Wifi_List = wifi_list;
        }

        @Override
        public int getCount() {
            return Wifi_List.size();
        }

        @Override
        public Object getItem(int i) {
            return Wifi_List.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void clear() {
            Wifi_List.clear();
        }


        @Override
        public View getView(final int i, View view, ViewGroup parent) {
            if (view == null) {
                view = View.inflate(adapterContext, R.layout.wifiscan_list,null);
                viewHolder = new ViewHolders();
                viewHolder.custom_font = Typeface.createFromAsset(adapterContext.getAssets(), "serenity-light.ttf");
                viewHolder.wifi_deviceName = (TextView) view.findViewById(R.id.wifi_name);
                viewHolder.text_arrow = (TextView) view.findViewById(R.id.text_arrow);
                viewHolder.wifi_img_plus = (ImageView) view.findViewById(R.id.wifi_img_plus);
                viewHolder.img_plus_rect = (FrameLayout) view.findViewById(R.id.img_plus_rect);
                viewHolder.wifi_img_blue_tick = (ImageView) view.findViewById(R.id.wifi_img_blue_tick);

                viewHolder.wifi_disconnect = (ImageView)view.findViewById(R.id.wifi_disconnect);
                viewHolder.wifi_swipe = (SwipeLayout) view.findViewById(R.id.wifi_swipe);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolders) view.getTag();
            }
            viewHolder.text_arrow.setTypeface(custom_font);
            viewHolder.text_arrow.setVisibility(GONE);
            viewHolder.wifi_img_blue_tick.setVisibility(GONE);
            viewHolder.wifi_swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
            viewHolder.wifi_swipe.setSwipeEnabled(false);

            viewHolder.wifi_swipe.open(true);
            viewHolder.wifi_swipe.close(true);

            if(Wifi_List.get(i).getAp_name() != null)
                viewHolder.wifi_deviceName.setText(Wifi_List.get(i).getAp_name());
            else viewHolder.wifi_deviceName.setText("Unknown Network");


            //  final int adapterPosition = viewHolder.getClass().getAdapterPosition();
            if( Wifi_List.get(i).getStatus()==1 || Wifi_List.get(i)== globalClass.wifi_connected_global){
                viewHolder.wifi_deviceName.setTypeface(custom_font_bold);
                viewHolder.wifi_img_plus.setVisibility(View.GONE);
                viewHolder.img_plus_rect.setVisibility(View.GONE);
                viewHolder.text_arrow.setVisibility(View.VISIBLE);
                viewHolder.wifi_img_blue_tick.setVisibility(View.VISIBLE);
                viewHolder.wifi_swipe.setSwipeEnabled(true);
            }else{
                viewHolder.wifi_deviceName.setTypeface(custom_font);
                viewHolder.wifi_img_plus.setVisibility(View.VISIBLE);
                viewHolder.text_arrow.setVisibility(View.GONE);
                viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                viewHolder.wifi_img_blue_tick.setVisibility(View.GONE);
                viewHolder.wifi_swipe.setSwipeEnabled(false);
            }
            viewHolder.wifi_img_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Wifi_Status bt_wifiName = Wifi_List.get(i);
                    if (bt_wifiName == null) {
                        Log.d(TAG, "wifi_img_plus is null " + bt_wifiName);
                        return;
                    }
                    globalClass.wifi_status_global = bt_wifiName;

                    Intent intent = new Intent(SetupWifiActivity.this,  MightyWifiConnectionActivity.class);
                    Gson gson = new Gson();
                    String wifi_obj= gson.toJson(bt_wifiName);
                    intent.putExtra("wifiobj",wifi_obj);
                    startActivity(intent);
                }
            });

            viewHolder.wifi_disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Wifi_Status bt_wifiName_cross = Wifi_List.get(i);
                    if (!globalClass.syncing_status) {
                        if (bt_wifiName_cross == null) {
                            Log.d(TAG, "wifi_img_plus is null " + bt_wifiName_cross);
                            return;
                        }
                        globalClass.wifi_status_global = bt_wifiName_cross;
                        disconnect_wifi_network();
                        bt_wifiName_cross.setStatus(2);
                        button.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                        button.setEnabled(false);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                get_wifi_scan_list();
                            }
                            },1600);
                        wifi_footer_setup_linear.setVisibility(View.VISIBLE);
                        globalClass.wifi_connected_global = null;
                        viewHolder.wifi_deviceName.setTypeface(custom_font);
                        viewHolder.wifi_img_plus.setVisibility(View.VISIBLE);
                        viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                        viewHolder.text_arrow.setVisibility(View.INVISIBLE);
                        viewHolder.wifi_img_blue_tick.setVisibility(View.GONE);

                    }else {
                        globalClass.toastDisplay("Playlist is Syncing can't Disconnect WiFi");
                    }
                }
            });

            return view;
        }
    }

    public class ViewHolders {
        TextView wifi_deviceName;
        ImageView wifi_img_plus;
        TextView text_arrow;
        ImageView wifi_disconnect;
        FrameLayout img_plus_rect;
        SwipeLayout wifi_swipe;
        ImageView wifi_img_blue_tick;
        Typeface custom_font;
    }

    public void get_wifi_scan_list() {

        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 23;

        tcpClient.SendData(mightyMessage);
        Log.d("Sent wifi structure", "Done");
    }

    public void disconnect_wifi_network() {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 23;

        Log.d(TAG, "Set wifi_scan structure");
        //Send the GET request to Mighty Device, Note:Only Header is Sent.
        tcpClient.SendData(mightyMessage);
    }

    public void get_wifi_status() {

        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 20;

        tcpClient.SendData(mightyMessage);
        Log.d("Sent wifi status", "Done");
    }
    public void enum_handler(int enum_value)
    {
        //  int value = Integer.parseInt(enum_value);
        switch (enum_value)
        {
            case Constants.WIFI_CONNECTED:
                //get_wifi_status();
                Log.d(TAG,"WIFI_CONNECTED_Fragement");
                Wifi_Scanlist.notifyDataSetChanged();
/*                if (Float.valueOf(globalClass.device_info.getSW_Version()) != 0.64f) {
                    button.setBackgroundColor(getResources().getColor(R.color.btn_color));
                    button.setEnabled(true);
                }*/
                break;
            case Constants.WIFI_DISCONNECTED:
                Log.d(TAG,"WIFI_DISCONNECTED_Fragement");
                Wifi_Scanlist.notifyDataSetChanged();
/*                button.setBackgroundColor(getResources().getColor(R.color.btn_disable_color));
                button.setEnabled(false);*/
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        previousActivity();
    }

    private void previousActivity(){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        unregisterReceiver(ble_onreceive);
    }
}
