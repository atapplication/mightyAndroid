package mightyaudio.TCP;

import mightyaudio.Model.MightyObject;

/**
 * Created by ashok on 07-06-2016.
 */
public class MightyMessage
{
    public int MessageType;
    public int MessageID;
    int RequestID;
    int ResponseID;
    int More;
    int Size;
    public String Token;
    public String display_string;
    public MightyObject mightyObject;


    public MightyMessage()
    {

    }

    public static MightyMessage NewMessageGET()
    {
        //Allocate a new GET Type Mighty Message Buffer
        MightyMessage mightyMessage = new MightyMessage();

     //   mightyMessage.MessageType = Constants.MSG_TYPE_GET; // MSG_TYPE_SET
        mightyMessage.RequestID = Configs.GetRequestID();
        mightyMessage.ResponseID = 0;
        mightyMessage.More = 0;
        mightyMessage.Token = Configs.GetToken();
        return  mightyMessage;
    }
}

