package mightyaudio.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.RootActivity;


public class PlugYourMightActivity extends RootActivity {

    Button btn_continue;
    TextView tv_back,text_title,skip,txt_need,text_sub_head,text_sub_head1;
    Typeface custom_font_light,custom_font_bold;
    private TextView cancel_txt,header_txt,save_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_mighty);
        custom_font_light = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        btn_continue = (Button) findViewById(R.id.btn_continue);
        txt_need=(TextView) findViewById(R.id.txt_need);

        txt_need.setTypeface(custom_font_bold);
        btn_continue.setTypeface(custom_font_light);

        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        text_sub_head=(TextView) findViewById(R.id.text_sub_head);
        text_sub_head1=(TextView) findViewById(R.id.text_sub_head1);
        cancel_txt.setTypeface(custom_font_light);
        header_txt.setTypeface(custom_font_bold);
        save_txt.setTypeface(custom_font_light);
        text_sub_head.setTypeface(custom_font_light);
        text_sub_head1.setTypeface(custom_font_light);
        btn_continue.setTypeface(custom_font_light);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);

        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), MightyGuide2.class));
            }
        });

    }

}
