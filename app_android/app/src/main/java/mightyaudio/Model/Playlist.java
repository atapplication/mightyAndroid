package mightyaudio.Model;

import java.util.Comparator;

/**
 * Created by admin on 6/1/2016.
 */
public class Playlist extends MightyObject implements Cloneable
{

    private  String name;
    private  String uri;
    private  String snapshotId;
    private  String tracks_count;
    private  int offline;
    private  float downloadProgress;
    private  int public_playlist;
    private  String hash_key;
    private String playListUrl;
    private String spotifFollowup;
    private boolean swipeFlag;

    public Playlist(){}


    public Playlist(String name, String uri, String snapshotId, String tracks_count, int offline, float downloadProgress, int public_playlist)
    {
        this.name = name;
        this.uri = uri;
        this.snapshotId = snapshotId;
        this.tracks_count = tracks_count;
        this.offline = offline;
        this.downloadProgress = downloadProgress;
        this.public_playlist = public_playlist;
     //   this.hash_key = hash_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getTracks_count() {
        return tracks_count;
    }

    public void setTracks_count(String tracks_count) {
        this.tracks_count = tracks_count;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public float getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(float downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public int getPublic_playlist() {
        return public_playlist;
    }

    public void setPublic_playlist(int public_playlist) {
        this.public_playlist = public_playlist;
    }

    public String getHash_key() {
        return hash_key;
    }

    public void setHash_key(String hash_key) {
        this.hash_key = hash_key;
    }

    public String getPlayListUrl() {
        return playListUrl;
    }

    public void setPlayListUrl(String playListUrl) {
        this.playListUrl = playListUrl;
    }

    public String getSpotifFollowup() {
        return spotifFollowup;
    }

    public void setSpotifFollowup(String spotifFollowup) {
        this.spotifFollowup = spotifFollowup;
    }

    public boolean isSwipeFlag() {
        return swipeFlag;
    }

    public void setSwipeFlag(boolean swipeFlag) {
        this.swipeFlag = swipeFlag;
    }
//static int track_size;

    public static  final Comparator<Playlist> TracksComparator = new Comparator<Playlist>(){

        @Override
        public int compare(Playlist o1, Playlist o2) {
            return Integer.parseInt( o1.tracks_count ) - Integer.parseInt( o2.tracks_count );
        }

    };
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
