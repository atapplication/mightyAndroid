package mightyaudio.Model;

/**
 * Created by admin on 6/28/2016.
 */
public class App_Info extends MightyObject
{

    public String OS;
    public String OS_Version;
    public String Serial_Number;
    public String IMEI;
    public String App_Version;

/*
    public App_Info()
    {
        OS = "Android";
        OS_Version = "23.0";
        Serial_Number = "6tkjahdspo";
        IMEI = "5hnio98u7qsw4";
        App_Version = "1.0";
    }
*/

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getOS_Version() {
        return OS_Version;
    }

    public void setOS_Version(String OS_Version) {
        this.OS_Version = OS_Version;
    }

    public String getSerial_Number() {
        return Serial_Number;
    }

    public void setSerial_Number(String serial_Number) {
        Serial_Number = serial_Number;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getApp_Version() {
        return App_Version;
    }

    public void setApp_Version(String app_Version) {
        App_Version = app_Version;
    }
}
