package lelisoft.com.lelimath.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Test;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.helpers.Misc;

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
        log.debug("Position {}, convertView {}", position, convertView);
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
        } else {
            log.warn("caption is null, position {}", position);
        }

        if (score != null) {
            Misc.setRating(view, score);
        } else {
            log.warn("score is null, position {}", position);
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
