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

        records.add(new TestScript("bgt_bed_linen", 3, 0.69f, "bgt_bed_linen"));
        records.add(new TestScript("bgt_bell_flower", 0, 0.1f, "bgt_bell_flower"));
        records.add(new TestScript("bgt_blue_flower", 0, 0.97f, "bgt_blue_flower"));
        records.add(new TestScript("bgt_gradient_chantilly", 4, 0.87f, "bgt_gradient_chantilly"));
        records.add(new TestScript("bgt_gradient_ice", 3, 0.78f, "bgt_gradient_ice"));
        records.add(new TestScript("bgt_gradient_pink", 4, 0.867f, "bgt_gradient_pink"));
        records.add(new TestScript("bgt_gradient_salmon", 12, 0.767f, "bgt_gradient_salmon"));
        records.add(new TestScript("bgt_gray_blocks", 22, 0.697f, "bgt_gray_blocks"));
        records.add(new TestScript("bgt_gray_hexagons", 35, 0.777f, "bgt_gray_hexagons"));
        records.add(new TestScript("bgt_orange_o", 23, 0.8f, "bgt_orange_o"));
        records.add(new TestScript("bgt_pyramids", 7, 0.28f, "bgt_pyramids"));
        records.add(new TestScript("bgt_red_rosa", 15, 0.98f, "bgt_red_rosa"));
        records.add(new TestScript("bgt_sea_weed", 11, 0.78f, "bgt_sea_weed"));
        records.add(new TestScript("bgt_squares", 56, 0.68f, "bgt_squares"));
        records.add(new TestScript("bgt_triangles", 9, 0.79f, "bgt_triangles"));

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
