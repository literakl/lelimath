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

/**
 * Lists TestItems of selected TestScript
 * Created by Leoš on 31.12.2016.
 */

public class CampaignActivity extends LeliBaseActivity {
    private static final Logger log = LoggerFactory.getLogger(CampaignActivity.class);

    Campaign campaign;

    @Override
    protected void onCreate(Bundle state) {
        log.debug("onCreate()");
        super.onCreate(state);

        setContentView(R.layout.act_campaign);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        if (state != null) {
            log.debug("load state");
            campaign = (Campaign) state.getSerializable(CampaignListActivity.KEY_CAMPAIGN);
        } else {
            campaign = (Campaign) getIntent().getSerializableExtra(CampaignListActivity.KEY_CAMPAIGN);
        }

        CampaignAdapter adapter = new CampaignAdapter(campaign);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(CampaignActivity.this, RunTestActivity.class);
                intent.putExtra(RunTestActivity.KEY_POSITION, position);
                intent.putExtra(CampaignListActivity.KEY_CAMPAIGN, campaign);
                CampaignActivity.this.startActivity(intent);
            }
        });
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
