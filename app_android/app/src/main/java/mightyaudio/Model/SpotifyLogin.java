package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class SpotifyLogin extends MightyObject
{

    public static String Username;
    public static String Authentication_Token;
    public static String OfflineToken;
    public static int Experity_date;
    public static String FacebookLinked;
    public static int PrivateSession;
    public static int PublishActivity;
    public static int Login_mode;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getAuthentication_Token() {
        return Authentication_Token;
    }

    public void setAuthentication_Token(String authentication_Token) {
        Authentication_Token = authentication_Token;
    }

    public String getOfflineToken() {
        return OfflineToken;
    }

    public void setOfflineToken(String offlineToken) {
        OfflineToken = offlineToken;
    }

    public int getExperity()
    {
        return Experity_date;
    }

    public void setExperity(int experity)
    {
        this.Experity_date = Experity_date;
    }

    public String getFacebookLinked() {
        return FacebookLinked;
    }

    public void setFacebookLinked(String facebookLinked) {
        FacebookLinked = facebookLinked;
    }

    public int getPrivateSession() {
        return PrivateSession;
    }

    public void setPrivateSession(int privateSession) {
        PrivateSession = privateSession;
    }

    public int getPublishActivity() {
        return PublishActivity;
    }

    public int getLogin_mode() {
        return Login_mode;
    }

    public void setLogin_mode(int login_mode){
        Login_mode = login_mode;
    }

    public void setPublishActivity(int publishActivity) {
        PublishActivity = publishActivity;
    }

    public SpotifyLogin()
    {



    }
}
