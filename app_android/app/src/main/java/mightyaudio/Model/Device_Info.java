package mightyaudio.Model;

/**
 * Created by admin on 6/10/2016.
 */
public class Device_Info extends MightyObject
{

    public int Device_Type;
    public String Mighty_Version;
    public String SW_Version;
    public String HW_Serial_Number;
    public int Device_ID;
    public String Device_Name;
    public String Device_Manufactured_Region;
    public String currentserviceuuid;

    public String getDevice_Manufactured_Region() {
        return Device_Manufactured_Region;
    }

    public void setDevice_Manufactured_Region(String device_Manufactured_Region) {
        Device_Manufactured_Region = device_Manufactured_Region;
    }

    public int getDevice_Type() {
        return Device_Type;
    }

    public void setDevice_Type(int device_Type) {
        Device_Type = device_Type;
    }

    public String getMighty_Version() {
        return Mighty_Version;
    }

    public void setMighty_Version(String mighty_Version) {
        Mighty_Version = mighty_Version;
    }

    public String getSW_Version() {
        return SW_Version;
    }

    public void setSW_Version(String SW_Version) {
        this.SW_Version = SW_Version;
    }

    public String getHW_Serial_Number() {
        return HW_Serial_Number;
    }

    public void setHW_Serial_Number(String HW_Serial_Number) {
        this.HW_Serial_Number = HW_Serial_Number;
    }

    public int getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(int device_ID) {
        Device_ID = device_ID;
    }

    public String getDevice_Name() {
        return Device_Name;
    }

    public void setDevice_Name(String device_Name) {
        Device_Name = device_Name;
    }

    public String getCurrentserviceuuid() {
        return currentserviceuuid;
    }

    public void setCurrentserviceuuid(String currentserviceuuid) {
        this.currentserviceuuid = currentserviceuuid;
    }

    public Device_Info()
    {
/*
        Mighty_Version = "V 1.0";
        SW_Version = "ver5.2";
        Device_ID = "23polarKelta";
        Device_Name = "SongsMighty";
        Device_Manufactured_Region = "INDIA";
*/


    }
};
