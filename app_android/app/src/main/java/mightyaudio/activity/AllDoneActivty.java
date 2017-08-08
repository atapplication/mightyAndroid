package mightyaudio.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;
import mightyaudio.core.SessionManager;

public class AllDoneActivty extends RootActivity implements View.OnClickListener{
    private GlobalClass globalClass =GlobalClass.getInstance();
    Typeface custom_font_bold,custom_font;
    Button btn_got_one,btn_need;
    ImageView device;
    ImageView image_led,image_jack,image_previous,image_next,image_play_pause,image_volume_up,image_volume_down,image_playlist;
    ImageView tooltip_led,tooltip_jack,tooltip_previous,tooltip_next,tooltip_play_pause,tooltip_volume_up,tooltip_volume_down,tooltip_playlist;
    private TextView cancel_txt,header_txt,save_txt,text_head,text_sub_head,text_buttom_head;
    private Button button;
    public SharedPreferences.Editor use_editor;
    public SharedPreferences usingpref;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_done_activty);
        session = new SessionManager(getApplicationContext());
        device = (ImageView) findViewById(R.id.device);
        image_led = (ImageView) findViewById(R.id.image_led);
        image_jack = (ImageView) findViewById(R.id.image_jack);
        image_previous = (ImageView) findViewById(R.id.image_previous);
        image_next = (ImageView) findViewById(R.id.image_next);
        image_play_pause = (ImageView) findViewById(R.id.image_play_pause);
        image_volume_up = (ImageView) findViewById(R.id.image_volume_up);
        image_volume_down = (ImageView) findViewById(R.id.image_volume_down);
        image_playlist = (ImageView) findViewById(R.id.image_playlist);
        tooltip_jack = (ImageView) findViewById(R.id.tooltip_jack);
        tooltip_previous = (ImageView) findViewById(R.id.tooltip_previous);
        tooltip_next = (ImageView) findViewById(R.id.tooltip_next);
        tooltip_play_pause = (ImageView) findViewById(R.id.tooltip_play_pause);
        tooltip_volume_up = (ImageView) findViewById(R.id.tooltip_volume_up);
        tooltip_volume_down = (ImageView) findViewById(R.id.tooltip_volume_down);
        tooltip_playlist = (ImageView) findViewById(R.id.tooltip_playlist);
        tooltip_led = (ImageView) findViewById(R.id.tooltip_led);
        button =(Button)findViewById(R.id.btn_all_done);

        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        text_head=(TextView) findViewById(R.id.text_head);
        text_sub_head=(TextView) findViewById(R.id.text_sub_head);
        text_buttom_head=(TextView) findViewById(R.id.text_buttom_head);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font_bold);
        button.setTypeface(custom_font);

        text_head.setTypeface(custom_font_bold);
        text_sub_head.setTypeface(custom_font);
        text_buttom_head.setTypeface(custom_font);

        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);
        cancel_txt.setVisibility(View.INVISIBLE);

        image_led.setOnClickListener(this);
        image_previous.setOnClickListener(this);
        image_next.setOnClickListener(this);
        image_play_pause.setOnClickListener(this);
        image_volume_up.setOnClickListener(this);
        image_volume_down.setOnClickListener(this);
        image_playlist.setOnClickListener(this);
        image_jack.setOnClickListener(this);

        button.setOnClickListener(this);
        usingpref = getSharedPreferences(globalClass.USING_PREF, Context.MODE_PRIVATE);
        use_editor = usingpref.edit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        session.checkLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_led:
                tooltip_led.setVisibility(View.VISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Pink means charging and charged\nGreen means power and sync\nBlue means Bluetooth\nOrange means low power and power off");
                image_led.setImageResource(R.drawable.oval_green);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;
            case R.id.image_jack:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.VISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Your Mighty will blink pink as it is charging and will stay pink when fully charged.");

                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_green);
                break;

            case R.id.image_previous:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.VISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Plays previous song in the playlist.");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_green);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;

            case R.id.image_next:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.VISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Plays the next song in the playlist.");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_green);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;

            case R.id.image_play_pause:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.VISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Press to turn your Mighty on and hold for 3 sec to turn off.");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_green);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;
            case R.id.image_volume_up:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.VISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("Pump up the jam, pump it up.");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_green);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;
            case R.id.image_volume_down:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.VISIBLE);
                tooltip_playlist.setVisibility(View.INVISIBLE);
                text_buttom_head.setText("A little bit softer now...a little bit softer now...A little bit softer now...");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_green);
                image_playlist.setImageResource(R.drawable.oval_blue);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;

            case R.id.image_playlist:
                tooltip_led.setVisibility(View.INVISIBLE);
                tooltip_jack.setVisibility(View.INVISIBLE);
                tooltip_previous.setVisibility(View.INVISIBLE);
                tooltip_next.setVisibility(View.INVISIBLE);
                tooltip_play_pause.setVisibility(View.INVISIBLE);
                tooltip_volume_up.setVisibility(View.INVISIBLE);
                tooltip_volume_down.setVisibility(View.INVISIBLE);
                tooltip_playlist.setVisibility(View.VISIBLE);
                text_buttom_head.setText("Tap to toggle between your playlists.\n Mighty will speak the playlist title to you.");
                image_led.setImageResource(R.drawable.oval_blue);
                image_previous.setImageResource(R.drawable.oval_blue);
                image_next.setImageResource(R.drawable.oval_blue);
                image_play_pause.setImageResource(R.drawable.oval_blue);
                image_volume_up.setImageResource(R.drawable.oval_blue);
                image_volume_down.setImageResource(R.drawable.oval_blue);
                image_playlist.setImageResource(R.drawable.oval_green);
                image_jack.setImageResource(R.drawable.oval_blue);
                break;
            case R.id.btn_all_done :
                globalClass.lower_tool_bar_status = 1;
                globalClass.auto_connect_force_cancel =1;
                globalClass.userActivityMode = 0;
                startActivity(new Intent(getApplicationContext(), LaunchTabActivity.class));
                //finish();
                use_editor.putInt("using_satatus",1);
                use_editor.commit();
                Intent spotify_login_tick = new Intent();
                spotify_login_tick.setAction("destroy.setup.activity");
                sendBroadcast(spotify_login_tick);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        globalClass.toastDisplay("Please press All Done");
        return;
    }
}
