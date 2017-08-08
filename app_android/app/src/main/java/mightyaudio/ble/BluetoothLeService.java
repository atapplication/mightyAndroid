package mightyaudio.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.msgpack.core.MessageTypeException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import mightyaudio.activity.MightySoftwareUpdateActivity;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.SpotifySessionManager;
import mightyaudio.receiver.ConnectivityReceiver;

import static mightyaudio.TCP.Constants.MSG_BIT_RATE_MODE;
import static mightyaudio.TCP.Constants.MSG_ID_APP_INFO_T;
import static mightyaudio.TCP.Constants.MSG_ID_BATTERY_INFO_ID;
import static mightyaudio.TCP.Constants.MSG_ID_DEBUG_FEATURE;
import static mightyaudio.TCP.Constants.MSG_ID_DEVICEINFO_ID;
import static mightyaudio.TCP.Constants.MSG_ID_EVENTS_ID;
import static mightyaudio.TCP.Constants.MSG_ID_HEADSET_HISTORY_ID;
import static mightyaudio.TCP.Constants.MSG_ID_MEMORY_ID;
import static mightyaudio.TCP.Constants.MSG_ID_MIGHTY_LOGIN_ID;
import static mightyaudio.TCP.Constants.MSG_ID_PLAYLIST_ID;
import static mightyaudio.TCP.Constants.MSG_ID_SPOTIFY_LOGIN_ID;
import static mightyaudio.TCP.Constants.MSG_WIFI_SCAN_LIST_ID;
import static mightyaudio.TCP.Constants.MSG_WIFI_STATUS_ID;

public class BluetoothLeService extends Service
{
    public final static String TAG = BluetoothLeService.class.getSimpleName();
    BluetoothDevice mighty_ble_device;
    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    public String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;        //  BluetoothGatt mBluetoothGatt;  30-09-16
    public int mConnectionState = STATE_DISCONNECTED;
    GlobalClass globalClass =GlobalClass.getInstance();
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    List<byte[]> blocks = new ArrayList<>();
    MightyMessage mightyMessage = new MightyMessage();
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    private boolean isConnected;
    private MightyMessage recieveMsg;
    private final int setGetTotalPriority = 12;
    private int setGetExceting = 1;
    private boolean avoid_previous_response = false;
    String value;
    byte[] byte_array;
    private Handler handler;
    final Handler handler_timer = new Handler();
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    List<BluetoothGattService> mightyGattServices ;
    Handler mHandler;

