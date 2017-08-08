package mightyaudio.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mightyaudio.Model.Events;
import mightyaudio.Model.Headset_History;
import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mighty.audio.R;
import mightyaudio.activity.ConnectActivity;
import mightyaudio.activity.DeviceRenameActivity;
import mightyaudio.adapter.BtDeviceAdapter;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SessionManager;

import static android.view.View.GONE;


public class ConnBluetoothFragmentBle extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String TAG = ConnBluetoothFragmentBle.class.getSimpleName();

    public LeDeviceListAdapter mLeDeviceListAdapter;
    public BtDeviceAdapter BtdeviceListAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    //String ble_device_name;
    ListView listView,bt_listView;
    FrameLayout frame;
    TextView mighty_device,Bt_Accessories,ble_not_text1,ble_not_text2,ble_discover_text1,ble_discover_text2,bluetooth_head,bt_plus_tap,spinner_text;
    LinearLayout ble_not_enabled,ble_discoverable;
    Typeface custom_font,custom_font_bold;
    ProgressBar progressBar_footer;

    private BluetoothLeScanner mBluetoothLeScanner;
    GlobalClass globalClass = GlobalClass.getInstance();
    private static Timer timer= new Timer();
    private static TimerTask timerTask;
    final Handler handler = new Handler();
    public  SwipeRefreshLayout swipeLayout;
    List<Headset_History> BT_history;
    HashMap<String,Headset_History> hashMap_bt_history;
    Map<String,BluetoothDevice> ble_hash_map = new LinkedHashMap<String,BluetoothDevice>();
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> local_ble_list = new ArrayList<BluetoothDevice>();
    private Context context;
    private SessionManager session;
    private SharedPreferences pref;
    private IntentFilter intentFilter ;
    private BroadcastReceiver ble_scan_list_receiver;
    View mProgressBarFooter;
    private int timout_flag= 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context= activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_conn_ble, container, false);
        Log.e(TAG,"onCreateView");
        custom_font = Typeface.createFromAsset(getActivity().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getActivity().getAssets(), "serenity-bold.ttf");
        session = new SessionManager(getActivity());
        pref = context.getSharedPreferences(session.PREF_NAME , Context.MODE_PRIVATE);
        hashMap_bt_history = new HashMap<String,Headset_History>();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        listView = (ListView)view.findViewById(R.id.listView);
        bt_listView = (ListView)view.findViewById(R.id.Frame_listView);
        frame = (FrameLayout)view.findViewById(R.id.frame);
        frame.setVisibility(View.GONE);

        mighty_device = (TextView)view.findViewById(R.id.mighty_device);
        Bt_Accessories = (TextView)view.findViewById(R.id.Bt_Accessories);
        ble_not_text1 = (TextView)view.findViewById(R.id.ble_not_text1);
        ble_not_text2 = (TextView)view.findViewById(R.id.ble_not_text2);
        ble_discover_text1 = (TextView)view.findViewById(R.id.ble_discover_text1);
        ble_discover_text2 = (TextView)view.findViewById(R.id.ble_discover_text2);
        //  ble_discover_text3 = (TextView)view.findViewById(R.id.ble_discover_text3);
        bluetooth_head = (TextView)view.findViewById(R.id.bluetooth_head);
        bt_plus_tap = (TextView)view.findViewById(R.id.bt_plus_tap);
        //bt_swipe_down = (TextView)view.findViewById(R.id.bt_swipe_down);
        bt_plus_tap.setVisibility(GONE);

        bluetooth_head.setTypeface(custom_font_bold);
        mighty_device.setTypeface(custom_font_bold);
        Bt_Accessories.setTypeface(custom_font_bold);
        ble_not_text1.setTypeface(custom_font_bold);
        ble_not_text2.setTypeface(custom_font);

        ble_discover_text1.setTypeface(custom_font_bold);
        ble_discover_text2.setTypeface(custom_font);
        //  ble_discover_text3.setTypeface(custom_font);
        bt_plus_tap.setTypeface(custom_font);
        //  bt_swipe_down.setTypeface(custom_font);
        //  bt_swipe_down.setVisibility(GONE);
        //  Bt_Accessories.setVisibility(View.GONE);
        ble_discoverable = (LinearLayout)view.findViewById(R.id.ble_discoverable);

        ble_not_enabled = (LinearLayout)view.findViewById(R.id.ble_not_enabled);
        ble_discoverable.setVisibility(View.GONE);
        ble_not_enabled.setVisibility(View.GONE);
        mProgressBarFooter = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.ble_spinner, null, false);
        progressBar_footer = (ProgressBar) mProgressBarFooter.findViewById(R.id.progressBar_footer);
        spinner_text = (TextView) mProgressBarFooter.findViewById(R.id.spinner_text);
        spinner_text.setTypeface(custom_font);
        listView.addFooterView(mProgressBarFooter);
        mProgressBarFooter.setVisibility(GONE);
        BT_history = new ArrayList<Headset_History>();
        BtdeviceListAdapter = new BtDeviceAdapter(getActivity(),BT_history);
        BtdeviceListAdapter.setMode(Attributes.Mode.Single);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);
        bt_listView.setAdapter(BtdeviceListAdapter);

        Log.e(TAG,"size of mLeDevices "+mLeDevices.size()+" "+globalClass.scan_ble_list);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setEnabled(false);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(Bluetooth_BroadcastReceiver, filter);
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        // BluetoothAdapter through BluetoothManager.
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        //mBluetoothLeService = GlobalClass.mBluetoothLeService_global;
        //getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (swipeLayout == null) {
            System.out.println("Swipelayout is empty");
        } else {
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeLayout.setRefreshing(false);
                        }

                    }, 9000);

                    send_bt_scan_request();
                    BtdeviceListAdapter.notifyDataSetChanged();
                    // Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_LONG).show();
                }
            });

        }


        //When Ble Connection from Setup Flow
        if(globalClass.mighty_ble_device != null) {
            Log.e(TAG,"if part exectuing ");
            if(mLeDeviceListAdapter == null)
                mLeDeviceListAdapter = new LeDeviceListAdapter();
            globalClass.setup_status = true;
            //Receiving_bt_structure();
            progressBar_footer.setVisibility(GONE);
            mProgressBarFooter.setVisibility(View.GONE);
            mLeDeviceListAdapter.addDevice(globalClass.mighty_ble_device);
            mLeDeviceListAdapter.notifyDataSetChanged();
            frame.setVisibility(View.VISIBLE);
            globalClass.Status = true;
            globalClass.scanLeDevice(false);
            addBtAccessories();
            ble_discoverable.setVisibility(View.GONE);
            stoptimertask();
        }else {
            Log.e(TAG,"else part exectuing "+globalClass.scan_ble_list.size());
            if(!globalClass.Status) {
                startTimer();
            }
            ble_hash_map.putAll(globalClass.scan_ble_list);
            // linkedHashble.addAll(globalClass.scan_ble_list.values());
            mLeDevices.addAll(ble_hash_map.values());
            if(mLeDevices.isEmpty()){
                ble_discoverable.setVisibility(View.VISIBLE);
                mProgressBarFooter.setVisibility(View.GONE);
            }
            if(globalClass.mBluetoothAdapter.isEnabled()){
                globalClass.scan_ble_list.clear();
                ble_discoverable.setVisibility(View.GONE);
                mProgressBarFooter.setVisibility(View.VISIBLE);
            }else{
                ble_not_enabled.setVisibility(View.VISIBLE);
                ble_discoverable.setVisibility(GONE);
            }

        }
        scanListBle();
        context.registerReceiver(ble_scan_list_receiver,intentFilter);

        return view;
    }


    private void scanListBle(){
        intentFilter = new IntentFilter("ble.scan.list");
        ble_scan_list_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent bleScanIntent) {
                Log.e(TAG,"On Receive hit");

                BluetoothDevice device = bleScanIntent.getParcelableExtra("blelist");
                Log.e(TAG,"Device_uuid" + device.getName() +"addr "+device.getAddress()+" "+globalClass.Status);
                ble_discoverable.setVisibility(View.GONE);
                if(globalClass.Status){
                    stoptimertask();
                    // globalClass.scanLeDevice(false);
                    mLeDeviceListAdapter.clear();
                    mLeDeviceListAdapter.addDevice(device);
                    mProgressBarFooter.setVisibility(View.GONE);
                    ble_hash_map.put(device.getAddress(), device);
                }else{
                    mLeDeviceListAdapter.addDevice(device);
                    ble_hash_map.put(device.getAddress(), device);
                }
            }
        };
    }

    //*************************************************
    public void bleDiscovered(){
        globalClass.auto_connect_force_cancel = 1;
        timout_flag = 2;
        progressBar_footer.setVisibility(GONE);
        mProgressBarFooter.setVisibility(View.GONE);
        if(globalClass.mighty_ble_device == null){
            Log.e(TAG," globalclass_mighty is null");
        }else Log.e(TAG,"Discover calling "+globalClass.mighty_ble_device.getName());
        frame.setVisibility(View.VISIBLE);
        globalClass.Status = true;
        //globalClass.scanLeDevice(false);
        stoptimertask();
        bt_plus_tap.setVisibility(View.VISIBLE);
        //Copy From Above those are sace broad cast registered
        mLeDeviceListAdapter.clear();
        if(!ble_hash_map.isEmpty())
            ble_hash_map.clear();
        if(globalClass.mighty_ble_device != null)
            mLeDeviceListAdapter.addDevice(globalClass.mighty_ble_device);
        mLeDeviceListAdapter.notifyDataSetChanged();

        if(getActivity() != null) {
            getActivity().invalidateOptionsMenu();

            ConnectActivity connectActivity = (ConnectActivity) getActivity();
            // connectActivity.refresh();
        }
    }

    public  void bleConnected(){
        Log.e(TAG,"timer is stopped onConnected");
        timout_flag = 1;
        mProgressBarFooter.setVisibility(View.VISIBLE);
        progressBar_footer.setVisibility(GONE);
        stoptimertask();
    }

    public void bleDisconnected(){
        timout_flag = 0;
        Log.e(TAG,"disconnected_called");
        mProgressBarFooter.setVisibility(View.VISIBLE);
        progressBar_footer.setVisibility(View.VISIBLE);
        // listView.addFooterView(mProgressBarFooter);
        mLeDeviceListAdapter.clear();
        mLeDeviceListAdapter.notifyDataSetChanged();
        globalClass.Status = false;
        globalClass.setup_status = false;
        globalClass.scanLeDevice(true);
        startTimer();
        bt_plus_tap.setVisibility(View.GONE);
        if(!GlobalClass.hasmap_bt_scan_headset_lists.isEmpty())
            GlobalClass.hasmap_bt_scan_headset_lists.clear();
        swipeLayout.setEnabled(false);
        frame.setVisibility(GONE);
        BtdeviceListAdapter.stoptimertask();
        BtdeviceListAdapter.clear();
        if(!ble_hash_map.isEmpty())
            ble_hash_map.clear();
        BtdeviceListAdapter.notifyDataSetChanged();
        if(!BT_history.isEmpty()){
            BT_history.clear();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //scanLeDevice(false);
        Log.e(TAG,"onResume");


    }

    //It Should be Setup flow
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG,"Blutooth  not enable");
            getActivity().finish();

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.e(TAG,"onPause");
    }
    private final BroadcastReceiver Bluetooth_BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Log.e(TAG,"From Broad Cast STATE_OFF ");
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG,"From Broad Cast STATE_OFF "+!globalClass.mBluetoothAdapter.isEnabled());
                        Log.e(TAG,"mBluetoothAdapter Disable in Fragment");
                        mLeDeviceListAdapter.clear();
                        mLeDeviceListAdapter.notifyDataSetChanged();
                        mProgressBarFooter.setVisibility(GONE);
                        listView.setVisibility(View.GONE);
                        mighty_device.setVisibility(View.GONE);
                        globalClass.scanLeDevice(false);
                        globalClass.Status = false;
                        stoptimertask();
                        if(!ble_hash_map.isEmpty())
                            ble_hash_map.clear();
                        if(!mLeDevices.isEmpty())
                            mLeDevices.clear();
                        ble_discoverable.setVisibility(View.GONE);
                        globalClass.bleDisconnect();
                        ble_not_enabled.setVisibility(View.VISIBLE);

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        startTimer();
                        ble_not_enabled.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        mighty_device.setVisibility(View.VISIBLE);
                        globalClass.scanLeDevice(true);
                        mProgressBarFooter.setVisibility(View.VISIBLE);

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }

            }
        }
    };
    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        globalClass.scanLeDevice(false);
        getActivity().unregisterReceiver(Bluetooth_BroadcastReceiver);
        stoptimertask();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"onDetach");
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                send_bt_scan_request();
                swipeLayout.setRefreshing(false);
                //  Toast.makeText(getActivity(), "Refreshing list", Toast.LENGTH_LONG).show();
            }
        }, 5000);
    }
    public void startTimer() {
        Log.e(TAG,"timer start"+ mLeDevices.size()+" "+ ble_hash_map.size());
/*        if(!globalClass.Status)
            globalClass.scanLeDevice(true);
     //   mLeDeviceListAdapter.clear();
   //     mLeDeviceListAdapter.notifyDataSetChanged();*/
        initializeTimerTask();

        if (timer ==null)
            timer = new Timer();
        timer.schedule(timerTask, 11000, 11000);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        Log.e(TAG,"timer Stop"+ mLeDevices.size()+" "+ ble_hash_map.size());
/*        if(globalClass.Status) {
            mProgressBarFooter.setVisibility(View.VISIBLE);
            progressBar_footer.setVisibility(GONE);
        }*/
        // stop_scanning = true;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            if (timerTask != null){
                timerTask.cancel();
                timerTask = null;
            }
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        Log.e(TAG,"timer___"+ mLeDevices.size()+" "+ ble_hash_map.size());
                        if (globalClass.mighty_ble_device == null) {
                            if(!ble_hash_map.isEmpty()) {
                                ble_discoverable.setVisibility(View.GONE);
                                mProgressBarFooter.setVisibility(View.VISIBLE);
                                for (int j = 0; j < mLeDevices.size(); j++) {
                                    if (ble_hash_map.get(mLeDevices.get(j).getAddress()) == null)
                                        local_ble_list.add(mLeDevices.get(j));
                                }
                                ble_hash_map.clear();

                                for (int i = 0; i < local_ble_list.size(); i++) {
                                    mLeDevices.remove(local_ble_list.get(i));
                                    ble_hash_map.put(local_ble_list.get(i).getAddress(), local_ble_list.get(i));
                                }

                                if (!local_ble_list.isEmpty())
                                    local_ble_list.clear();
                                if (mLeDevices.isEmpty()) {
                                    ble_discoverable.setVisibility(View.VISIBLE);
                                    mProgressBarFooter.setVisibility(View.GONE);
                                }
                                Log.e(TAG, " if timer  mledevice ");
                            }else {
                                if (!mLeDevices.isEmpty())
                                    mLeDevices.clear();
                                ble_discoverable.setVisibility(View.VISIBLE);
                                mProgressBarFooter.setVisibility(View.GONE);
                                if (!globalClass.mBluetoothAdapter.isEnabled()) {
                                    ble_discoverable.setVisibility(View.GONE);
                                    mProgressBarFooter.setVisibility(View.GONE);
                                }                                }
                            Log.e(TAG," mLeDevices value "+mLeDevices.size()+" "+ble_hash_map.size());
                            //if(mLeDevices.isEmpty())
                            globalClass.scanLeDevice(false);
                            globalClass.scanLeDevice(true);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }else {
                            Log.e(TAG," else timer  mledevice ");
                        }
                    }
                });

            }
        };
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter
    {
        private boolean Status = false;
        //private BluetoothDevice device;
        ViewHolder viewHolder;
        public LeDeviceListAdapter()
        {
            // mLeDevices = new ArrayList<BluetoothDevice>();
            if (mLeDevices.isEmpty()){
                mProgressBarFooter.setVisibility(GONE);
                ble_discoverable.setVisibility(View.VISIBLE);
                ble_not_enabled.setVisibility(View.GONE);
                if (!globalClass.mBluetoothAdapter.isEnabled()) {
                    if(!mLeDevices.isEmpty())
                        mLeDevices.clear();
                    Log.e(TAG,"Adapter in side trigger");
                    ble_not_enabled.setVisibility(View.VISIBLE);
                    mProgressBarFooter.setVisibility(GONE);
                    ble_discoverable.setVisibility(View.GONE);
                }
            }else if(globalClass.mighty_ble_device != null){
                ble_discoverable.setVisibility(View.GONE);
                mProgressBarFooter.setVisibility(View.VISIBLE);
            }

            Log.e(TAG,"size of mLeDevices in side adapter "+mLeDevices.size());
        }

        public void addDevice(BluetoothDevice device) {
            if (device != null) {
                if ((!mLeDevices.contains(device)) && (device.getName() != null)) {
                    Log.e(TAG, " device_name " + device.getName() + " address " + device.getAddress() + "device_object "+device);
                    mLeDevices.add(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            }
        }

        private class ViewHolder {
            TextView deviceName;
            ImageView ble_image;
            ImageView img_plus;
            FrameLayout img_plus_rect;
            TextView text_arrow;
            ProgressBar progressBar;
            SwipeLayout ble_device_swipe;
            ImageView bt_delete;
            Typeface custom_font;
        }

        public void clear() {
            if(!mLeDevices.isEmpty())
                mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = View.inflate(context, R.layout.ble_listitem_device,null);
                viewHolder = new ViewHolder();
                viewHolder.custom_font = Typeface.createFromAsset(context.getAssets(), "serenity-light.ttf");
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.text_arrow = (TextView) view.findViewById(R.id.text_arrow);
                viewHolder.ble_image = (ImageView)view.findViewById(R.id.blue_trick);
                viewHolder.bt_delete = (ImageView)view.findViewById(R.id.bt_delete);
                viewHolder.text_arrow.setVisibility(View.GONE);
                viewHolder.progressBar = (ProgressBar)view.findViewById(R.id.progressBar2);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.img_plus = (ImageView) view.findViewById(R.id.img_plus);
                viewHolder.img_plus_rect = (FrameLayout) view.findViewById(R.id.img_plus_rect);
                viewHolder.ble_device_swipe = (SwipeLayout) view.findViewById(R.id.ble_device_swipe);
                viewHolder.ble_device_swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
                viewHolder.ble_device_swipe.setSwipeEnabled(false);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
         /*   viewHolder.ble_device_swipe.open(true);
            viewHolder.ble_device_swipe.close(true);*/

            viewHolder.deviceName.setTypeface( viewHolder.custom_font);
            viewHolder.text_arrow.setTypeface( viewHolder.custom_font);

            Log.e(TAG,"size of mLeDevices in side adapter get View"+mLeDevices.size() );


            //Some time getting no name
            if(mLeDevices.get(i).getName() != null){
                if(mLeDevices.get(i).getName().length() != 0){
                    viewHolder.deviceName.setText(mLeDevices.get(i).getName());
                    Log.e(TAG,"device Name "+ mLeDevices.get(i).getName()+ " addre "+mLeDevices.get(i).getAddress());
                }
            }else viewHolder.deviceName.setText("Unknown mighty");

            viewHolder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(globalClass.mighty_ble_device != null) {
                        Log.e(TAG, "Global___" + globalClass.mighty_ble_device.getName());
                        startActivity(new Intent(getActivity(), DeviceRenameActivity.class));
                    }
                }
            });

            if(mLeDevices.get(i) == GlobalClass.mighty_ble_device){
                viewHolder.ble_image.setVisibility(View.VISIBLE);
                viewHolder.text_arrow.setTypeface(custom_font);
                bt_listView.setVisibility(View.VISIBLE);
                frame.setVisibility(View.VISIBLE);
                viewHolder.deviceName.setText(globalClass.ble_device_name);
                Log.e(TAG,"devi_ "+ GlobalClass.mighty_ble_device.getName()+ "  "+ globalClass.ble_device_name);
                swipeLayout.setEnabled(true);
                viewHolder.ble_device_swipe.setSwipeEnabled(true);
                viewHolder.text_arrow.setVisibility(View.VISIBLE);
                viewHolder.img_plus_rect.setVisibility(View.GONE);
                viewHolder.img_plus.setVisibility(View.INVISIBLE);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                globalClass.Status = false;
                Log.e(TAG,"if refreshing ");
            }else {
                viewHolder.ble_image.setVisibility(View.GONE);
                viewHolder.text_arrow.setVisibility(View.GONE);
                viewHolder.img_plus.setVisibility(View.VISIBLE);
                viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
                mProgressBarFooter.setVisibility(View.VISIBLE);
                viewHolder.ble_device_swipe.setSwipeEnabled(false);
                Log.e(TAG,"else refreshing ");
                if(globalClass.Status){
                    viewHolder.img_plus.setVisibility(View.INVISIBLE);  //setImageResource(android.R.color.transparent);
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    progressBar_footer.setVisibility(GONE);
                    //mProgressBarFooter.setVisibility(View.GONE);
                    viewHolder.ble_device_swipe.setSwipeEnabled(true);
                }
            }

            viewHolder.img_plus.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //    if(!globalClass.Status) {
                    stoptimertask();
                    //globalClass.scanLeDevice(false)
                    BluetoothDevice device = mLeDevices.get(i);

                    Log.e(TAG, "Devic___" + device.getName());
                    mLeDeviceListAdapter.clear();
                    addDevice(device);

                    mProgressBarFooter.setVisibility(View.VISIBLE);
                    progressBar_footer.setVisibility(GONE);
                    notifyDataSetChanged();
                    globalClass.bleConnect(device);
                    globalClass.Status = true;

                }
            });

            viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mLeDevices.isEmpty()){
                        globalClass.Status = false;
                        final BluetoothDevice device = mLeDevices.get(i);
                        if (device == null) return;
                        BtdeviceListAdapter.clear();
                        if(!ble_hash_map.isEmpty())
                            ble_hash_map.clear();
                        //mBluetoothLeService.disconnect();
                        globalClass.auto_connect_force_cancel = 2;
                        globalClass.bleDisconnect();
                        bt_listView.setVisibility(View.GONE);
                        swipeLayout.setEnabled(false);
                        viewHolder.text_arrow.setVisibility(View.GONE);
                        viewHolder.img_plus.setVisibility(View.VISIBLE);
                        viewHolder.img_plus_rect.setVisibility(View.VISIBLE);
                        viewHolder.progressBar.setVisibility(View.GONE);
                        mProgressBarFooter.setVisibility(View.VISIBLE);
                        progressBar_footer.setVisibility(View.VISIBLE);
                        clear();
                        if(!GlobalClass.hasmap_bt_scan_headset_lists.isEmpty())
                            GlobalClass.hasmap_bt_scan_headset_lists.clear();
                        notifyDataSetInvalidated();
                        globalClass.scanLeDevice(true);
                        globalClass.mighty_ble_device = null;
                    }
                }
            });

            return view;

        }
    }



    /************************ Sending Request to BT_Scan **************************/
    public void send_bt_scan_request()
    {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
        //Set the MessageID to BT_SCAN to Read the Mighty bt_scan device
        mightyMessage.MessageID = 13;
        Log.d(TAG,"Set Bt_scan Request");

        //Send the GET request to Mighty Device, Note:Only Header is Sent.
        tcpClient.SendData(mightyMessage);
        Log.d("Sent Bt_Scan  Request", "Done");
    }

    /************************ Sending Request to BT_Configure **************************/
    public void send_bt_structure()
    {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        //Set the MessageID to Device_Info to Read the Mighty Device Info
        mightyMessage.MessageID = 4;

        Log.d(TAG,"Set BT_Config structure");
        //Send the GET request to Mighty Device, Note:Only Header is Sent.
        tcpClient.SendData(mightyMessage);
        Log.d("Sent BT_Conf structure","Done");
    }

    public void handleMightyMessage(int msgId, int msgType)
    {
        Log.d(TAG, "ble_received Mighty Message ID = " + msgId +" messageType "+msgType);
        if(msgId == 12)
        {
            Log.d(TAG, "Received BT_Scan Status ");

            addBtAccessories();
        }
        if(msgId == 13){
            addBtAccessories();
        }

        if(msgId == 16)
        {
            Events events = globalClass.events_global;
            Log.d(TAG,"BT_Config Status Connected");
        }
        if(msgId == 4){

            if (msgType==102) {
                //After Conneted Bt Device making row in top
                BtdeviceListAdapter.stoptimertask();
                responceFormDevice();
                addBtAccessories();
                if (globalClass.scan_sel_bt_headphone_model != null) {
                    Log.e(TAG,"Enum Handler Before");
                    enum_handler(globalClass.scan_sel_bt_headphone_model.getStatus());
                }
            }
            if (msgType == 201){
                BtdeviceListAdapter.stoptimertask();
                responceFormDevice();
                BtdeviceListAdapter.notifyDataSetChanged();
                globalClass.toastDisplay("Failed to connect BT Accessory");
            }
        }
        if (msgId == 13 | msgId == 4 ){
            if(msgType == 300){
                BtdeviceListAdapter.stoptimertask();
                responceFormDevice();
                BtdeviceListAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
                globalClass.alertDailogSingleText("Please remove the Jack and try again",getActivity());
            }
        }


    }
    public void enum_handler(int enum_value)
    {
        Log.e(TAG,"Enum Handler");
        switch (enum_value)
        {
            case Constants.Pair:
                Log.d(TAG,"BT_CONF_PAIRED");
                BtdeviceListAdapter.notifyDataSetChanged();
                break;
            case Constants.Unpair:
                Log.d(TAG,"BT_CONF_UNPAIRED");
                BtdeviceListAdapter.notifyDataSetChanged();
                break;
            case Constants.Connect:
                Log.d(TAG,"BT_CONF CONNECTED");
                BtdeviceListAdapter.notifyDataSetChanged();
                break;
            case Constants.Disconnect:
                Log.d(TAG,"BT_CONF DISCONNECTED");
                BtdeviceListAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }

    private void responceFormDevice(){
        if(!BT_history.isEmpty()){
            for(int i =0;i<BT_history.size();i++){
                BT_history.get(i).setPlus_button_clickable(false);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG,"onAttach");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated");
    }

    private void addBtAccessories(){
        if(!GlobalClass.hasmap_bt_scan_headset_lists.isEmpty()){
            if(!BT_history.isEmpty()){
                BT_history.clear();
            }
            if(globalClass.scan_sel_bt_headphone_model != null && !globalClass.scan_sel_bt_headphone_model.getMac_id().equals("mac_id")) {
                if (globalClass.scan_sel_bt_headphone_model.getStatus() == 4)
                    globalClass.scan_sel_bt_headphone_model.setStatus(1);
                Log.e(TAG,"globalClass.scan_sel_bt_headphone_model.getStatus() "+globalClass.scan_sel_bt_headphone_model.getStatus()+" "+globalClass.scan_sel_bt_headphone_model.getName());
                if(globalClass.hasmap_bt_scan_headset_lists.get(globalClass.scan_sel_bt_headphone_model.getMac_id()) != null)
                globalClass.hasmap_bt_scan_headset_lists.get(globalClass.scan_sel_bt_headphone_model.getMac_id()).setStatus(globalClass.scan_sel_bt_headphone_model.getStatus());
            }
            for ( Map.Entry<String, Headset_History> entry1 : GlobalClass.hasmap_bt_scan_headset_lists.entrySet()) {
                String key = entry1.getKey();
                Log.e(TAG,"mac id "+entry1.getValue().getStatus());
                BT_history.add(entry1.getValue());
            }
            //Sorting array in decending order
            Collections.sort(BT_history,Headset_History.statusComparator);
            for(Headset_History mydata :BT_history){
                Log.e(TAG,"mydata "+mydata.getName()+" "+mydata.getStatus());
            }
        }
        if(BT_history.size() >= 3){
            //  bt_swipe_down.setVisibility(View.INVISIBLE);
            bt_plus_tap.setVisibility(View.GONE);
        }else
            bt_plus_tap.setVisibility(View.VISIBLE);

        BtdeviceListAdapter.notifyDataSetChanged();
    }

}