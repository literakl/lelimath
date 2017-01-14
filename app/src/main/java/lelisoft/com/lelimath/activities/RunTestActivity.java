package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.TestItem;
import lelisoft.com.lelimath.data.TestScript;

/**
 * Responsible for starting tests.
 * Created by Leoš on 08.01.2017.
 */

public class RunTestActivity extends BaseGameActivity {
    private static final Logger log = LoggerFactory.getLogger(RunTestActivity.class);

    public static final String KEY_POSITION = "POSITION";

    TestScript script;
    TestItem testItem;
    int position;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_with_fragment);

        Intent intent = getIntent();
        script = (TestScript) intent.getSerializableExtra(ScriptListActivity.KEY_SCRIPT);
        position = intent.getIntExtra(KEY_POSITION, 0);
        testItem = script.getItems().get(position);

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(KEY_POSITION, position);
        state.putSerializable(ScriptListActivity.KEY_SCRIPT, script);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        script = (TestScript) state.getSerializable(ScriptListActivity.KEY_SCRIPT);
        position = state.getInt(KEY_POSITION, 0);
        testItem = script.getItems().get(position);
    }
}
