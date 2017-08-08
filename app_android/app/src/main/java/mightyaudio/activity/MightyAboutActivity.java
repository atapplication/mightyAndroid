package mightyaudio.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;

public class MightyAboutActivity extends RootActivity {
    private static final String TAG = UserActivity.class.getSimpleName();
    private Typeface custom_font_bold,custom_font_light;
    private TextView txt_back,text_title,txt_save,head_mobile_version,text_app_version,head_mighty_version,text_mighty_status;
    private IntentFilter intentFilter ;
    private BroadcastReceiver ble_onreceive;
    private GlobalClass globalClass;
    private PackageInfo pinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mighty_about);
        globalClass = GlobalClass.getInstance();
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        txt_back = (TextView) findViewById(R.id.txt_cancel);
        txt_save = (TextView) findViewById(R.id.txt_save);
        text_title = (TextView)findViewById(R.id.text_title);
        head_mobile_version = (TextView)findViewById(R.id.head_mobile_version);
        text_app_version = (TextView)findViewById(R.id.text_app_version);
        head_mighty_version = (TextView)findViewById(R.id.head_mighty_version);
        text_mighty_status = (TextView)findViewById(R.id.text_mighty_status);
        text_app_version.setText(pinfo.versionName+" Build "+pinfo.versionCode);

        text_title.setTypeface(custom_font_bold);
        txt_back.setText("< BACK");
        text_title.setText(getString(R.string.mighty_about));
        txt_save.setVisibility(View.INVISIBLE);


        txt_back.setTypeface(custom_font_light);
        head_mobile_version.setTypeface(custom_font_light);
        text_app_version.setTypeface(custom_font_light);
        head_mighty_version.setTypeface(custom_font_light);
        text_mighty_status.setTypeface(custom_font_light);


        onReceiveBleSoftwareVersion();


        //Displaying Software Version
        if(globalClass.device_info != null){
            if(globalClass.device_info.getSW_Version() != null){
                if(globalClass.mighty_ble_device != null && !globalClass.device_info.getSW_Version().equals("ver5.2")){
                    Log.e(TAG,"Soft Version "+globalClass.device_info.getSW_Version());
                    text_mighty_status.setText(globalClass.device_info.getSW_Version()+"");
                }else {
                    text_mighty_status.setText("Not Connected");
                }
            }
        }
        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(ble_onreceive,intentFilter);
    }


    private void onReceiveBleSoftwareVersion(){
        intentFilter = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                if(msgId == 0){
                    if(msgType == 202){
                        Log.e(TAG,"Softaw version "+globalClass.device_info.getSW_Version());
                        if(globalClass.mighty_ble_device != null){
                            Log.e(TAG,"Soft Version "+globalClass.device_info.getSW_Version());
                            text_mighty_status.setText(globalClass.device_info.getSW_Version());
                        }
                    }
                }


            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ble_onreceive);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
