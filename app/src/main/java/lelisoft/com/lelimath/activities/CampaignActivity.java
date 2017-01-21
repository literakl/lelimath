package lelisoft.com.lelimath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.CampaignAdapter;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.provider.TestRecordProvider;

/**
 * Lists TestItems of selected TestScript
 * Created by Leoš on 31.12.2016.
 */

public class CampaignActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CampaignActivity.class);

    Campaign campaign;
    GridView gridView;
    CampaignAdapter adapter;
    TestRecordProvider provider;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_campaign);
        gridView = (GridView) findViewById(R.id.gridview);

        if (state != null) {
            log.debug("load state");
            campaign = (Campaign) state.getSerializable(CampaignListActivity.KEY_CAMPAIGN);
        } else {
            campaign = (Campaign) getIntent().getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        }

        provider = new TestRecordProvider(this);
        adapter = new CampaignAdapter(campaign, provider.getTestScores(campaign));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(CampaignActivity.this, RunTestActivity.class);
                intent.putExtra(RunTestActivity.KEY_POSITION, position);
                intent.putExtra(CampaignListActivity.KEY_CAMPAIGN, campaign);
                CampaignActivity.this.startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.updateScores(provider.getTestScores(campaign));
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putSerializable(CampaignListActivity.KEY_CAMPAIGN, campaign);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        campaign = (Campaign) state.getSerializable(CampaignListActivity.KEY_CAMPAIGN);
    }
}
