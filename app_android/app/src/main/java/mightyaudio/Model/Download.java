package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class Download extends MightyObject
{

    private String playlist_name;
    private String getPlaylist_url;
    private float progress;
    private float free_space;
    private int downloaded_tracks;
    private int total_tracks;
    private int status;
    private String SnapshotId;

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public String getGetPlaylist_url() {
        return getPlaylist_url;
    }

    public void setGetPlaylist_url(String getPlaylist_url) {
        this.getPlaylist_url = getPlaylist_url;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getFree_space() {
        return free_space;
    }

    public void setFree_space(float free_space) {
        this.free_space = free_space;
    }

    public int getDownloaded_tracks() {
        return downloaded_tracks;
    }

    public void setDownloaded_tracks(int downloaded_tracks) {
        this.downloaded_tracks = downloaded_tracks;
    }

    public int getTotal_tracks() {
        return total_tracks;
    }

    public void setTotal_tracks(int total_tracks) {
        this.total_tracks = total_tracks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSnapshotId() {
        return SnapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.SnapshotId = snapshotId;
    }
}
