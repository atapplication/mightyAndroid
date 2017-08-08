package mightyaudio.Model;

import java.util.Comparator;

/**
 * Created by sys on 04-Oct-16.
 */
public class Wifi_Status extends MightyObject
{
    public int status;
    public int sec_type;
    public String ip_address;
    public int rssi;
    public String ssid;
    public String ap_name;
    private String wifiPssword;

    public Wifi_Status() {
    }

    public Wifi_Status(int status,int sec_type, int rssi, String ip_address, String ssid, String ap_name) {
        this.status = status;
        this.sec_type = sec_type;
        this.ip_address = ip_address;
        this.rssi = rssi;
        this.ssid = ssid;
        this.ap_name = ap_name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public int getSec_type() {
        return sec_type;
    }

    public void setSec_type(int sec_type) {
        this.sec_type = sec_type;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int flag) {
        this.status = flag;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(String bssid) {
        this.rssi = rssi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getAp_name() {
        return ap_name;
    }

    public void setAp_name(String bonjour) {
        this.ap_name = ap_name;
    }

    public String getWifiPssword() {
        return wifiPssword;
    }

    public void setWifiPssword(String wifiPssword) {
        this.wifiPssword = wifiPssword;
    }

    public static final Comparator<Wifi_Status> statusComparator = new Comparator<Wifi_Status>(){

        @Override
        public int compare(Wifi_Status o1, Wifi_Status o2) {
            return o1.rssi - o2.rssi;  // This will work because age is positive integer
        }

    };
}