    private SharedPreferences spotifyPref;
    byte[] data;
    int chunksize = 400; //400 byte chunk
    int packetSize;
    boolean playlist_send = false;
    byte[][] packets = new byte [packetSize][chunksize];
    int packetInteration;
    Integer start;
    String eom = "EOM";
    byte[] byteData = eom.getBytes();
    public static int split_flag = 0;
    BluetoothDevice device;
    private List<BluetoothGattCharacteristic> gattCharacteristics  = new ArrayList<BluetoothGattCharacteristic>();
    BluetoothGattService mCustomService;
    BluetoothGattCharacteristic mWriteCharacteristic_204;
    BluetoothGattCharacteristic mWriteCharacteristic_203;
    BluetoothGatt mighty_dummy_object;
    boolean  discovered = false ;
    private static Timer bletimer= new Timer();
    private static TimerTask bletimerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        Log.d(TAG, "Service Started");
        spotifyPref = getApplicationContext().getSharedPreferences(SpotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);
    }

    //Timer for Ble_Connection_Timeout

    public void startTimer() {
        initializeTimerTask();
        if (bletimer ==null)
            bletimer = new Timer();
        bletimer.schedule(bletimerTask, 30000, 30000);
    }

    public void stoptimertask() {
        //stop the bletimer, if it's not already null
        if (bletimer != null) {
            bletimer.cancel();
            // bletimer.purge();
            bletimer = null;
            if (bletimerTask != null){
                bletimerTask.cancel();
            }
        }
    }
    public void initializeTimerTask() {
        bletimerTask = new TimerTask() {
            public void run() {
                handler_timer.post(new Runnable() {
                    public void run() {
                        Log.e(TAG, "Timer_called ");
                        // if(globalClass.mighty_ble_device == null){
                        disconnect(mBluetoothGatt);
                        //}
                    }
                });
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    public boolean connect(final String address) {
        startTimer();
        //globalClass.scanLeDevice(false);
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        device = mBluetoothAdapter.getRemoteDevice(address);
        Log.e(TAG, "Last_call " + device.getAddress() + " Last name" + device.getName());
        globalClass.ble_device_name = device.getName();
        mighty_ble_device = device;
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        if (mBluetoothGatt != null) {
            Log.e(TAG, " bluetoothgatt is not null ");
            close(mBluetoothGatt);
        }
        mBluetoothGatt = null;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e(TAG, "Gatt_connection" + globalClass.isFromMainThread());
                    mBluetoothGatt = device.connectGatt(globalClass, false, mGattCallback, BluetoothDevice.TRANSPORT_AUTO);
                    Log.e(TAG, " marsh");
                } else {
                    mBluetoothGatt = device.connectGatt(globalClass, false, mGattCallback);
                    Log.e(TAG, " lollipop");
                }
                // Log.e(TAG,"Refreshdata "+refreshDeviceCache(mBluetoothGatt));
                mBluetoothGatt.requestConnectionPriority(1);
                Log.e(TAG, "Trying to create a new connection." );

                if (mBluetoothGatt != null)
                    mBluetoothGatt.connect();

                mBluetoothDeviceAddress = address;
                mConnectionState = STATE_CONNECTING;
            }
        }, 1000);

        return true;
    }
    // connection change and services discovered.
    public BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED ) {
                if(mighty_ble_device == null)
                    disconnect(gatt);
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.e(TAG,"Gatt_callback_thread" +globalClass.isFromMainThread());
                Log.i(TAG, "Connected to GATT server. "+status +" newstate "+ newState+ " Main thread "+globalClass.isFromMainThread());

                try {
                    Thread.sleep(500);
                    mBluetoothGatt.discoverServices();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if (status != BluetoothGatt.GATT_SUCCESS) {
                if (status != 133) {
                    disconnect(gatt);
                    Log.d(TAG, "Connection error GATT server");
                }else {
                    refreshDeviceCache(gatt);
                    gatt.disconnect();
                    gatt.close();
                    mBluetoothGatt = null;
                    try{
                        Thread.sleep(200);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBluetoothGatt = device.connectGatt(globalClass, false, mGattCallback, BluetoothDevice.TRANSPORT_AUTO);
                            Log.e(TAG, " marsh");
                        } else {
                            mBluetoothGatt = device.connectGatt(globalClass, false, mGattCallback);
                            Log.e(TAG, " lollipop");
                        }
                        mBluetoothGatt.requestConnectionPriority(1);
                        mBluetoothGatt.connect();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                clearMightyCache();
                Log.i(TAG, "Disconnected from GATT server.");
                //disconnect(gatt);
                globalClass.mighty_ble_device = null;
                globalClass.device_info = null;
                if (mBluetoothGatt != null) {
                    //mBluetoothGatt.requestConnectionPriority(0);
                    mBluetoothGatt.disconnect();
                    mighty_dummy_object = mBluetoothGatt;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "onServicesDiscovered received: " + status);
                gattCharacteristics = gatt.getServices().get(0).getCharacteristics();

                if(mighty_ble_device == null)
                    disconnect(gatt);

                try {
                    mBluetoothGatt.setCharacteristicNotification(gattCharacteristics.get(0), true);
                    mBluetoothGatt.setCharacteristicNotification(gattCharacteristics.get(1), true);
                    Log.d(TAG, "Discovered GATT Characteristics 0 = " + gattCharacteristics.get(0).getUuid());
                    Log.d(TAG, "Discovered GATT Characteristics 1 = " + gattCharacteristics.get(1).getUuid());
                    mCustomService = mBluetoothGatt.getService(UUID.fromString("0000a200-0000-1000-8000-00805f9b34fb"));

                    if(mCustomService == null)
                    {
                        Log.w(TAG, "Custom BLE Service not found");
                        disconnect(mBluetoothGatt);
                    }

                    /*get the read characteristic from the service*/
                    mWriteCharacteristic_204 = mCustomService.getCharacteristic(UUID.fromString("0000a204-0000-1000-8000-00805f9b34fb"));
                    mWriteCharacteristic_203 = mCustomService.getCharacteristic(UUID.fromString("0000a203-0000-1000-8000-00805f9b34fb"));

                }catch (Exception e) {
                    e.printStackTrace();
                }

/*                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "discovered_");

                    }
                },300);*/
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
                //    mBluetoothGatt.discoverServices();
                disconnect(gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Received_data in gatt : " + characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            Log.e(TAG," size_before "+ packetInteration + " packets "+ packetSize+ " jay_value "+split_flag);
            if (packetInteration < packetSize) {
                Log.e(TAG,"split is send");
                mWriteCharacteristic_204.setValue(packets[packetInteration]);
                mWriteCharacteristic_204.setWriteType(2);
                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_204);
                packetInteration++;
                if(packetInteration == packetSize)
                    split_flag =1;
                else
                    split_flag = 0;
            }
            else if (packetInteration == packetSize && split_flag == 1) {
                Log.e(TAG, "EOM is send");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mWriteCharacteristic_204.setValue(byteData);
                mWriteCharacteristic_204.setWriteType(2);
                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_204);
                //  mBluetoothGatt.requestConnectionPriority(0);
                split_flag = 0;
            }

            Log.e(TAG," size_after "+ packetInteration + " packets "+ packetSize + " jay_value "+split_flag);

            if(status ==0 ){
                if( setGetExceting != setGetTotalPriority && !globalClass.diffrent_user_block && !globalClass.lower_version){
                    ++setGetExceting;
                    set_get_usingThread(setGetExceting);
                }
                Log.e(TAG,"Writing was Successfull");
            }else {
                Log.e(TAG," setGetExceting Writing fail "+ status+" "+setGetExceting);
                mWriteCharacteristic_203.setValue(String.valueOf(characteristic));
                mWriteCharacteristic_203.setWriteType(2);
                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_203);

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,final BluetoothGattCharacteristic characteristic)
        {
            data = characteristic.getValue();
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    public void broadcastUpdate(final String action)
    {
        Log.e(TAG,"action in broadcast "+action);

        if(action.equals("com.example.bluetooth.le.ACTION_GATT_CONNECTED")) {
            globalClass.action_send = false;
            final Intent intent1 = new Intent("com.example.bluetooth.le.ACTION_GATT_CONNECTED");
            globalClass.sendBroadcast(intent1);
        }

        if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            globalClass.action_send = false;
            globalClass.setgetplaylist = false;
            globalClass.softwareupdateTrriger = false;
            if(mighty_ble_device == null)
                disconnect(mBluetoothGatt);

            if(!globalClass.mighty_playlist.isEmpty())
                globalClass.mighty_playlist.clear();

            globalClass.diffrent_user_block = false;
            globalClass.lower_version = false;
            avoid_previous_response = false;
            setGetExceting =1;
            globalClass.mightyLogin.setLogin_mode(0);
            globalClass.mighty_logout_status = false;
            globalClass.download_error = false;
            if(!GlobalClass.wifi_lists_global.isEmpty())
                GlobalClass.wifi_lists_global.clear();

            TCPClient tcpClient = new TCPClient();
            mightyMessage.MessageType = Constants.MSG_TYPE_SET;
            mightyMessage.MessageID = MSG_ID_APP_INFO_T;
            tcpClient.SendData(mightyMessage);
        }
        if (ACTION_GATT_DISCONNECTED.equals(action)) {
            Intent ble_disconnect = new Intent();
            ble_disconnect.setAction("ble.disconnect.successful");
            globalClass.sendBroadcast(ble_disconnect);
            globalClass.action_send = false;
            globalClass.setgetplaylist = false;
            if(!globalClass.mighty_playlist.isEmpty())
                globalClass.mighty_playlist.clear();
            Log.e(TAG,"Disconnected_called_service_class");
            stoptimertask();
            playlist_send = false;
            setGetExceting =1;
            if(data != null )
                data = null;
            if(value != null)
                value = null;
            blocks.clear();
            globalClass.lower_version = false;
            globalClass.diffrent_user_block = false;
            if (mBluetoothGatt != null)
                mBluetoothGatt.disconnect();
            if(byte_array != null)
                byte_array = null;
            packetInteration = 0;
            chunksize = 0;
            packetSize = 0;
            start = 0;
            playlist_send = false;
            split_flag = 0;
            for(int i =0;i<gattCharacteristics.size();i++) {
                gattCharacteristics.get(i).getService().getCharacteristics().clear();
            }
            if(mightyGattServices != null)
                mightyGattServices.clear();
            globalClass.autoConnectedTrigger = false;
            globalClass.wifi_connected_global = null;
            globalClass.upgradeProcess = false;
            globalClass.scanLeDevice(true);
            if(!gattCharacteristics.isEmpty())
                gattCharacteristics.clear();
            if(bluetoothGattCharacteristic != null) {
                bluetoothGattCharacteristic.getDescriptors().clear();
                bluetoothGattCharacteristic.getService().getCharacteristics().clear();
            }
            if(globalClass.mighty_ble_device != null)
                globalClass.mighty_ble_device = null;
            final Intent intent = new Intent(action);
            globalClass.sendBroadcast(intent);
        }

    }


    public void broadcastUpdate(final String action,final BluetoothGattCharacteristic characteristic)
    {
        // String value = new String(data);
        //final byte[] data = characteristic.getValue();
        String value = new String(data);
        Log.e(TAG,"String_value" + value);
        if(value.equals("EOM"))
        {
            byte[] byte_array =  concatenateByteArrays(blocks);
            Log.d(TAG, "Received EOM, Message Size = "+byte_array.length);
            try {
                TCPClient tcpClient = new TCPClient();
                recieveMsg = tcpClient.ReceiveData(byte_array);
            }catch (MessageTypeException e){
                e.printStackTrace();
                Log.e(TAG,"Packing and Unpacking Exception");
            }

            if(recieveMsg == null) {
                Log.e(TAG, "Received INVALID message from Mighty");
                blocks.clear();
                return;
            }
            int msgid = recieveMsg.MessageID;
            int msgtype = recieveMsg.MessageType;
            Log.e(TAG,"switch case trigger "+msgid+" "+msgtype);
            switch (msgid){

                case MSG_ID_APP_INFO_T :
                    //BLE Authorization
                    if(msgtype == 303)
                        disconnect(mBluetoothGatt);

                    if (msgtype == 200) {
                        globalClass.mighty_ble_device = mighty_ble_device;
                        TCPClient tcpClient = new TCPClient();
                        final Intent intent = new Intent("com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED");
                        globalClass.sendBroadcast(intent);
                        globalClass.ble_status = true;
                        stoptimertask();
                        //mBluetoothGatt.requestConnectionPriority(0);
                        // Set Device Info
                        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                        mightyMessage.MessageID = MSG_ID_DEVICEINFO_ID;
                        tcpClient.SendData(mightyMessage);
                    }
                    break;
                case MSG_ID_DEVICEINFO_ID :
                    if (msgtype == 202) {
                        if(globalClass.device_info.SW_Version.equals("UPDATING")){
                            Intent intent = new Intent(getApplicationContext(), MightySoftwareUpdateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            blocks.clear();
                        }
                        try {
                            if (0.70f < Float.valueOf(globalClass.device_info.getSW_Version())) {
                                Log.e(TAG, "Condition_true");
                                isConnected = ConnectivityReceiver.isConnected();
                                if (isConnected) {
                                    Log.e(TAG, "mighty Registration top" + globalClass.upgradeProcess);
                                    globalClass.mightyRegistration("BluetoothService");
                                } else {
                                    if (Float.valueOf(globalClass.device_info.getSW_Version()) < 0.80f) {
                                        disconnect(mBluetoothGatt);
                                        globalClass.auto_connect_force_cancel = 2;
                                        Intent compatibility_inten = new Intent();
                                        compatibility_inten.setAction("mighty.hardware.compatibility");
                                        globalClass.sendBroadcast(compatibility_inten);
                                    }
                                }
                                // Set Mighty Info
                                TCPClient tcpClient = new TCPClient();
                                mightyMessage.MessageType = Constants.MSG_TYPE_SET;
                                mightyMessage.MessageID = MSG_ID_MIGHTY_LOGIN_ID;
                                tcpClient.SendData(mightyMessage);
                                globalClass.diffrent_user_block = true;
                            } else {
                                Log.e(TAG, "Condition_false");
                                globalClass.lower_version = true;
                                getReqLowerVersion();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_ID_MIGHTY_LOGIN_ID :
                    if(msgtype == 200) {
                        Log.d(TAG,"Get_Infos_from");
                        globalClass.diffrent_user_block = false;
                        getMightyBasicInfos();

                    }else {
                        Log.e(TAG,"not allow"+msgtype);
                        globalClass.auto_connect_force_cancel = 2;
                        globalClass.diffrent_user_block = true;
                    }
                    break;
                case MSG_WIFI_STATUS_ID :
                    if(msgtype == 202) {
                        Log.e(TAG,"mighty Registration second"+globalClass.upgradeProcess+" "+globalClass.softwareupdateTrriger);
                        isConnected = ConnectivityReceiver.isConnected();
                        if(globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null &&  !globalClass.wifi_connected_global.ap_name.equals("") && isConnected && !globalClass.softwareupdateTrriger) {
                            if(globalClass.upgradeProcess){
                                Log.e(TAG,"mighty Registration third"+globalClass.upgradeProcess);
                                Log.e(TAG, "Last line Call Every things done in method");
                                globalClass.softwareUpdateChecked(null);
                            }else{
                                Log.e(TAG,"mighty Registration four");
                                globalClass.mightyRegistration("LaunchTab");
                            }
                        }
                    }
                    break;
                case MSG_ID_EVENTS_ID:
                    if(globalClass.events_global.getWiFi_status() == 9){
                        //  globalClass.wifi_connected_global = null;
                        //globalClass.wifi_status=false;
                        // get wifi scan list Info
                        TCPClient tcpClient = new TCPClient();
                        mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                        mightyMessage.MessageID = MSG_WIFI_SCAN_LIST_ID;
                        tcpClient.SendData(mightyMessage);
                    }
                    break;
                case MSG_ID_DEBUG_FEATURE:
                    if(msgtype == 202){
                        mBluetoothGatt.requestConnectionPriority(1);
                    }
            }

            Intent ble_receive = new Intent();
            ble_receive.setAction("ble.receive.successful");
            ble_receive.putExtra("msgid",msgid);
            ble_receive.putExtra("msgtype",msgtype);
            globalClass.sendBroadcast(ble_receive);
            Log.e(TAG, "Receive_data in msgID "+ recieveMsg.MessageID + " msgtype" + recieveMsg.MessageType);
            blocks.clear();
        }else{
            Log.d(TAG, "EOM Else Condition");
            blocks.add(data);
        }
    }


    public  void getReqLowerVersion(){
        Log.e(TAG,"Call getReqLowerVersion");
        mightyMessage.Token = "AndroidClient";
        new Thread(new Runnable() {
            @Override
            public void run() {
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_BATTERY_INFO_ID;
                sendMessage(Thread.currentThread(), mightyMessage);

                //* Get Wifi Info *//*
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_WIFI_STATUS_ID;
                sendMessage(Thread.currentThread(), mightyMessage);

                Log.e(TAG,"Call getReqLowerVersion finish");
                //* Get Events Info *//*
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_EVENTS_ID;
                sendMessage(Thread.currentThread(), mightyMessage);

            }
        }).start();
    }

    public byte[] concatenateByteArrays(List<byte[]> blocks) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (byte[] b : blocks) {
            os.write(b, 0, b.length);
        }
        return os.toByteArray();
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            Log.e(TAG,"Service Started ");
            return BluetoothLeService.this;

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public final IBinder mBinder = new LocalBinder();

    public boolean initialize()
    {
        Log.d(TAG,"Initializing");
        //globalClass.mBluetoothLeService_global = this;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public void disconnect(BluetoothGatt gatt)
    {
        if (mBluetoothAdapter == null ) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        close(gatt);
        globalClass.scanLeDevice(true);
        String intentAction;
        globalClass.Status = false;
        intentAction = ACTION_GATT_DISCONNECTED;
        mConnectionState = STATE_DISCONNECTED;
        globalClass.wifi_connected_global = null;
        Log.i(TAG, "Disconnected from GATT server_________________.");
        if(globalClass.mighty_ble_device != null)
            globalClass.mighty_ble_device = null;
        //refreshDeviceCache(gatt);
        mighty_ble_device = null;
        broadcastUpdate(intentAction);

    }
    public void close(BluetoothGatt gatt)
    {
        refreshDeviceCache(gatt);
        if (mBluetoothGatt == null) {
            Log.w(TAG, "DISCONNECTED BLE GATT DEVICE !!!!!!___________null");
            return;
        }
        else  if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            Log.e(TAG, " Connection_closed " + mBluetoothGatt);
            if(mBluetoothGatt != null)
                mBluetoothGatt.close();
        }
    }
    public int writeCustomCharacteristic(byte[] value)    //   <<------------------------
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return -10;
        }
        Log.e(TAG," Packets "+ packets.length + " value "+value.length + " packetsize "+packetSize);
        if(value.length <= 500){
            try {
                mWriteCharacteristic_203.setValue(value);
                mWriteCharacteristic_203.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_203);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(!mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_203))
            {
                Log.w(TAG, "Failed to write characteristic" + mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_203));
                return -1;
            }
        }
        else {
            //mBluetoothGatt.requestConnectionPriority(1);
            chunksize = 500;
            packetSize = (int) Math.ceil(value.length / (double) chunksize);
            Log.e(TAG, " Packets_else " + packets.length + " value " + value.length + " packetsize " + packetSize + " chunksize " + chunksize);
            if (packets != null)
                packets = null;
            packets = new byte[packetSize][chunksize];
            packetInteration = 0;
            start = 0;
            for (int i = 0; i < packets.length; i++) {
                int end = start + chunksize;
                if (end > value.length) {
                    end = value.length;
                }
                packets[i] = Arrays.copyOfRange(value, start, end);
                start += chunksize;
            }
            mWriteCharacteristic_204.setValue(packets[packetInteration]);
            mWriteCharacteristic_204.setWriteType(2);
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_204);
            packetInteration++;
            split_flag = 1;
            Log.e(TAG, " Size___if" + packetInteration + " packsize " + packetSize + " jay_value_if " + split_flag);
            if (!mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_204)) {
                Log.w(TAG, "Failed to write characteristic" + mBluetoothGatt.writeCharacteristic(mWriteCharacteristic_204));
                return -1;
            }
        }

        return 0;
    }

    public void getMightyBasicInfos()
    {
        //getReqLowerVersion();
        setGetExceting = 4;
        set_get_usingThread(setGetExceting);
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                if(globalClass.mighty_ble_device != null && !globalClass.diffrent_user_block){
                    if(setGetExceting != setGetTotalPriority){
                        set_get_usingThread(setGetExceting);
                        Log.e(TAG,"Handeler Triger");
                    }
                }
            }
        }, 15000);
    }

    public void clearMightyCache()
    {
        globalClass.mighty_playlist.clear();
        globalClass.battery_info.setAvailablePercentage(0);
        globalClass.wifi_status = false;
        globalClass.wifi_status_global.setSsid("");
    }

    public void sendMessage(Thread thread, MightyMessage mightyMessage)
    {
        TCPClient tcpClient = new TCPClient();
        int retry = 1;
        try {
            for(int i=0 ; i< retry; i++) {
                if (-1 == tcpClient.SendData(mightyMessage)) {
                    Log.e(TAG, "Failed sending. Retrying count : " + i+" tcp"+tcpClient.SendData(mightyMessage));
                    thread.sleep(500, 0);
                }
                else {
                    return;
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void set_get_usingThread(int bleSequence){
        Log.e(TAG,"SetGet Method Call "+bleSequence);
        mightyMessage.Token = "AndroidClient";
        Log.e(TAG,"Thread Check "+globalClass.isFromMainThread());
        final TCPClient tcpClient = new TCPClient();
        switch(bleSequence){
            case 4:

                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_BATTERY_INFO_ID;
                tcpClient.SendData(mightyMessage);

                break;
            case 5:

                //* Get Wifi Info *//*
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_WIFI_STATUS_ID;
                tcpClient.SendData(mightyMessage);

                break;
            case 6:

                Log.e(TAG,"Call getReqLowerVersion finish");
                //* Get Events Info *//*
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_EVENTS_ID;
                tcpClient.SendData(mightyMessage);

                break;
            case 7:

                 /* Get Headset_history */
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_HEADSET_HISTORY_ID;
                tcpClient.SendData(mightyMessage);


                break;
            case 8:

                /* Get Memory Info */
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_ID_MEMORY_ID;
                tcpClient.SendData(mightyMessage);

                break;
            case 9:

                /* Get Bit_rate_mode */
                mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                mightyMessage.MessageID = MSG_BIT_RATE_MODE;
                tcpClient.SendData(mightyMessage);

                break;
            case 10:
                Log.e(TAG,"(globalClass.spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)"+ spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false));
                if(spotifyPref.getBoolean(SpotifySessionManager.IS_LOGIN,false)){
                    //   Set Spotify Info
                    mightyMessage.MessageType = Constants.MSG_TYPE_SET;
                    mightyMessage.MessageID = MSG_ID_SPOTIFY_LOGIN_ID;
                    tcpClient.SendData(mightyMessage);
                }else {
                    //Get playlist
                    mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                    mightyMessage.MessageID = MSG_ID_PLAYLIST_ID;
                    tcpClient.SendData(mightyMessage);
                    playlist_send = true;
                }
                break;
            case 11:
                if(playlist_send == false) {
                    mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                    mightyMessage.MessageID = MSG_ID_PLAYLIST_ID;
                    tcpClient.SendData(mightyMessage);
                    playlist_send = true;
                }else{
                    Log.e(TAG,"get_playlist ");
                    mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                    mightyMessage.MessageID = MSG_WIFI_SCAN_LIST_ID;
                    tcpClient.SendData(mightyMessage);
                    playlist_send = false;
                }
                mBluetoothGatt.requestConnectionPriority(0);
                break;

            case 12:
                Log.e(TAG,"Wifi_Scan_list_triggered");
                if(playlist_send == true) {
                    mightyMessage.MessageType = Constants.MSG_TYPE_GET;
                    mightyMessage.MessageID = MSG_WIFI_SCAN_LIST_ID;
                    tcpClient.SendData(mightyMessage);
                }
                break;
        }


    }

    public boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
/*            if(gatt != null)
                gatt.close();*/
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e(TAG, "An exception occured while refreshing device");
        }
        return false;
    }
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BluetoothLeService was destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG,"Stop Service");
        stopSelf();
        //stopForeground(true);
        //stopService(rootIntent);
    }

}
