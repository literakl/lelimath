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
import java.util.Map;

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
    private Map<String, Integer> scores;
    private int firstIncomplete;

    public CampaignAdapter(Campaign script, Map<String, Integer> scores) {
        log.debug("CampaignAdapter()");
        this.records = script.getItems();
        if (records == null) {
            records = new ArrayList<>();
        }
        updateScores(scores);
    }

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

        Integer score = scores.get(item.getId());
        if (convertView == null) {
            if (score != null || position <= firstIncomplete) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_open, parent, false);
                caption = (TextView) view.findViewById(R.id.caption);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_test_locked, parent, false);
            }
        } else {
            view = convertView;
            caption = (TextView) view.findViewById(R.id.caption);
            if (score != null || position <= firstIncomplete) {
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

        if (score != null) {
        /*
            no progress - 0 stars
            0 - 59:   1 star
            60 - 89:  2 stars
            90 - 100: 3 stars
        */
            ImageView star1 = (ImageView) view.findViewById(R.id.star1);
            ImageView star2 = (ImageView) view.findViewById(R.id.star2);
            ImageView star3 = (ImageView) view.findViewById(R.id.star3);
            star1.setImageResource(R.drawable.star_on);
            if (score >= 60) {
                star2.setImageResource(R.drawable.star_on);
                if (score >= 90) {
                    star3.setImageResource(R.drawable.star_on);
                } else {
                    star3.setImageResource(R.drawable.star_off);
                }
            } else {
                star2.setImageResource(R.drawable.star_off);
                star3.setImageResource(R.drawable.star_off);
            }
        }


        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return position <= firstIncomplete;
    }

    public void updateScores(Map<String, Integer> scores) {
        log.debug("updateScores()");
        this.scores = scores;
        // find the first unfinished item
        if (records.isEmpty()) {
            firstIncomplete = 0;
        } else {
            firstIncomplete = 10000;
            for (int i = 0; i < records.size(); i++) {
                Test item = records.get(i);
                if (scores.get(item.getId()) == null) {
                    firstIncomplete = i;
                    break;
                }
            }
        }
    }
}
