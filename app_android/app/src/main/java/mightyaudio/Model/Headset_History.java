package mightyaudio.Model;
import java.util.Comparator;
public class Headset_History extends MightyObject
{
    public String name="name";
    public String mac_id="mac_id";
    private boolean plus_button_clickable;
    public int Status;

    public Headset_History() {
    }

    public Headset_History(String name, String mac_id, int Status)
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

    public boolean isPlus_button_clickable() {
        return plus_button_clickable;
    }

    public void setPlus_button_clickable(boolean plus_button_clickable) {
        this.plus_button_clickable = plus_button_clickable;
    }

    public static final Comparator<Headset_History> statusComparator = new Comparator<Headset_History>(){

        @Override
        public int compare(Headset_History o1, Headset_History o2) {
            return o2.Status - o1.Status;  // This will work because age is positive integer
        }

    };
}

