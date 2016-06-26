package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.helpers.Metrics;

/**
 * Displays links to information pages.
 * Created by Leoš on 26.06.2016.
 */
public class InformationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(InformationActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.debug("onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        TextView button = (TextView) findViewById(R.id.main_button_blog);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_bugtracker);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_twitter);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_changelog);
        button.setOnClickListener(this);
        button = (TextView) findViewById(R.id.main_button_credits);
        button.setOnClickListener(this);
        Metrics.saveContentDisplayed("web", "home");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_changelog:
                WebViewerActivity.start(this, "changelog.html");
                break;

            case R.id.main_button_credits:
                WebViewerActivity.start(this, "credits.html");
                break;

            case R.id.main_button_bugtracker:
                openBrowser("http://lelimath.net/projects/lelimath");
                break;

            case R.id.main_button_blog:
                openBrowser("http://www.literak.cz/kategorie/lelimath/");
                break;

            case R.id.main_button_twitter:
                openBrowser("https://twitter.com/lelimath_app");
                break;
        }

    }

    public void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public static void start(Context c) {
        Intent intent = new Intent(c, InformationActivity.class);
        c.startActivity(intent);
    }
}
