package mightyaudio.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.RootActivity;

public class MightyGuide2 extends RootActivity implements View.OnClickListener
{
    Typeface custom_font_bold,custom_font;
    TextView txt_need;
    Button btn_got_one,btn_need;
    private TextView cancel_txt,header_txt,save_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mighty_guide2);
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");

        btn_got_one = (Button) findViewById(R.id.btn_got);
        btn_need = (Button) findViewById(R.id.btn_need);


        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        txt_need=(TextView) findViewById(R.id.txt_need);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font_bold);
        save_txt.setTypeface(custom_font);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);

        btn_got_one.setTypeface(custom_font);
        btn_need.setTypeface(custom_font);
        txt_need.setTypeface(custom_font_bold);

        btn_got_one.setText("Got One");
        btn_need.setText("Need One");


       btn_got_one.setOnClickListener(this);
       btn_need.setOnClickListener(this);
        cancel_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_need :
                startActivity(new Intent(this,MightyCreateAccountActivity.class));
                break;
            case R.id.btn_got :
                startActivity(new Intent(this, MightyLoginActivity.class));
                break;
            case R.id.txt_cancel :
                finish();
                break;

        }
    }
}
