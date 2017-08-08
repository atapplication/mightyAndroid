package mightyaudio.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import mighty.audio.R;
import mightyaudio.core.RootActivity;

public class PrivacyTermsActivity extends RootActivity implements View.OnClickListener {
    private static final String mighty_privacy_url= "https://bemighty.com/pages/legal/";
    private TextView back_text,close_text,middle_text;
    private Typeface custom_font,custom_font_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);
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
        webView.loadUrl(mighty_privacy_url);
        back_text.setText("< BACK");
        close_text.setText("CLOSE");
        middle_text.setText("Privacy");

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
