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
import lelisoft.com.lelimath.provider.TestRecordProvider;

/**
 * Lists available scripts.
 * Created by Leoš on 10.12.2016.
 */

public class CampaignListActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    protected static final String KEY_CAMPAIGN = "CAMPAIGN_ID";

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

        records.add(new Campaign("bgt_bed_linen", "bgt_bed_linen"));
        records.add(new Campaign("bgt_bell_flower", "bgt_bell_flower"));
        records.add(new Campaign("bgt_blue_flower", "bgt_blue_flower"));
        records.add(new Campaign("bgt_gradient_chantilly", "bgt_gradient_chantilly"));
        records.add(new Campaign("bgt_gradient_ice", "bgt_gradient_ice"));
        records.add(new Campaign("bgt_gradient_pink", "bgt_gradient_pink"));
        records.add(new Campaign("bgt_gradient_salmon", "bgt_gradient_salmon"));
        records.add(new Campaign("bgt_gray_blocks", "bgt_gray_blocks"));
        records.add(new Campaign("bgt_gray_hexagons", "bgt_gray_hexagons"));
        records.add(new Campaign("bgt_orange_o", "bgt_orange_o"));
        records.add(new Campaign("bgt_pyramids", "bgt_pyramids"));
        records.add(new Campaign("bgt_red_rosa", "bgt_red_rosa"));
        records.add(new Campaign("bgt_sea_weed", "bgt_sea_weed"));
        records.add(new Campaign("bgt_squares", "bgt_squares"));
        records.add(new Campaign("bgt_triangles", "bgt_triangles"));

        TestRecordProvider provider = new TestRecordProvider(this);
        provider.setCampaignsData(records);
        CampaignListAdapter adapter = new CampaignListAdapter(records);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
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
