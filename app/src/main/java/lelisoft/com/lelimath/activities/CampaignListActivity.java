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
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.logic.CampaignParser;
import lelisoft.com.lelimath.provider.TestRecordProvider;

/**
 * Lists available scripts.
 * Created by Leoš on 10.12.2016.
 */

public class CampaignListActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CalcActivity.class);

    public static final String KEY_CAMPAIGNS_PATH = "CAMPAIGN_LIST_PATH";
    protected static final String KEY_CAMPAIGN = "CAMPAIGN_ID";

    GridView gridView;
    CampaignListAdapter adapter;
    TestRecordProvider provider;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);
        setContentView(R.layout.act_campaign_list);

        String path;
        if (state != null) {
            log.debug("load state");
            path = state.getString(KEY_CAMPAIGNS_PATH);
        } else {
            path = getIntent().getStringExtra(KEY_CAMPAIGNS_PATH);
        }

        final List<Campaign> records = new ArrayList<>();

        try {
            InputStream is = getAssets().open(path);
            Campaign[] scripts = CampaignParser.parse(is);
            Collections.addAll(records, scripts);
        } catch (Exception e) {
            log.error("Error reading file {}", path, e);
        }

        provider = new TestRecordProvider(this);
        provider.setCampaignsData(records);
        adapter = new CampaignListAdapter(records);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(CampaignListActivity.this, CampaignActivity.class);
                intent.putExtra(KEY_CAMPAIGN, records.get(position));
                CampaignListActivity.this.startActivityForResult(intent, 0);
            }
        });

        Metrics.saveCampaignsDisplayed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        provider.setCampaignsData(adapter.getRecords());
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, CampaignListActivity.class));
    }
}
