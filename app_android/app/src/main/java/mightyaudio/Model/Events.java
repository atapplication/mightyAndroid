package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Events extends MightyObject {

    public int BT_Status;
    public int WiFi_status;
    public int Internet_Connection;
    public int Spotify_Status;
    public int Mighty_Cloud_Status;
    public int Offline_Status;


    public int getBT_Status() {
        return BT_Status;
    }

    public void setBT_Status(int BT_Status) {
        this.BT_Status = BT_Status;
    }

    public int getWiFi_status() {
        return WiFi_status;
    }

    public void setWiFi_status(int wiFi_status) {
        WiFi_status = wiFi_status;
    }

    public int getInternet_Connection() {
        return Internet_Connection;
    }

    public void setInternet_Connection(int internet_Connection) {
        Internet_Connection = internet_Connection;
    }

    public int getSpotify_Status() {
        return Spotify_Status;
    }

    public void setSpotify_Status(int spotify_Status) {
        Spotify_Status = spotify_Status;
    }

    public int getMighty_Cloud_Status() {
        return Mighty_Cloud_Status;
    }

    public void setMighty_Cloud_Status(int mighty_Cloud_Status) {
        Mighty_Cloud_Status = mighty_Cloud_Status;
    }

    public int getOffline_Status() {
        return Offline_Status;
    }

    public void setOffline_Status(int offline_Status) {
        Offline_Status = offline_Status;
    }
}
/*    public Events()
    {



    }*/
/*

    public Events(int BLEStatus, int wiFiConnected, int internetConnection, int BTScanStatus, int BTPairingStatus,int spotifyStatus) {
        this.BLEStatus = BLEStatus;
        WiFiConnected = wiFiConnected;
        InternetConnection = internetConnection;
        this.BTScanStatus = BTScanStatus;
        this.BTPairingStatus = BTPairingStatus;
        this.SpotifyStatus = SpotifyStatus;
    }


    public int getBLEStatus() {
        return BLEStatus;
    }

    public void setBLEStatus(int BLEStatus) {
        this.BLEStatus = BLEStatus;
    }

    public int getSpotifyStatus() {
        return SpotifyStatus;
    }

    public void setSpotifyStatus(int SpotifyStatus) {
        this.SpotifyStatus = SpotifyStatus;
    }

    public int getWiFiConnected() {
        return WiFiConnected;
    }

    public void setWiFiConnected(int wiFiConnected) {
        WiFiConnected = wiFiConnected;
    }

    public int getInternetConnection() {
        return InternetConnection;
    }

    public void setInternetConnection(int internetConnection) {
        InternetConnection = internetConnection;
    }

    public int getBTScanStatus() {
        return BTScanStatus;
    }

    public void setBTScanStatus(int BTScanStatus) {
        this.BTScanStatus = BTScanStatus;
    }

    public int getBTPairingStatus() {
        return BTPairingStatus;
    }

    public void setBTPairingStatus(int BTPairingStatus) {
        this.BTPairingStatus = BTPairingStatus;
    }
}
*/
