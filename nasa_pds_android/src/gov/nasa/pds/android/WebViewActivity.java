/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBarActivity;

/**
 * Activity that previews lesson.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class WebViewActivity extends ActionBarActivity {

    public static final String EXTRA_WEB_URL = "WEB_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // get URL from intent
        String url = getIntent().getStringExtra(EXTRA_WEB_URL);
        Log.i("soap", "Preview URL: " + url);

        // load passed url
        WebView webview = (WebView) findViewById(R.id.webView);
        webview.loadUrl(url);

        // set progress indicator
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                startProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                stopProgress();
            }
        });

        // set action bar
        getActionBar().setTitle("Preview");
        getActionBar().setUpAction(new AbstractAction(R.drawable.level_left, "Lesson") {
            @Override
            public void performAction(View view) {
                finish();
            }
        });
    }
}
