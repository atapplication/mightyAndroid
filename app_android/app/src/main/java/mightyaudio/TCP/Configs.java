package mightyaudio.TCP;

/**
 * Created by ashok on 08-06-2016.
 */
public class Configs
{
    static int RequestID;
    public static int GetRequestID()
    {
        return RequestID++;
    }

    public static String GetToken()
    {
        return "MIGHTY_ANDROID_TOKEN";
    }
}
