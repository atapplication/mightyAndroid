package mightyaudio.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.RootActivity;

public class MightyHelpActivity extends RootActivity implements View.OnClickListener{
    private static final String TAG = MightyHelpActivity.class.getSimpleName();
    private static final String mighty_help_url= "https://bemighty.com/pages/setup-page";
    private static final String spotify_needone_url= "https://www.spotify.com/us/subscriptions2/";
    private static final String spotify_signup_url= "https://www.spotify.com/us/premium/";
    private static final String spotify_help_url= "https://support.spotify.com/us/";
    private static final String need_mighty_url = "https://bemighty.com/";
    private static final String mighty_led_url = "https://bemighty.com/pages/setup-page#LED_anchor";
    private TextView back_text,close_text,middle_text;
    private Typeface custom_font,custom_font_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mighty_help);
        String intent_data= getIntent().getStringExtra("FromData");
        Log.e(TAG,"intent_data "+intent_data);

        custom_font = Typeface.createFromAsset(getAssets(), "serenity-light.ttf");
        custom_font_bold = Typeface.createFromAsset(getAssets(), "serenity-bold.ttf");
        back_text= (TextView)findViewById(R.id.txt_cancel);
        close_text= (TextView)findViewById(R.id.txt_save);
        middle_text= (TextView)findViewById(R.id.text_title);
        WebView webView = (WebView)findViewById(R.id.web_view);
        WebViewClient yourWebClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //progressDialog.dismiss();

            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(yourWebClient);
        webView.getHitTestResult();
        if(intent_data.equals("UserActivity")){
            webView.loadUrl(mighty_help_url);
            middle_text.setText("Mighty Help");
        }else if(intent_data.equals("SpotifyActivity")){
            webView.loadUrl(spotify_needone_url);
            middle_text.setText("Spotify Setup");
        }else if(intent_data.equals("SpotifyPremium")){
            webView.loadUrl(spotify_signup_url);
            middle_text.setText("Spotify Premium");
        }else if(intent_data.equals("SpotifyHelp")){
            webView.loadUrl(spotify_help_url);
            middle_text.setText("Spotify Help");
        }else if(intent_data.equals("MightGuide1")){
            webView.loadUrl(need_mighty_url);
            middle_text.setText("Mighty");
        }else if(intent_data.equals("MightyLED")){
            webView.loadUrl(mighty_led_url);
            middle_text.setText("Mighty LED");
        }

        back_text.setText("< BACK");
        close_text.setText("CLOSE");


        back_text.setTypeface(custom_font);
        close_text.setTypeface(custom_font);
        middle_text.setTypeface(custom_font_bold);
        back_text.setOnClickListener(this);
        close_text.setOnClickListener(this);
        middle_text.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_cancel :
               finish();
                break;
            case R.id.txt_save :
                finish();
                break;

        }
    }
}
