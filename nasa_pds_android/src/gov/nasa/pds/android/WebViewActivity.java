package gov.nasa.pds.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBarActivity;

public class WebViewActivity extends ActionBarActivity {

    public static final String EXTRA_WEB_URL = "WEB_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // get URL from intent
        String url = getIntent().getStringExtra(EXTRA_WEB_URL);
        Log.i("soap", "Opening URL: " + url);

        // find view by ids
//TODO        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.webViewProgress);
        WebView webview = (WebView) findViewById(R.id.webView);

        // set action bar
        getActionBar().setTitle("Preview");

        // set progress indicator
//        webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                Log.i("soap", "Progress: " + progress);
//                progressBar.setProgress(progress);
//            }
//        });

        // load passed url
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
        getActionBar().setUpAction(new AbstractAction(R.drawable.level_up, "Lesson") {
            @Override
            public void performAction(View view) {
                finish();
            }
        });
    }
}
