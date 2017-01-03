package lelisoft.com.lelimath.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.TestItemAdapter;
import lelisoft.com.lelimath.data.TestScript;

/**
 * Lists TestItems of selected TestScript
 * Created by Leoš on 31.12.2016.
 */

public class TestItemsActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(TestItemsActivity.class);


    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_script_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCalc);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(TestItemsActivity.this);
                }
            });
        }

        GridView gridView = (GridView) findViewById(R.id.gridview);
        TestScript script = (TestScript) getIntent().getSerializableExtra(ScriptListActivity.KEY_SCRIPT);
        gridView.setAdapter(new TestItemAdapter(script));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(TestItemsActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO load and store in stop/resume events
}
