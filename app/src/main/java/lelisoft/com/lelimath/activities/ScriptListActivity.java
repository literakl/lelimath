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

        records.add(new TestScript("abstract_pattern_background", 3, 0.69f, "abstract_pattern_background")); // hezke
        records.add(new TestScript("blue_flowers", 0, 0.1f, "blue_flowers")); // ujde
//        records.add(new TestScript("floral_flowers_background", 15, 0.3f, "floral_flowers_background")); // ujde
//        records.add(new TestScript("flowers_floral_pattern_background", 15, 0.8f, "flowers_floral_pattern_background")); prilis tmave
        records.add(new TestScript("geometric_pattern_3d_blocks", 0, 0.97f, "geometric_pattern_3d_blocks")); // pekne
//        records.add(new TestScript("geometric_pattern_squares", 2, 0.67f, "geometric_pattern_squares")); // nelibi
//        records.add(new TestScript("geometric_pattern", 11, 0.78f, "geometric_pattern")); // vzor je prilis tmavy
//        records.add(new TestScript("japanese_wave", 0, 0.57f, "japanese_wave")); // mozna az moc vyrazne, utlumit barvy?
        records.add(new TestScript("muster_rosa", 4, 0.87f, "muster_rosa")); // slo by, vyrazne
        records.add(new TestScript("owl_colorful_patchwork_art", 3, 0.78f, "owl_colorful_patchwork_art")); // jednoduche, slo by
//        records.add(new TestScript("pink_pattern_seamless", 4, 0.27f, "pink_pattern_seamless")); // asi prilis vyrazne
//        records.add(new TestScript("scr_metal", 10, 0.76f, "scr_metal")); // slo by, ale prijde mi nudne
//        records.add(new TestScript("scr_silk", 4, 0.77f, "scr_silk")); // slo by
//        records.add(new TestScript("seamless_pattern", 12, 0.87f, "seamless_pattern")); // slo by po uprave, horsi kontrast
//        records.add(new TestScript("sheep_wallpaper_pattern_pink", 6, 0.67f, "sheep_wallpaper_pattern_pink")); ne, moc bere pozornost
//        records.add(new TestScript("swirls_and_squares_pattern", 2, 0.767f, "swirls_and_squares_pattern")); ne
        records.add(new TestScript("yellow_pink_easter_eggs", 4, 0.867f, "yellow_pink_easter_eggs")); // jenom barevny prechod ale vypada moc hezky
        records.add(new TestScript("pattern_671857", 12, 0.767f, "pattern_671857")); // seda se mi moc nelibi
//        records.add(new TestScript("pattern_958064", 15, 0.997f, "pattern_958064")); // docela dobre
        records.add(new TestScript("background_pattern_1410202414", 22, 0.697f, "background_pattern_1410202414")); // pekne
        records.add(new TestScript("hippie_tie_dye_retro_pattern_colorful", 35, 0.777f, "hippie_tie_dye_retro_pattern_colorful")); // trosku zesvetlit
//        records.add(new TestScript("flowers_floral_seamless_pattern", 12, 0.887f, "flowers_floral_seamless_pattern")); // ne
//        records.add(new TestScript("abstract_seamless_pattern_1438715636a7r", 23, 0.8f, "abstract_seamless_pattern_1438715636a7r")); // spatny kontrast
//        records.add(new TestScript("abstract_triangles_background_1439541011jmi", 23, 0.8f, "abstract_triangles_background_1439541011jmi")); // spatny kontrast
        records.add(new TestScript("curvy_pattern_1408545778o3y", 23, 0.8f, "curvy_pattern_1408545778o3y")); // ujde
//        records.add(new TestScript("trellis_pattern_seamless_teal_green", 23, 0.8f, "trellis_pattern_seamless_teal_green")); // spatny kontrast
//        records.add(new TestScript("hexagon_wallpaper_pattern_1471189240nox", 23, 0.8f, "hexagon_wallpaper_pattern_1471189240nox")); // zmensit, nahradit bilou
//        records.add(new TestScript("geometric_circles_seamless_pattern_1402610686fy2", 23, 0.8f, "geometric_circles_seamless_pattern_1402610686fy2")); // ne
//        records.add(new TestScript("geometric_694941_640", 23, 0.8f, "geometric_694941_640")); // ujde, nahradit bilou
        records.add(new TestScript("background_1152459_640", 23, 0.8f, "background_1152459_640")); // dobre
        records.add(new TestScript("abstract_1505912_640", 23, 0.8f, "abstract_1505912_640")); // ujde
//        records.add(new TestScript("geometric_wallpaper_pattern_orange", 23, 0.8f, "geometric_wallpaper_pattern_orange")); // zmensit

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
