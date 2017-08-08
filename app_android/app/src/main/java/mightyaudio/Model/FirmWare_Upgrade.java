package mightyaudio.Model;



public class FirmWare_Upgrade {
    private int Available_Status;
    private int Last_Upgrade_Status;
    private int Upgrade_Action;
    private String Current_Version;
    private String New_Version;
    private String Last_Update_Date;
    private String URL;
    private String Hash_Value;
    private int  Hash_Type;
    private int FW_Size;

    public int getAvailable_Status() {
        return Available_Status;
    }

    public void setAvailable_Status(int available_Status) {
        Available_Status = available_Status;
    }

    public int getLast_Upgrade_Status() {
        return Last_Upgrade_Status;
    }

    public void setLast_Upgrade_Status(int last_Upgrade_Status) {
        Last_Upgrade_Status = last_Upgrade_Status;
    }

    public int getUpgrade_Action() {
        return Upgrade_Action;
    }

    public void setUpgrade_Action(int upgrade_Action) {
        Upgrade_Action = upgrade_Action;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLast_Update_Date() {
        return Last_Update_Date;
    }

    public void setLast_Update_Date(String last_Update_Date) {
        Last_Update_Date = last_Update_Date;
    }

    public String getNew_Version() {
        return New_Version;
    }

    public void setNew_Version(String new_Version) {
        New_Version = new_Version;
    }

    public String getCurrent_Version() {
        return Current_Version;
    }

    public void setCurrent_Version(String current_Version) {
        Current_Version = current_Version;
    }

    public int getFW_Size() {
        return FW_Size;
    }

    public void setFW_Size(int FW_Size) {
        this.FW_Size = FW_Size;
    }

    public String getHash_Value() {
        return Hash_Value;
    }

    public int getHash_Type() {
        return Hash_Type;
    }

    public void setHash_Type(int hash_Type) {
        Hash_Type = hash_Type;
    }

    public void setHash_Value(String hash_Value) {
        Hash_Value = hash_Value;
    }
}
