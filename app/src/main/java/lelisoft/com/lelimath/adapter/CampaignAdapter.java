package lelisoft.com.lelimath.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Test;
import lelisoft.com.lelimath.data.Campaign;

/**
 * Provides TestItem data
 * Created by Leoš on 31.12.2016.
 */

public class CampaignAdapter extends BaseAdapter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CampaignAdapter.class);

    private List<Test> records;
    private int currentPosition;

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Test item = records.get(position);
        View view;
        TextView caption = null;

        if (convertView == null) {
            if (item.isFinished() || position <= currentPosition) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_open, parent, false);
                caption = (TextView) view.findViewById(R.id.caption);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_locked, parent, false);
            }
        } else {
            view = convertView;
            caption = (TextView) view.findViewById(R.id.caption);
            if (item.isFinished() || position <= currentPosition) {
                if (caption == null) { // recycling wrong view, we must replace it anyway
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_open, parent, false);
                }
            } else {
                if (caption != null) { // recycling wrong view, we must replace it anyway
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_locked, parent, false);
                }
            }
        }

        if (caption != null) {
            caption.setText(Integer.toString(position + 1));
        }

        if (item.isFinished()) {
            ImageView star1 = (ImageView) view.findViewById(R.id.star1);
            ImageView star2 = (ImageView) view.findViewById(R.id.star2);
            ImageView star3 = (ImageView) view.findViewById(R.id.star3);

        /*
            no progress - 0 stars
            0.0 - 0.5999 - 1 star
            0.6 - 0.8999 - 2 stars
            0.9 - 1 - 3 stars
        */
            star1.setImageResource(R.drawable.star_on);
            float score = item.getRecord().getScore();
            if (score >= 0.6) {
                star2.setImageResource(R.drawable.star_on);
                if (score >= 0.9) {
                    star3.setImageResource(R.drawable.star_on);
                }
            }
        }


        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        Test item = records.get(position);
        return item.isFinished() || position <= currentPosition;
    }

    public CampaignAdapter(Campaign script) {
        log.debug("CampaignAdapter()");
        this.records = script.getItems();
        if (records == null) {
            records = new ArrayList<>();
        }

        // find the first unfinished item
        for (int i = 0; i < records.size(); i++) {
            Test item = records.get(i);
            if (! item.isFinished()) {
                currentPosition = i;
                break;
            }
        }
    }
}
