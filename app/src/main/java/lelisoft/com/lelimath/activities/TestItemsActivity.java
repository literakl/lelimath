package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

    TestScript script;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_script_items);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        if (state != null) {
            log.debug("load state");
            script = (TestScript) state.getSerializable(ScriptListActivity.KEY_SCRIPT);
        } else {
            script = (TestScript) getIntent().getSerializableExtra(ScriptListActivity.KEY_SCRIPT);
        }

        TestItemAdapter adapter = new TestItemAdapter(script);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(TestItemsActivity.this, RunTestActivity.class);
                intent.putExtra(RunTestActivity.KEY_POSITION, position);
                intent.putExtra(ScriptListActivity.KEY_SCRIPT, script);
                TestItemsActivity.this.startActivity(intent);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putSerializable(ScriptListActivity.KEY_SCRIPT, script);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        script = (TestScript) state.getSerializable(ScriptListActivity.KEY_SCRIPT);
    }
}
