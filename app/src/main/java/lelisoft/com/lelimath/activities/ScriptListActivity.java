package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;

/**
 * Lists available scripts.
 * Created by Leoš on 10.12.2016.
 */

public class ScriptListActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_scripts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCalc);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(ScriptListActivity.this);
                }
            });
        }
    }
}
