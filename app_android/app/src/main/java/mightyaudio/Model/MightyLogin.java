package mightyaudio.Model;

/**
 * Created by admin on 6/1/2016.
 */
public class MightyLogin extends MightyObject
{

    public String Username;
    public String HashPassword;
    public int  Login_mode;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getHashPassword() {
        return HashPassword;
    }

    public void setHashPassword(String hashPassword) {
        HashPassword = hashPassword;
    }

    public int getLogin_mode() {
        return Login_mode;
    }

    public void setLogin_mode(int login_mode) {
        Login_mode = login_mode;
    }
}
