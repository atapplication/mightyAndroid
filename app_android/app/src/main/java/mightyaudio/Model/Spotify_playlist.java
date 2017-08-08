package mightyaudio.Model;

/**
 * Created by sys on 15-Sep-16.
 */
public class Spotify_playlist extends MightyObject
{

    public String name;
    public String uri;
    public int playlist_count;

    public Spotify_playlist()
    {

    }

    public Spotify_playlist(String name, String uri,int playlist_count) {
        this.name = name;
        this.uri = uri;
        this.playlist_count = playlist_count;
    }

    public String getName()
    {
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

    public int getPlaylist_count() {
        return playlist_count;
    }

    public void setPlaylist_count(int playlist_count) {
        this.playlist_count = playlist_count;
    }
}
