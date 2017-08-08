package mightyaudio.TCP;

import android.util.Log;

import org.msgpack.core.MessageTypeException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import mightyaudio.core.GlobalClass;

/**
 * Created by ashok on 07-06-2016.
 */


public class TCPClient
{
    Socket mightySocket;
    InputStream mightyInputStream;
    OutputStream mightyOutputStream;
    MightyMessage mightyMessage;
    private static final String TAG = TCPClient.class.getSimpleName();

    GlobalClass globalClass = GlobalClass.getInstance();

    public MightyMessage ReceiveData()
    {
        MightyMessage mightyMessage;

        byte[] inputBytesArray = new byte[1024];

        mightyMessage = Serializer.UnPacker(inputBytesArray);
        if(mightyMessage == null)
        {
            Log.e("Mighty", "Error unpacking Stream");
            return null;
        }


        switch (mightyMessage.MessageID)
        {
            case Constants.MSG_ID_DEVICEINFO_ID:
                Log.d("Mighty","Received Mighty Info");
                break;
            default:
                break;

        }
        return mightyMessage;
    }


    public MightyMessage ReceiveData(byte[] byte_array)
    {
        byte[] inputBytesArray = new byte[1024];

        try {
            mightyMessage = Serializer.UnPacker(byte_array);
            System.out.println("Receiving data......... inside mightyaudio.TCP");

            if(mightyMessage == null)
            {
                Log.e("Mighty", "Error unpacking Stream");
                return null;
            }
            switch (mightyMessage.MessageID)
            {
                case Constants.MSG_ID_DEVICEINFO_ID:
                    Log.d(TAG,"Received Mighty Info");
                    break;
                default:
                    break;

            }
        }catch (MessageTypeException e){
            Log.e(TAG,"Packing UnPacking");
            e.printStackTrace();
        }

        return mightyMessage;
    }
    public int SendData(MightyMessage mightyMessage)
    {
        byte[] outByteArray = Serializer.Packer(mightyMessage);
        try {
       //     mightyOutputStream.write(outByteArray);
            if(globalClass.mBluetoothLeService_global != null)
                return globalClass.mBluetoothLeService_global.writeCustomCharacteristic(outByteArray);

            System.out.println("Sending data......... inside mightyaudio.TCP");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*public Socket Connect(String ipaddress, int port) throws Exception
    {
        mightySocket = new Socket(ipaddress,port);

        mightyInputStream = mightySocket.getInputStream();
        mightyOutputStream = mightySocket.getOutputStream();

        return mightySocket;
    }

    // Added 04/07/2016----------------

    public void DisConnect()
    {
        try {
            mightySocket.close();










        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }*/
  //----------------------------------
    public TCPClient()
    {

    }
}