package mightyaudio.ble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import mightyaudio.core.GlobalClass;

public abstract class MightyMsgReceiver  {

    public abstract void onConnected();
    public abstract void onDiscovered();
    public abstract void onDisconnected();
    GlobalClass globalClass =GlobalClass.getInstance();

   // public abstract void onReceiveMessage(int msgId, int msgType);

    public void init()
    {
    }

    public MightyMsgReceiver(Context context)
    {
  //     context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void RegisterReceiver(Context context)
    {
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unRegisterReceiver(Context context)
    {
        context.unregisterReceiver(mGattUpdateReceiver);
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String TAG =MightyMsgReceiver.class.getSimpleName();
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                    onConnected();
                    globalClass.action_send = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    onDisconnected();
                    globalClass.action_send = true;
                    Log.e(TAG, "printing :");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    onDiscovered();
                    globalClass.action_send = true;
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                byte[] byte_array = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
               // int msgID = intent.getIntExtra(BluetoothLeService.EXTRA_MSG_ID,-1);
               // int msgType = intent.getIntExtra(BluetoothLeService.EXTRA_MSG_TYPE, -1);
                //Log.e(TAG,"printing :" +msgID +" msgType "+ msgType +"action "+ action);
                //onReceiveMessage(msgID,msgType);
            }
        }
    };
}