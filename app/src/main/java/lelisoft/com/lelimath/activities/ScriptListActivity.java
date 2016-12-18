package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.TestScriptAdapter;
import lelisoft.com.lelimath.data.TestScript;
import lelisoft.com.lelimath.helpers.Misc;

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

        List<TestScript> records = new ArrayList<>();
        records.add(new TestScript("Prvnacek", 3, 15, 0.6962372f));
        records.add(new TestScript("Pocitame zpameti", 0, 9, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani a odecitani do peti", 0, 9, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani dvojcifernych cisel", 15, 33, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Scitani pres desitku", 2, 7, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Odecitani pres desitku", 11, 16, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Odecitani trojky", 0, 8, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Odecitani desitky", 4, 14, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka trojky", 3, 33, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka peti", 4, 15, Misc.getRandom().nextFloat()));
        records.add(new TestScript("Nasobilka deseti", 10, 10, Misc.getRandom().nextFloat()));
        TestScriptAdapter adapter = new TestScriptAdapter(records);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.scripts_recycler_view);
        recyclerView.setAdapter(adapter);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, ScriptListActivity.class));
    }
}
