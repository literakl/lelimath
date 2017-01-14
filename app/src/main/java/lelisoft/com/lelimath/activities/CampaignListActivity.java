package lelisoft.com.lelimath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import lelisoft.com.lelimath.adapter.CampaignListAdapter;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.logic.ScriptParser;

/**
 * Lists available scripts.
 * Created by Leoš on 10.12.2016.
 */

public class CampaignListActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    protected static final String KEY_CAMPAIGN = "CAMPAIGN";

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);
        setContentView(R.layout.act_campaign_list);

        final List<Campaign> records = new ArrayList<>();

        try {
            InputStream is = getAssets().open("campaigns/addition1.json");
            Campaign[] scripts = ScriptParser.parse(is);
            Collections.addAll(records, scripts);
        } catch (IOException e) {
            log.error("chyba", e);
        }

        records.add(new Campaign("bgt_bed_linen", 3, 0.69f, "bgt_bed_linen"));
        records.add(new Campaign("bgt_bell_flower", 0, 0.1f, "bgt_bell_flower"));
        records.add(new Campaign("bgt_blue_flower", 0, 0.97f, "bgt_blue_flower"));
        records.add(new Campaign("bgt_gradient_chantilly", 4, 0.87f, "bgt_gradient_chantilly"));
        records.add(new Campaign("bgt_gradient_ice", 3, 0.78f, "bgt_gradient_ice"));
        records.add(new Campaign("bgt_gradient_pink", 4, 0.867f, "bgt_gradient_pink"));
        records.add(new Campaign("bgt_gradient_salmon", 12, 0.767f, "bgt_gradient_salmon"));
        records.add(new Campaign("bgt_gray_blocks", 22, 0.697f, "bgt_gray_blocks"));
        records.add(new Campaign("bgt_gray_hexagons", 35, 0.777f, "bgt_gray_hexagons"));
        records.add(new Campaign("bgt_orange_o", 23, 0.8f, "bgt_orange_o"));
        records.add(new Campaign("bgt_pyramids", 7, 0.28f, "bgt_pyramids"));
        records.add(new Campaign("bgt_red_rosa", 15, 0.98f, "bgt_red_rosa"));
        records.add(new Campaign("bgt_sea_weed", 11, 0.78f, "bgt_sea_weed"));
        records.add(new Campaign("bgt_squares", 56, 0.68f, "bgt_squares"));
        records.add(new Campaign("bgt_triangles", 9, 0.79f, "bgt_triangles"));

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CampaignListAdapter(records));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(CampaignListActivity.this, CampaignActivity.class);
                intent.putExtra(KEY_CAMPAIGN, records.get(position));
                CampaignListActivity.this.startActivity(intent);
            }
        });
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CampaignListActivity.class));
    }
}
