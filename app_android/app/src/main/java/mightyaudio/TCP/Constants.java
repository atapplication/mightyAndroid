package mightyaudio.TCP;

/**
 * Created by ashok on 07-06-2016.
 */
public abstract class Constants {
    //  static final int MSG_ID_WIFI_PROVISIONING = 1;
    //   static final int MSG_ID_MIGHTY_INFO = 0;
    public static final int MSG_TYPE_GET = 100;
    public static final int MSG_TYPE_SET = 101;
    public static final int MSG_TYPE_NOTIFICATION = 102;

    public static final int MSG_ID_DEVICEINFO_ID = 0;
    public static final int MSG_ID_WIFICONFIGURATION_ID = 1;
    public static final int MSG_ID_WIFI_STATIC_NETWORK_ID = 2;
    public static final int MSG_ID_WIFI_HISTORY_ID = 3;
    public static final int MSG_ID_BT_CONFIGURATION_ID = 4;
    public static final int MSG_ID_BATTERY_INFO_ID = 5;
    public static final int MSG_ID_PLAYLIST_ID = 6;
    public static final int MSG_ID_DOWNLOAD_ID = 7;
    public static final int MSG_ID_VOICE_OVER_ID = 8;
    public static final int MSG_ID_POWER_ID = 9;
    public static final int MSG_ID_MEMORY_ID = 10;
    public static final int MSG_ID_HEADSET_STATUS_ID = 11;
    public static final int MSG_ID_HEADSET_HISTORY_ID = 12;
    public static final int MSG_ID_BT_SCAN_LIST_ID = 13;
    public static final int MSG_ID_SPOTIFY_LOGIN_ID = 14;
    public static final int MSG_ID_MIGHTY_LOGIN_ID = 15;
    public static final int MSG_ID_EVENTS_ID = 16;
    public static final int MSG_ID_APP_INFO_T = 17;
    public static final int MSG_ID_HEADSET_DISCONNECT = 18;
    public static final int MSG_ACTIVITY_MATRIX_ID = 19;
    public static final int MSG_WIFI_STATUS_ID = 20;
    public static final int MSG_FIRMWARE_UPGRADE_ID = 21;
    public static final int MSG_FIRMWARE_DL_PROGRESS_ID = 22;
    public static final int MSG_WIFI_SCAN_LIST_ID = 23;
    public static final int MSG_BIT_RATE_MODE = 24;
    public static final int MSG_ID_DEBUG_FEATURE = 25;





    /*********************
     * BLE_WIFI_STATUS
     **********************/

    public static final int WIFI_CONNECTED = 1;
    public static final int WIFI_DISCONNECTED = 2;
    public static final int WIFI_WRONG_PASSWORD = 3;
    public static final int WIFI_SHORT_PASSWORD = 4;
    public static final int WIFI_CONN_FAILED = 5;
    public static final int WIFI_AUTH_NOT_VALID = 6;
    public static final int WIFI_TIMEOUT = 7;
    public static final int WIFI_TERMINATE = 8;
    public static final int WIFI_TEMP_DISABLED = 9;
    public static final int WIFI_SSID_NOT_FOUND = 10;
    public static final int WIFI_SCAN_FAILED = 11;
    public static final int WIFI_CONNECTING = 12;

    /**************************************************************/
    /******* BT_Headset Status **********/

    public static final int None = 0;
    public static final int Pair = 1;
    public static final int Unpair = 2;
    public static final int Connect = 3;
    public static final int Disconnect = 4;

    /**************************************************************/
    /************** Firmware_Upgrade_Status ******/
    public static final int FW_LAST_FIRMWARE_SUCCESS = 0;
    public static final int FW_LAST_FIRMWARE_FAILED = 1;
    public static final int FW_DOWNLOAD_ERROR = 2;
    public static final int FW_DISK_FULL_ERROR = 3;
    public static final int FW_NETWORK_ERROR = 4;
    public static final int FW_ROLLBACK_ERROR =5;

    /*********** Firmware_Download_Progress *******/

    public static final int FW_DL_IN_PROGRESS = 0;
    public static final int FW_DL_IN_STALLED = 1;
    public static final int FW_DL_COMPLETED = 2;
    public static final int FW_DL_FAILED = 3;

    /*********** MSG_BIT_RATE_MODE *******/

    public static final int BIT_RATE_NORMAL = 0;
    public static final int BIT_RATE_HIGH = 1;
    public static final int BIT_RATE_EXTREME = 2;
    public static final int BIT_RATE_UNKNOWN = 3;

    /************** Browse_Playlist_Download_Status ***********/
    public static final int DL_NONE = 0;
    public static final int DL_COMPLETED = 1;
    public static final int DL_COMPLETED_WITH_ERROR = 2;
    public static final int DL_IN_PROGRESS =3;
    public static final int DL_FAILED = 4;
    public static final int DL_REFRESH_IN_PROGRESS = 5;
    public static final int DL_REFRESH_COMPLETED = 6;
    public static final int DL_REFRESH_COMPLETED_WITH_ERROR = 7;
    public static final int DL_WIFI_ERROR =8;
    public static final int DL_SPOTIFY_LOGIN_ERROR= 9;
    public static final int DL_FAILED_STORAGE_ERROR = 10;
    public static final int DL_ABOUT_TO_START = 11;
    public static final int DL_SP_PREFETCH_TIMEOUT = 12;
    public static final int DL_FAILED_BIT_RATE_ERROR = 13;
    public static final int SELECTED_PLAYLIST_STATUS = 14;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
}



