package mightyaudio.Model;

import mightyaudio.core.GlobalClass;

/**
 * Created by admin on 6/1/2016.
 */
public class BT_Configuration extends MightyObject
{
  //  public String uuid;



    public String name;
    public String mac_id;
    public int Status;
    GlobalClass globalClass ;

    public BT_Configuration(String name, String mac_id, int Status) {
        this.name = name;
        this.mac_id = mac_id;
        this.Status = Status ;
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

    public int getStatus(){ return Status;}

    public void setStatus(int Status){ this.Status = Status;}

    public BT_Configuration()
    {}
}
