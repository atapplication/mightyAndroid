package mightyaudio.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;

public class DeviceRenameActivity extends RootActivity implements View.OnClickListener {

    private static final String TAG = DeviceRenameActivity.class.getSimpleName();
    private EditText device_name;
    Typeface custom_font_bold,custom_font;
    private TextView cancel_txt,header_txt,save_txt,text_head_lebel;
    private MaterialDialog  mMaterialDialog;
    private View promptView;
    private TextView aler_header_txt;
    private TextView aler_message_txt;
    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        globalClass = GlobalClass.getInstance();
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        device_name = (EditText) findViewById(R.id.edit_email);

        device_name.setText(globalClass.ble_device_name);
        device_name.setHint("Please name your Mighty");
        text_head_lebel = (TextView) findViewById(R.id.text_head_lebel);
        cancel_txt = (TextView) findViewById(R.id.txt_cancel);
        header_txt = (TextView) findViewById(R.id.text_title);
        save_txt = (TextView) findViewById(R.id.txt_save);
        device_name.setTypeface(custom_font);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font_bold);
        save_txt.setTypeface(custom_font);
        cancel_txt.setText("< BACK");
        header_txt.setText("Rename Your Mighty");
        save_txt.setText("SAVE");
        cancel_txt.setOnClickListener(this);
        save_txt.setOnClickListener(this);
        text_head_lebel.setVisibility(View.INVISIBLE);

        device_name.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[\\x00-\\x7F]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });
    }


    @Override
    public void onClick(View v){
            switch (v.getId()) {
                case R.id.txt_save:
                    if (device_name.getText().toString().length() < 4 | device_name.getText().toString().length() > 15) {
                        globalClass.alertDailogSingleText("Your Mighty's name must be 4 to 15 characters",DeviceRenameActivity.this );
                    }else{
                        globalClass.device_info.setDevice_ID(0);
                        globalClass.device_info.setDevice_Name(device_name.getText().toString());
                        set_device_name();
                        alert("Your Mighty has been renamed","The change will be reflected in the app after you reboot your Mighty" );
                    }
                    break;
                case R.id.txt_cancel:
                    finish();
                    break;

            }
        }

    private void set_device_name() {
        Log.e(TAG,"Sending_Set_device");
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 0;
        tcpClient.SendData(mightyMessage);
    }



    public void alert(String title, String Message){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        aler_header_txt=(TextView)promptView.findViewById(R.id.text_header);
        aler_message_txt=(TextView)promptView.findViewById(R.id.text_message);
        aler_header_txt.setText(title);
        aler_message_txt.setText(Message);
        mMaterialDialog = new MaterialDialog(this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mMaterialDialog.dismiss();

                    }
                });

        mMaterialDialog.show();
    }
    }
