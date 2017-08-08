package mightyaudio.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mightyaudio.TCP.Constants;
import mightyaudio.TCP.MightyMessage;
import mightyaudio.TCP.TCPClient;
import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;

import static mightyaudio.adapter.YourMusicAdapter.selectedlist;

public class DownloadQualityActivity extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = DownloadQualityActivity.class.getSimpleName();
    private ImageView blue_tick_normal,blue_tick_high,blue_tick_extreme;
    private TextView normal_text,high_text,extreme_text,save_text,txt_cancel,text_title;
    private ImageView normal_img_plus,high_img_plus,extreme_img_plus,normal_img_tick,high_img_tick,extreme_img_tick;
    private GlobalClass globalClass;
    private Typeface custom_font,custom_font_bold;
    private FrameLayout normal_frame,high_fram,extem_fram;
    public static float pink_memory_level;
    private float pink_level;
    ProgressDialog progressDialog;
    Dialog dialog;
    IntentFilter intentFilter;
    BroadcastReceiver receiver;
    private MaterialDialog  mMaterialDialog;
    private View promptView;
    private TextView header_txt;
    private TextView message_txt,txt_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_quality);

        globalClass = GlobalClass.getInstance();
        txt_cancel = (TextView)findViewById(R.id.txt_cancel);
        save_text = (TextView)findViewById(R.id.txt_save);
        text_title= (TextView)findViewById(R.id.text_title);

        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");

        blue_tick_normal = (ImageView) findViewById(R.id.dq_blue_tick_normal);
        blue_tick_high = (ImageView) findViewById(R.id.dq_blue_tick_high);
        blue_tick_extreme = (ImageView) findViewById(R.id.dq_blue_tick_extreme);

        normal_text = (TextView) findViewById(R.id.text_normal);
        high_text = (TextView) findViewById(R.id.text_high);
        extreme_text = (TextView) findViewById(R.id.text_extreme);
        txt_note = (TextView) findViewById(R.id.txt_note);

        normal_img_plus =(ImageView)findViewById(R.id.normal_img_plus);
        high_img_plus =(ImageView)findViewById(R.id.high_img_plus);
        extreme_img_plus =(ImageView)findViewById(R.id.extrem_img_plus);

        normal_img_tick =(ImageView)findViewById(R.id.normal_img_tick);
        high_img_tick =(ImageView)findViewById(R.id.high_img_tick);
        extreme_img_tick =(ImageView)findViewById(R.id.extrem_img_tick);

        normal_img_tick =(ImageView)findViewById(R.id.normal_img_tick);
        high_img_tick =(ImageView)findViewById(R.id.high_img_tick);
        extreme_img_tick =(ImageView)findViewById(R.id.extrem_img_tick);

        normal_frame = (FrameLayout)findViewById(R.id.normal_frame_layout);
        high_fram = (FrameLayout)findViewById(R.id.high_frame_layout);
        extem_fram = (FrameLayout)findViewById(R.id.extrem_frame_layout);

        normal_text.setTypeface(custom_font);
        high_text.setTypeface(custom_font);
        extreme_text.setTypeface(custom_font);

        txt_cancel.setText("< BACK");
        save_text.setText("SAVE");
        text_title.setText("Download Quality");

        text_title.setTypeface(custom_font_bold);
        txt_cancel.setTypeface(custom_font);
        save_text.setTypeface(custom_font);
        txt_note.setTypeface(custom_font);

        normal_img_plus.setOnClickListener(this);
        high_img_plus.setOnClickListener(this);
        extreme_img_plus.setOnClickListener(this);
        save_text.setOnClickListener(this);
        txt_cancel.setOnClickListener(this);
        displayRow();
        defineBroadCast();
    }

    private void displayRow(){
        int bitrate_mode = globalClass.global_bit_rate.getBitRateMode();
        Log.e(TAG,"bitrate_mode "+bitrate_mode+" "+globalClass.global_bit_rate);
        switch(bitrate_mode){
            case 0 :
                normal_img_plus.setVisibility(View.INVISIBLE);
                blue_tick_normal.setVisibility(View.VISIBLE);
                blue_tick_high.setVisibility(View.GONE);
                blue_tick_extreme.setVisibility(View.GONE);

                normal_frame.setVisibility(View.INVISIBLE);
                high_fram.setVisibility(View.VISIBLE);
                extem_fram.setVisibility(View.VISIBLE);

                high_img_plus.setVisibility(View.VISIBLE);
                high_img_tick.setVisibility(View.INVISIBLE);
                extreme_img_plus.setVisibility(View.VISIBLE);
                extreme_img_tick.setVisibility(View.INVISIBLE);
                break;
            case 1 :
                high_img_plus.setVisibility(View.INVISIBLE);
                blue_tick_normal.setVisibility(View.GONE);
                blue_tick_high.setVisibility(View.VISIBLE);
                blue_tick_extreme.setVisibility(View.GONE);

                normal_frame.setVisibility(View.VISIBLE);
                high_fram.setVisibility(View.INVISIBLE);
                extem_fram.setVisibility(View.VISIBLE);

                normal_img_plus.setVisibility(View.VISIBLE);
                normal_img_tick.setVisibility(View.INVISIBLE);
                extreme_img_plus.setVisibility(View.VISIBLE);
                extreme_img_tick.setVisibility(View.INVISIBLE);
                break;
            case 2 :
                extreme_img_plus.setVisibility(View.INVISIBLE);
                blue_tick_normal.setVisibility(View.GONE);
                blue_tick_high.setVisibility(View.GONE);
                blue_tick_extreme.setVisibility(View.VISIBLE);

                normal_frame.setVisibility(View.VISIBLE);
                high_fram.setVisibility(View.VISIBLE);
                extem_fram.setVisibility(View.INVISIBLE);

                normal_img_plus.setVisibility(View.VISIBLE);
                normal_img_tick.setVisibility(View.INVISIBLE);
                high_img_plus.setVisibility(View.VISIBLE);
                high_img_tick.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void set_bit_rate() {
        TCPClient tcpClient = new TCPClient();
        MightyMessage mightyMessage = new MightyMessage();
        mightyMessage.MessageType = Constants.MSG_TYPE_SET;
        mightyMessage.MessageID = 24;
        Log.d("Bit_rate_mode", "Set bit_rate structure");
        tcpClient.SendData(mightyMessage);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.normal_img_plus :
                    normal_img_plus.setVisibility(View.INVISIBLE);
                    normal_img_tick.setVisibility(View.VISIBLE);
                    high_img_plus.setVisibility(View.VISIBLE);
                    high_img_tick.setVisibility(View.INVISIBLE);
                    extreme_img_plus.setVisibility(View.VISIBLE);
                    extreme_img_tick.setVisibility(View.INVISIBLE);
                    globalClass.global_set_bitrate = 0;
                    break;

            case R.id.high_img_plus :
                    normal_img_plus.setVisibility(View.VISIBLE);
                    normal_img_tick.setVisibility(View.INVISIBLE);
                    high_img_plus.setVisibility(View.INVISIBLE);
                    high_img_tick.setVisibility(View.VISIBLE);
                    extreme_img_plus.setVisibility(View.VISIBLE);
                    extreme_img_tick.setVisibility(View.INVISIBLE);
                    globalClass.global_set_bitrate = 1;
                break;

            case R.id.extrem_img_plus :
                    normal_img_plus.setVisibility(View.VISIBLE);
                    normal_img_tick.setVisibility(View.INVISIBLE);
                    high_img_plus.setVisibility(View.VISIBLE);
                    high_img_tick.setVisibility(View.INVISIBLE);
                    extreme_img_plus.setVisibility(View.INVISIBLE);
                    extreme_img_tick.setVisibility(View.VISIBLE);
                    globalClass.global_set_bitrate = 2;
                break;

            case R.id.txt_save :
                if(globalClass.global_set_bitrate != globalClass.global_bit_rate.getBitRateMode()){
                    if(globalClass.mighty_playlist.size() != 0) {
                        LayoutInflater layoutInflater = LayoutInflater.from(this);
                        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
                        header_txt=(TextView)promptView.findViewById(R.id.text_header);
                        message_txt=(TextView)promptView.findViewById(R.id.text_message);
                        header_txt.setText("Changing download quality will erase all playlists currently synced to your Mighty and will require  you to re-sync your playlists");
                        message_txt.setText("Proceed?");
                        mMaterialDialog = new MaterialDialog(this)
                                .setView(promptView)
                                .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar();
                                        if(!globalClass.mighty_playlist.isEmpty()){
                                            GlobalClass.lower_tool_bar_status = 1;
                                        }
                                        Log.e(TAG,"Download_quality_received "+globalClass.mighty_playlist.size());
                                        set_bit_rate();

                                        mMaterialDialog.dismiss();
                                    }
                                })
                        .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();

                            }
                        });
                        mMaterialDialog.show();
                    }else{
                        progressBar();
                        set_bit_rate();
                    }
                }else{
                    globalClass.alertDailogSingleText("Please Select Bitrate",DownloadQualityActivity.this);
                }
                break;
            case R.id.txt_cancel :
                finish();
                break;
        }
    }

    private void defineBroadCast(){
        intentFilter=new IntentFilter("bitrate.broadcast");
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int msgId= intent.getExtras().getInt("msgid");
                int msgType = intent.getExtras().getInt("msgType");
                onReceiveMessage(msgId,msgType);
                Log.e(TAG,"broad cast fire");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,intentFilter);
    }

    public void onReceiveMessage(int msgId, int msgType){
        if(msgId == Constants.MSG_BIT_RATE_MODE){
            if(msgType == 200){
                Log.e(TAG,"Download_quality_received" +globalClass.global_bit_rate);
                globalClass.global_bit_rate.setBitRateMode(globalClass.global_set_bitrate);
                displayRow();
                if(!globalClass.mighty_playlist.isEmpty()){
                    GlobalClass.lower_tool_bar_status = 1;
                }
                Toast.makeText(this, "Download Quality changed successfully", Toast.LENGTH_LONG).show();
                if(!selectedlist.isEmpty()){
                    Intent spotify_login_tick = new Intent();
                    spotify_login_tick.setAction("bitrate.change.unselectplaylist");
                    sendBroadcast(spotify_login_tick);
                }
                if(dialog != null){
                    dialog.cancel();
                    dialog.dismiss();
                }
                finish();
            }else{
                globalClass.toastDisplay("Unable to change Download Quality, please try again!");
            }

        }
    }
    // if msg_ID == 24 and msgType == 200 ----->  setBitRate
    private void progressBar() {
        Log.e(TAG,"Prgressbar clicked_autofill");
        if(dialog != null){
            if(dialog.isShowing())
                return;
        }

        dialog = new Dialog(this);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();
/*        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(dialog != null){
                    dialog.cancel();
                    dialog.dismiss();
                }
            }
        }, 6000);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");
        unregisterReceiver(receiver);
    }

}
