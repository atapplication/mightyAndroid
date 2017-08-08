package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Headset_Status extends MightyObject
{
    public int HeadsetTypeConnected;
    public int HeadsetConnectionStatus;

    public int getHeadsetTypeConnected() {
        return HeadsetTypeConnected;
    }

    public void setHeadsetTypeConnected(int headsetTypeConnected) {
        HeadsetTypeConnected = headsetTypeConnected;
    }

    public int getHeadsetConnectionStatus() {
        return HeadsetConnectionStatus;
    }

    public void setHeadsetConnectionStatus(int headsetConnectionStatus) {
        HeadsetConnectionStatus = headsetConnectionStatus;
    }

    public Headset_Status() {
        HeadsetTypeConnected = 87;
        HeadsetConnectionStatus = 76;

    }

    public Headset_Status(int headsetTypeConnected, int headsetConnectionStatus) {
        HeadsetTypeConnected = headsetTypeConnected;
        HeadsetConnectionStatus = headsetConnectionStatus;
    }
}
