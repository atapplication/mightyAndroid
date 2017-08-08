package mightyaudio.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.ble.MightyMsgReceiver;
import mightyaudio.core.GlobalClass;

public class MightySoftwareUpdateActivity extends AppCompatActivity {
    private static final String TAG = MightySoftwareUpdateActivity.class.getSimpleName();
    MightyMsgReceiver mightyMsgReceiver;
    Typeface custom_font_light,custom_font_bold;
    private TextView cancel_txt,header_txt,save_txt,text_download_percentage,text_head,text_sub_head,text_few_minutes;
    private Button plugged_in;
    private GlobalClass globalClass;
    private IntentFilter intentFilter1 ;
    private BroadcastReceiver ble_onreceive;
    private ProgressBar download_progress;
    private ImageView mighty_img,auto_upgrade;
    private ProgressBar small_progressbar;
    private android.app.AlertDialog.Builder alertDialogBuilder;
    private MaterialDialog  mMaterialDialog,backPressMaterial,backPressMaterial_lower,other_mMaterialDialog;
    private View promptView ,backpromptView,other_prompView,backpromptViewlower;
    private TextView message_alert,other_messageTest;
    private TextView header_alert,backheader_alert,backmsg_alert,other_headeralert,backheaderlower_alert,backmsglower_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upgrade);
        globalClass = GlobalClass.getInstance();
        custom_font_light = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        plugged_in = (Button) findViewById(R.id.plugged_in);
        download_progress = (ProgressBar)findViewById(R.id.download_progress);
        text_download_percentage = (TextView)findViewById(R.id.text_download_percentage);
        small_progressbar = (ProgressBar)findViewById(R.id.small_progressbar);
        download_progress.setProgress(0);
        text_head = (TextView)findViewById(R.id.text_head);
        text_sub_head = (TextView)findViewById(R.id.text_sub_head);
        text_few_minutes = (TextView)findViewById(R.id.text_few_minutes);

        mighty_img = (ImageView) findViewById(R.id.mighty_img);
        auto_upgrade = (ImageView) findViewById(R.id.auto_upgrade);
        mighty_img.setVisibility(View.VISIBLE);
        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        cancel_txt.setTypeface(custom_font_light);
        header_txt.setTypeface(custom_font_bold);
        save_txt.setTypeface(custom_font_light);
        text_head.setTypeface(custom_font_bold);
        text_sub_head.setTypeface(custom_font_light);
        plugged_in.setTypeface(custom_font_light);
        text_few_minutes.setTypeface(custom_font_light);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);

        onreceive_ble();
        registerReceiver(ble_onreceive,intentFilter1);

        plugged_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"avarage "+globalClass.battery_info.AvailablePercentage+" "+globalClass.battery_info.getStatus()+" "+plugged_in.getTag());
                if( globalClass.mighty_ble_device != null){
                    if(globalClass.wifi_connected_global != null && globalClass.wifi_connected_global.getAp_name() != null &&  !globalClass.wifi_connected_global.ap_name.equals("")){
                        Log.e(TAG,"Batta info "+globalClass.battery_info.getAvailablePercentage()+" "+globalClass.battery_info.getStatus());
                        if(globalClass.battery_info.getStatus() == 1 && 35 <= globalClass.battery_info.getAvailablePercentage()){
                            Log.e(TAG,"Batta info "+globalClass.battery_info.getAvailablePercentage());
                            mighty_img.setVisibility(View.INVISIBLE);
                            auto_upgrade.setVisibility(View.VISIBLE);
                            small_progressbar.setVisibility(View.VISIBLE);
                            plugged_in.setVisibility(View.INVISIBLE);
                            text_few_minutes.setVisibility(View.VISIBLE);
                            text_sub_head.setText("Keep your Mighty plugged in \n during the update.");
                            sendDataFirmWare();
                        }else{
                            mightyNotConneted("Your Mighty's battery is too low to start the update","Please plug your Mighty into a charger and try again in a few minutes");
                        }
                    }else{
                        mightyNotConneted(getString(R.string.mighty_not_connected_wifi),getString(R.string.click_okay_below_to_reconnect));
                    }

                }else mightyNotConneted(getString(R.string.mighty_not_connected_app),getString(R.string.click_okay_below_to_reconnect));

            }
        });
        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPressed();
            }
        });

        if(globalClass.device_info.getSW_Version().equals("UPDATING")){
            mighty_img.setVisibility(View.INVISIBLE);
            auto_upgrade.setVisibility(View.VISIBLE);
            small_progressbar.setVisibility(View.VISIBLE);
            plugged_in.setVisibility(View.INVISIBLE);
            text_few_minutes.setVisibility(View.VISIBLE);
            cancel_txt.setVisibility(View.INVISIBLE);
            text_sub_head.setText("Keep your Mighty plugged in \n during the update.");
        }


        mightyMsgReceiver = new MightyMsgReceiver(this) {
            @Override
            public void onConnected() {
                Log.d(TAG, "Connected");
            }

            @Override
            public void onDiscovered() {
                Log.d(TAG, "Discovered");
                globalClass.lower_tool_bar_status = 1;
                //Intent launch_intent = new Intent(getApplicationContext(),LaunchTabActivity.class);
                //launch_intent.putExtra("onBackPressed","onBackPressed");
                //launch_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(launch_intent);
                finish();
            }

            @Override
            public void onDisconnected() {
                if(globalClass.global_firm_download.getProgress() != null){
                    if((int)Math.round(globalClass.global_firm_download.getProgress()) < 100 )
                        mightyUpdatingDailog("Download failed. Please reconnect your Mighty to WiFi in order to complete the software update.");
                }
                Log.d(TAG, "DisConnected");
                globalClass.reqLatestVersion = null;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(120000);
                            if(globalClass.reqLatestVersion != null) {
                                if (globalClass.reqLatestVersion != "null" && Float.valueOf(globalClass.device_info.getSW_Version()) < Float.valueOf(globalClass.reqLatestVersion)) {
                                    Log.e(TAG, "dont close the activity mighty is updating ");
                                } else {
                                    finish();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        };
    }

    private void cancelPressed(){
        if(backPressMaterial != null | backPressMaterial_lower != null)
        {
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(MightySoftwareUpdateActivity.this);
        try {
            if (globalClass.mighty_ble_device != null && !globalClass.device_info.getSW_Version().equals("UPDATING")) {
                if (Float.valueOf(globalClass.device_info.getSW_Version()) < 0.80f) {
                    backpromptViewlower = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                    backheaderlower_alert = (TextView) backpromptViewlower.findViewById(R.id.text_header);
                    backmsglower_alert = (TextView) backpromptViewlower.findViewById(R.id.text_message);
                    backheaderlower_alert.setText(getString(R.string.you_must_update_mighty_header));
                    backmsglower_alert.setText(getString(R.string.you_must_update_mighty_lower));
                    backPressMaterial_lower = new MaterialDialog(MightySoftwareUpdateActivity.this)
                            .setView(backpromptViewlower)
                            .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    globalClass.bleDisconnect();
                                    globalClass.Status = false;
                                    globalClass.lower_tool_bar_status = 0;
                                    finish();
                                    backPressMaterial_lower.dismiss();
                                    backPressMaterial_lower = null;
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    backPressMaterial_lower.dismiss();
                                    backPressMaterial_lower = null;
                                }
                            });
                    backPressMaterial_lower.show();
                } else {
                    backpromptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                    backheader_alert = (TextView) backpromptView.findViewById(R.id.text_header);
                    backmsg_alert = (TextView) backpromptView.findViewById(R.id.text_message);
                    backheader_alert.setText(getString(R.string.you_must_update_mighty_header));
                    backmsg_alert.setText(getString(R.string.you_must_update_mighty));
                    backPressMaterial = new MaterialDialog(MightySoftwareUpdateActivity.this)
                            .setView(backpromptView)
                            .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                    backPressMaterial.dismiss();
                                    backPressMaterial = null;
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    backPressMaterial.dismiss();
                                    backPressMaterial = null;
                                }
                            });
                    backPressMaterial.show();
                }
            } else {
                finish();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onreceive_ble(){
        intentFilter1 = new IntentFilter("ble.receive.successful");
        ble_onreceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent spotify) {
                int msgId = spotify.getExtras().getInt("msgid");
                int msgType = spotify.getExtras().getInt("msgtype");
                onReceiveMessageAutoUpgrade(msgId, msgType); //your_music_fragment indicating
                Log.e(TAG,"on_Connected_user :" +msgId +" msgType "+ msgType);


            }
        };
    }

    public void mightyUpdatingDailog(String myMsg){
        if(mMaterialDialog != null){
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(MightySoftwareUpdateActivity.this);
        promptView = layoutInflater.inflate(R.layout.materialdesign_singletext_dailog, null);
        header_alert=(TextView)promptView.findViewById(R.id.text_header);
        header_alert.setText(myMsg);
        mMaterialDialog = new MaterialDialog(MightySoftwareUpdateActivity.this)
                .setView(promptView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        mMaterialDialog = null;
                        finish();

                    }
                });
        mMaterialDialog.show();
    }

    public void mightyNotConneted(String tittle, String meassge){
        if(other_mMaterialDialog != null) {
            return;
        }
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(MightySoftwareUpdateActivity.this);
        other_prompView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        other_headeralert=(TextView)other_prompView.findViewById(R.id.text_header);
        other_messageTest=(TextView)other_prompView.findViewById(R.id.text_message);
        other_headeralert.setText(tittle);
        other_messageTest.setText(meassge);
        other_mMaterialDialog = new MaterialDialog(MightySoftwareUpdateActivity.this)
                .setView(other_prompView)
                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        other_mMaterialDialog.dismiss();
                        other_mMaterialDialog = null;
                        //if(flag == 1)
                        finish();

                    }
                });
        other_mMaterialDialog.show();
    }


    private void sendDataFirmWare(){
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 21;
        tcpClient.SendData(mightyMessage);
        Log.e(TAG, "calling");
    }
    public void onReceiveMessageAutoUpgrade(int msgId, int msgType) {

        if (msgId == 22){
            if (msgType == 102){

                Log.e(TAG,"Dowload_Progress "+ globalClass.global_firm_download.getProgress());
                Log.e(TAG,"Dowload_Status "+ globalClass.global_firm_download.getStatus());
                downloading_handler(globalClass.global_firm_download.getStatus());

            }
            else if( msgType == 200){

            }
        }
        if (msgId == 21){
            if (msgType == 102){

            }
        }
        if (msgId == 16){
            if(msgType == 200){
                //    globalClass.bleDisconnect();
            }
        }

    }
    public void downloading_handler(int status)
    {
        switch (status)
        {
            case Constants.FW_DL_IN_PROGRESS :
                Log.d(TAG,"DOWNLOAD PRGRESS");
                mighty_img.setVisibility(View.INVISIBLE);
                auto_upgrade.setVisibility(View.VISIBLE);
                plugged_in.setVisibility(View.INVISIBLE);
                text_few_minutes.setVisibility(View.VISIBLE);
                text_sub_head.setText("Keep your Mighty plugged in \n during the update.");

                cancel_txt.setVisibility(View.INVISIBLE);
                small_progressbar.setVisibility(View.INVISIBLE);
                download_progress.setVisibility(View.VISIBLE);
                text_download_percentage.setVisibility(View.VISIBLE);
                download_progress.setProgress((int)Math.round(globalClass.global_firm_download.getProgress()));
                text_download_percentage.setText((int) Math.round(globalClass.global_firm_download.getProgress())+"%");
                break;
            case Constants.FW_DL_IN_STALLED :
                globalClass.toastDisplay("Wait for some time");
                break;
            case Constants.FW_DL_COMPLETED:
                Log.d(TAG,"DOWNLOAD COMPLETED");

                mighty_img.setVisibility(View.INVISIBLE);
                download_progress.setVisibility(View.VISIBLE);
                auto_upgrade.setVisibility(View.VISIBLE);
                small_progressbar.setVisibility(View.VISIBLE);
                download_progress.setProgress(0);
                cancel_txt.setVisibility(View.INVISIBLE);
                text_download_percentage.setVisibility(View.INVISIBLE);
                //plugged_in.setVisibility(View.INVISIBLE);
                text_few_minutes.setVisibility(View.VISIBLE);
                text_sub_head.setText("Your Mighty is now installing the update. \n This will take a few minutes.");
                text_few_minutes.setText("Keep an eye on your Mighty. If the LED blinks blue, tap the play/pause button on your Mighty  to reconnect it to the app.");
                //globalClass.lower_tool_bar_status = 0;

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(120000);
                            //globalClass.lower_tool_bar_status = 0;
                            //Intent launch_intent = new Intent(getApplicationContext(),LaunchTabActivity.class);
                            //launch_intent.putExtra("onBackPressed","onBackPressed");
                            //launch_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //startActivity(launch_intent);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            case Constants.FW_DL_FAILED:
                Log.d(TAG,"DOWNLOAD fail");
                if(globalClass.mighty_ble_device != null){

                    mightyUpdatingDailog("Download failed. Please reconnect your Mighty to WiFi in order to complete the software update.");
                }else{
                    mightyUpdatingDailog("Please reconnect your Mighty to the mobile app");
                }
                globalClass.upgradeProcess = false;
                // progress_wait.setVisibility(View.INVISIBLE);
                text_download_percentage.setVisibility(View.INVISIBLE);
                mighty_img.setVisibility(View.VISIBLE);
                download_progress.setVisibility(View.INVISIBLE);
                small_progressbar.setVisibility(View.INVISIBLE);
                auto_upgrade.setVisibility(View.INVISIBLE);
                small_progressbar.setVisibility(View.INVISIBLE);
                plugged_in.setVisibility(View.INVISIBLE);
                download_progress.setProgress(0);
                text_download_percentage.setText("");
                break;
            default:
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mightyMsgReceiver.RegisterReceiver(this);
        globalClass.autoupgradeactivity = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        globalClass.autoupgradeactivity = false;
    }

    @Override
    public void onBackPressed() {
        if(cancel_txt.getVisibility() == View.VISIBLE)
            cancelPressed();
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mightyMsgReceiver.unRegisterReceiver(this);
        unregisterReceiver(ble_onreceive);
        Log.e(TAG,"onDestroy ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mighty_img.setVisibility(View.VISIBLE);
        auto_upgrade.setVisibility(View.INVISIBLE);
        small_progressbar.setVisibility(View.INVISIBLE);
        plugged_in.setVisibility(View.VISIBLE);
        text_few_minutes.setVisibility(View.INVISIBLE);
        download_progress.setVisibility(View.INVISIBLE);
        text_download_percentage.setVisibility(View.INVISIBLE);
        download_progress.setProgress(0);
        cancel_txt.setVisibility(View.VISIBLE);
        text_sub_head.setText("Looks like we need to update your \\n Mighty. Please connect your Mighty \\n to a power source.");
    }
}
