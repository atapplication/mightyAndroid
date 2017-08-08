package mightyaudio.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.RootActivity;

public class MightyGuide1 extends RootActivity implements View.OnClickListener{
private static final String TAG = MightyGuide1.class.getCanonicalName();
    TextView tv_need,tv_title;
    Typeface custom_font_bold,custom_font;
    Button btn_got_one,btn_need;
    private TextView cancel_txt,header_txt,save_txt;
    private GlobalClass globalClass = GlobalClass.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mighty_guide1);
        custom_font_bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-bold.ttf");
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "serenity-light.ttf");
        tv_need = (TextView) findViewById(R.id.txt_need);
        tv_title = (TextView) findViewById(R.id.text_title);

        cancel_txt=(TextView) findViewById(R.id.txt_cancel);
        header_txt=(TextView) findViewById(R.id.text_title);
        save_txt=(TextView) findViewById(R.id.txt_save);
        cancel_txt.setTypeface(custom_font);
        header_txt.setTypeface(custom_font);
        save_txt.setTypeface(custom_font);
        cancel_txt.setText("< BACK");
        header_txt.setText("Mighty Setup");
        save_txt.setText("CANCEL");
        save_txt.setVisibility(View.INVISIBLE);
        Log.e(TAG,"Setup flow value "+globalClass.flowpref.getString(globalClass.FLOW_WAY,""));


        btn_got_one = (Button) findViewById(R.id.btn_got);
        btn_need = (Button)findViewById(R.id.btn_need);

        tv_need.setTypeface(custom_font_bold);
        tv_title.setTypeface(custom_font_bold);

        btn_got_one.setTypeface(custom_font);
        btn_need.setTypeface(custom_font);

        cancel_txt.setOnClickListener(this);
        btn_got_one.setOnClickListener(this);
        btn_need.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_cancel :
                finish();
                break;
            case R.id.btn_got :
                startActivity(new Intent(getApplicationContext(), PlugYourMightActivity.class));
                break;
            case R.id.btn_need :
                    Intent mighty_need = new Intent(this,MightyHelpActivity.class);
                    mighty_need.putExtra("FromData","MightGuide1");
                    startActivity(mighty_need);
                    break;
        }
    }
}
