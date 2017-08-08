package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class BT_Scan_List extends MightyObject
{
    public String name="name";
    public String mac_id="mac_id";
    public int Status;

    public BT_Scan_List() {
    }

    public BT_Scan_List(String name, String mac_id, int Status)
    {
        this.name = name;
        this.mac_id = mac_id;
        this.Status = Status;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac_id() {
        return mac_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public int getStatus() {return Status;}

    public void setStatus(int Status) {
        this.Status = Status;
    }
}
