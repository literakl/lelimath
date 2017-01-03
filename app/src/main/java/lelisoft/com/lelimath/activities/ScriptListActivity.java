package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.TestScriptAdapter;
import lelisoft.com.lelimath.data.TestScript;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.ScriptParser;

/**
 * Lists available scripts.
 * Created by Leoš on 10.12.2016.
 */

public class ScriptListActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    protected static final String KEY_SCRIPT = "SCRIPT";

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

        final List<TestScript> records = new ArrayList<>();

        try {
            InputStream is = getAssets().open("scripts/addition1.json");
            TestScript[] scripts = ScriptParser.parse(is);
            Collections.addAll(records, scripts);
        } catch (IOException e) {
            log.error("chyba", e);
        }

        records.add(new TestScript("Prvnacek", 3, 0.6962372f));
        records.add(new TestScript("Pocitame z pameti", 0, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani dvou\u00ADcifernych cisel", 15, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani dvou\u200bcifernych cisel", 15, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani a odecitani do peti", 0, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani pres desitku", 2, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Odecitani pres desitku", 11, Misc.getRandom().nextFloat()));
        records.add(new TestScript("ABCD EFGHI JKLMN OPQRS TUVWX YZ", 0, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Odecitani desitky", 4, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka trojky", 3, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka peti", 4, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka deseti", 10, Misc.getRandom().nextFloat()));

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new TestScriptAdapter(records));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(ScriptListActivity.this, TestItemsActivity.class);
                intent.putExtra(KEY_SCRIPT, records.get(position));
                ScriptListActivity.this.startActivity(intent);
            }
        });
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, ScriptListActivity.class));
    }
}
