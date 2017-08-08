package mightyaudio.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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

import mightyaudio.Model.Wifi_Status;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mighty.audio.R;
import mightyaudio.activity.MightyWifiConnectionActivity;
import mightyaudio.core.GlobalClass;

import static android.view.View.GONE;

public class ConnWifiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String TAG = ConnWifiFragment.class.getSimpleName();
    ListView lv;
    LinearLayout wifi_cant_detect,mighty_not_connect,wifi_cant_detect_prog;
    boolean wifi_cant;
    boolean wifi_cant_else = false;
    //WifiManager wifi;
    int index = 0;
    public WifiScanListAdapter Wifi_Scanlist;
    public SwipeRefreshLayout swipeLayout;
    Typeface custom_font, custom_font_bold;
    private List<Wifi_Status> wifi_list;
    public  List<Wifi_Status> Temp_Wifi_List = new ArrayList<Wifi_Status>();
    Wifi_Status temp;
    View wifi_footer;
    private android.app.AlertDialog.Builder alertDialogBuilder;
    GlobalClass globalClass = GlobalClass.getInstance();
    private static final int REQUEST_FINE_LOCATION = 0;
    TextView wifi_footer_text,mighty_not_connect1,mighty_not_connect2,wifi_cant_detect1,wifi_cant_detect2,wifi_cant_detect3,wifi_cant_detect4,wifi_head,searching_wifitext;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() ");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_conn_wifi, container, false);
        custom_font = Typeface.createFromAsset(getActivity().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getActivity().getAssets(), "serenity-bold.ttf");
        //text_wifi = (TextView) v.findViewById(R.id.text_wifi);
        lv = (ListView) v.findViewById(R.id.listView);
        wifi_cant_detect = (LinearLayout)  v.findViewById(R.id.wifi_cant_detect);
        wifi_cant_detect_prog = (LinearLayout)  v.findViewById(R.id.wifi_cant_detect_prog);
        wifi_cant_detect_prog.setVisibility(GONE);
        wifi_cant_detect.setVisibility(GONE);
        mighty_not_connect = (LinearLayout)  v.findViewById(R.id.mighty_not_connect);
        wifi_list = new ArrayList<Wifi_Status>();
        //wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.wifi_swipe_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setEnabled(false);
        Wifi_Scanlist = new WifiScanListAdapter(getActivity(),wifi_list);
        lv.setAdapter(Wifi_Scanlist);
        mighty_not_connect.setVisibility(View.VISIBLE);
        wifi_head = (TextView) v.findViewById(R.id.wifi_head);
        mighty_not_connect1 = (TextView) v.findViewById(R.id.mighty_not_connect1);
        mighty_not_connect2 = (TextView) v.findViewById(R.id.mighty_not_connect2);
        wifi_cant_detect1 = (TextView) v.findViewById(R.id.wifi_cant_detect1);
        wifi_cant_detect2 = (TextView) v.findViewById(R.id.wifi_cant_detect2);
        wifi_cant_detect3 = (TextView) v.findViewById(R.id.wifi_cant_detect3);
        wifi_cant_detect4 = (TextView) v.findViewById(R.id.wifi_cant_detect4);
        searching_wifitext = (TextView) v.findViewById(R.id.searching_wifitext);

        wifi_head.setTypeface(custom_font_bold);
        mighty_not_connect1.setTypeface(custom_font_bold);
        mighty_not_connect2.setTypeface(custom_font);
        searching_wifitext.setTypeface(custom_font_bold);
        wifi_cant_detect1.setTypeface(custom_font_bold);
        wifi_cant_detect2.setTypeface(custom_font);
        wifi_cant_detect3.setTypeface(custom_font);
        wifi_cant_detect4.setTypeface(custom_font);
        wifi_footer = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.wifi_footer, null, false);
        wifi_footer_text = (TextView) wifi_footer.findViewById(R.id.wifi_footer_text);
        wifi_footer_text.setTypeface(custom_font);
        lv.addFooterView(wifi_footer);
        wifi_footer.setVisibility(View.GONE);

        if (globalClass.mighty_ble_device ==null) {
            mighty_not_connect.setVisibility(View.VISIBLE);
        }else {
            mighty_not_connect.setVisibility(View.GONE);
            if (globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals("")) {
                if (!wifi_list.contains(globalClass.wifi_connected_global)) {
                    wifi_list.add(globalClass.wifi_connected_global);
                    wifi_footer.setVisibility(View.GONE);
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
                }, 50000);
                wifiscanlistsequence();
                Log.e(TAG, "my else part_");
                wifi_cant_detect_prog.setVisibility(View.VISIBLE);
                wifi_cant_detect.setVisibility(View.GONE);
                mighty_not_connect.setVisibility(GONE);
                swipeLayout.setEnabled(false);
                Wifi_Scanlist.notifyDataSetChanged();
            }else {
                Log.e(TAG, "wifiscan_list ");
                wifiscanlistsequence();
                wifi_footer.setVisibility(View.VISIBLE);
                lv.setVisibility(View.VISIBLE);
                wifi_cant_detect_prog.setVisibility(View.GONE);
                wifi_cant_detect.setVisibility(View.GONE);
                mighty_not_connect.setVisibility(GONE);
                swipeLayout.setEnabled(true);
            }
        }

        Wifi_Scanlist.notifyDataSetChanged();
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
                //Toast.makeText(getActivity(), "Scanning WiFi Networks", Toast.LENGTH_LONG).show();
            }
        });



        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView View, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView View, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (lv == null || lv.getChildCount() == 0) ?
                                0 : lv.getChildAt(0).getTop();
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        return v;

    }
    public void progress_disable(){

        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message message) {
                wifi_cant_else = true;
                if(!wifi_cant) {
                    wifi_cant_detect.setVisibility(View.VISIBLE);
                    swipeLayout.setEnabled(true);
                }
                wifi_cant_detect_prog.setVisibility(View.GONE);
                Log.e(TAG,"progressBar_disable ");
            }
        };
        h.sendMessageDelayed(new Message(), 15000);
    }

    public void bleDisconnected(){
        Log.e(TAG,"bleDisconnected");
        globalClass.wifi_connected_global = null;
        // swipeLayout.setRefreshing(false);
        swipeLayout.setEnabled(false);
        Wifi_Scanlist.clear();
        wifi_footer.setVisibility(View.GONE);
        wifi_cant_detect.setVisibility(View.GONE);
        swipeLayout.setVisibility(GONE);
        lv.setVisibility(GONE);
        wifi_cant_detect_prog.setVisibility(View.GONE);
        GlobalClass.wifi_lists_global.clear();
        mighty_not_connect.setVisibility(View.VISIBLE);
    }
    public void blDdiscovered(){
        Log.e(TAG,"blDdiscovered");
        wifi_cant = false;
        wifi_cant_else = false;
        wifi_cant_detect_prog.setVisibility(View.VISIBLE);
        mighty_not_connect.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        lv.setVisibility(GONE);
        wifi_cant_detect.setVisibility(View.GONE);
        swipeLayout.setEnabled(false);
    }



    public void onReceiveMessage(int msgId, int msgType) {
        Log.e(TAG, "MsgID_" + msgId + " MsgType_ " + msgType);
        if (msgId == 20 | msgId == 16 | msgId == 23) {
            wifiscanlistsequence();
            if (msgType == 102) {
                Wifi_Scanlist.notifyDataSetChanged();
                if (globalClass.events_global.getWiFi_status() == 1 | globalClass.events_global.getWiFi_status() == 12)
                    get_wifi_status();
                if(globalClass.events_global.getWiFi_status() == 11 && GlobalClass.wifi_lists_global.isEmpty()) {
                    wifi_cant_detect_prog.setVisibility(View.GONE);
                    wifi_cant_detect.setVisibility(View.VISIBLE);
                    swipeLayout.setEnabled(true);
                }
            }
        }
    }


    public void wifiscanlistsequence() {
        enum_handler(globalClass.events_global.getWiFi_status());
        Log.e(TAG, "GlobalClass.wifi_lists_global " + GlobalClass.wifi_lists_global + " " + GlobalClass.wifi_lists_global.size());
        swipeLayout.setVisibility(View.VISIBLE);
        if (!GlobalClass.wifi_lists_global.isEmpty()) {
            wifi_cant = true;
            wifi_cant_detect.setVisibility(GONE);
            wifi_cant_detect_prog.setVisibility(GONE);
            lv.setVisibility(View.VISIBLE);
            if (!wifi_list.isEmpty())
                wifi_list.clear();
            wifi_list.addAll(GlobalClass.wifi_lists_global);
            Collections.sort(wifi_list, Wifi_Status.statusComparator);
            // refreshing_list();
            if (!Temp_Wifi_List.isEmpty())
                Temp_Wifi_List.clear();
            globalClass.wifi_connected_global = null;
            Temp_Wifi_List.addAll(wifi_list);
            if (!Temp_Wifi_List.isEmpty()) {
                for (int j = 0; j < Temp_Wifi_List.size(); j++) {
                    Log.e(TAG, "Wifi list in for loop" + Temp_Wifi_List.get(j).getAp_name());
                    if (Temp_Wifi_List.get(j).getStatus() == 1) {
                        globalClass.wifi_connected_global = Temp_Wifi_List.get(j);
                        wifi_footer.setVisibility(View.GONE);
                        if (Temp_Wifi_List.size() == 1) {
                            Log.e(TAG, "size is 1" + Temp_Wifi_List.get(0).getAp_name());
                            wifi_list.clear();
                            wifi_list.addAll(Temp_Wifi_List);
                            Temp_Wifi_List.clear();
                        } else {
                            temp = Temp_Wifi_List.get(j);
                            Temp_Wifi_List.remove(temp);
                            wifi_list.clear();
                            wifi_list.add(temp);
                            wifi_list.addAll(Temp_Wifi_List);
                            Temp_Wifi_List.clear();
                        }
                    }else {
                        wifi_footer.setVisibility(View.VISIBLE);
                    }
                }
                if ((globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null && !globalClass.wifi_connected_global.ap_name.equals(""))) {
                    globalClass.wifi_status = true;
                    wifi_footer.setVisibility(View.GONE);
                }else{
                    globalClass.wifi_status = false;
                    wifi_footer.setVisibility(View.VISIBLE);
                }
            }

        //swipeLayout.setRefreshing(false);
        Wifi_Scanlist.notifyDataSetChanged();
        swipeLayout.setEnabled(true);

        Log.e(TAG, "my if part");
    } else {
            progress_disable();
            Log.e(TAG, "my else part");
            wifi_cant_detect_prog.setVisibility(View.VISIBLE);
            wifi_cant_detect.setVisibility(View.GONE);
            mighty_not_connect.setVisibility(GONE);
            lv.setVisibility(GONE);
            swipeLayout.setEnabled(false);
        }
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
                get_wifi_scan_list();
                Log.e(TAG," Refresh_hitted");
            }
        }, 4000);
    }

    public void onPause() {
        super.onPause();
        index = lv.getFirstVisiblePosition();

        Log.e(TAG, "onPause() ");
        //mightyMsgReceiver.unRegisterReceiver(context);
    }

    public void onResume() {
        super.onResume();
        globalClass.process_GoingOn = false;
        if(lv != null){
            if(lv.getCount() > index)
                lv.setSelectionFromTop(index, 0);
            else
                lv.setSelectionFromTop(0, 0);
        }
        //mightyMsgReceiver.RegisterReceiver(context);

        //getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.e(TAG, "onResume() ");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart() ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        Log.e(TAG, "onAttach() ");
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
            viewHolder.text_arrow.setTypeface(viewHolder.custom_font);
            viewHolder.text_arrow.setVisibility(GONE);
            viewHolder.wifi_img_blue_tick.setVisibility(GONE);
            viewHolder.wifi_swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
            viewHolder.wifi_swipe.setSwipeEnabled(false);

            viewHolder.wifi_swipe.open(true);
            viewHolder.wifi_swipe.close(true);

            Log.e(TAG, "after condition" + Wifi_List.get(i).getAp_name());

            if(Wifi_List.get(i).getAp_name() != null)
                viewHolder.wifi_deviceName.setText(Wifi_List.get(i).getAp_name());
            else viewHolder.wifi_deviceName.setText("Unknown Network");


            //  final int adapterPosition = viewHolder.getClass().getAdapterPosition();

            if( Wifi_List.get(i).getStatus()==1 ){
         //       wifi_footer.setVisibility(View.GONE);
                Log.e(TAG,"wifi status"+Wifi_List.get(i).getAp_name());
                globalClass.wifi_connected_global = Wifi_List.get(i);
                viewHolder.wifi_deviceName.setTypeface(custom_font);
                viewHolder.wifi_img_plus.setVisibility(View.GONE);
                viewHolder.img_plus_rect.setVisibility(View.GONE);
                viewHolder.text_arrow.setVisibility(View.VISIBLE);
                viewHolder.wifi_img_blue_tick.setVisibility(View.VISIBLE);
                viewHolder.wifi_swipe.setSwipeEnabled(true);
            }else{
           //     wifi_footer.setVisibility(View.VISIBLE);
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
                    globalClass.process_GoingOn = true;
                    globalClass.wifi_status_global = bt_wifiName;
                    globalClass.process_GoingOn = true;
                    Intent intent = new Intent(getActivity(),  MightyWifiConnectionActivity.class);
                    Gson gson = new Gson();
                    String wifi_obj= gson.toJson(bt_wifiName);
                    intent.putExtra("wifiobj",wifi_obj);
                    startActivity(intent);

                }
            });

            viewHolder.wifi_disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.wifi_swipe.close();
                    if(!globalClass.syncing_status) {
                        final Wifi_Status bt_wifiName_cross = Wifi_List.get(i);

                        if (bt_wifiName_cross == null) {
                            Log.d(TAG, "wifi_img_plus is null " + bt_wifiName_cross);
                            return;
                        }
                        globalClass.wifi_status_global = bt_wifiName_cross;
                        disconnect_wifi_network();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                get_wifi_scan_list();
                            }
                        },1600);
                        bt_wifiName_cross.setStatus(2);
                        globalClass.wifi_connected_global = null;
                        viewHolder.wifi_deviceName.setTypeface(custom_font);
                        viewHolder.wifi_img_plus.setVisibility(View.VISIBLE);
                        viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                        viewHolder.text_arrow.setVisibility(View.INVISIBLE);
                        viewHolder.wifi_img_blue_tick.setVisibility(View.GONE);
                        wifi_footer.setVisibility(View.VISIBLE);
                    }else {

                        globalClass.hardwarecompatibility(getActivity(),getString(R.string.wifi_is_required_syncing),getString(R.string.playlist_selectwhile_sync_meaage));
                        //setupDailogSwitch();
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

    public void get_wifi_status() {

        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 20;

        tcpClient.SendData(mightyMessage);
        Log.d("Sent wifi status", "Done");
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


    public void enum_handler(int enum_value)
    {
        //  int value = Integer.parseInt(enum_value);
        switch (enum_value)
        {
            case Constants.WIFI_CONNECTED:
                //get_wifi_status();
                Log.d(TAG,"WIFI_CONNECTED_Fragement");
                Wifi_Scanlist.notifyDataSetChanged();
                break;
            case Constants.WIFI_DISCONNECTED:
                Log.d(TAG,"WIFI_DISCONNECTED_Fragement");
                Wifi_Scanlist.notifyDataSetChanged();
                break;
            default:
        }
    }
}

