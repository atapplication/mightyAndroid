package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class WiFi_Configuration extends MightyObject
{
    public String SSID;
    public String AP_Name;
    public String Passcode;
    public int Mode;
    public int Connection_Status;
    public int def;

    public WiFi_Configuration() {
    }

    public WiFi_Configuration(String SSID, String AP_Name, String passcode, int mode, int connection_Status, int def) {
        this.SSID = SSID;
        this.AP_Name = AP_Name;
        Passcode = passcode;
        Mode = mode;
        Connection_Status = connection_Status;
        this.def = def;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getAP_Name() {
        return AP_Name;
    }

    public void setAP_Name(String AP_Name) {
        this.AP_Name = AP_Name;
    }

    public String getPasscode() {
        return Passcode;
    }

    public void setPasscode(String passcode) {
        Passcode = passcode;
    }

    public int getMode() {
        return Mode;
    }

    public void setMode(int mode) {
        Mode = mode;
    }

    public int getConnection_Status() {
        return Connection_Status;
    }

    public void setConnection_Status(int connection_Status) {
        Connection_Status = connection_Status;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }
}
