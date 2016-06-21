package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.helpers.Metrics;

/**
 * Displays web page from resources
 * Created by Leoš on 20.06.2016.
 */
public class WebViewerActivity extends AppCompatActivity {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebViewerActivity.class);
    public static final String KEY_RESOURCE_NAME = BuildConfig.APPLICATION_ID + ".RESOURCE_NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate()");
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);
        String resource = (String) getIntent().getSerializableExtra(KEY_RESOURCE_NAME);
        webview.loadUrl("file:///android_res/raw/" + resource);
        Metrics.saveContentDisplayed("web", resource);
    }

    public static void start(Context c, String resource) {
        Intent intent = new Intent(c, WebViewerActivity.class);
        intent.putExtra(KEY_RESOURCE_NAME, resource);
        c.startActivity(intent);
    }
}
