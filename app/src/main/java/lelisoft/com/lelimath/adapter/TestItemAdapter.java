package lelisoft.com.lelimath.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.TestItem;
import lelisoft.com.lelimath.data.TestScript;

/**
 * Provides TestItem data
 * Created by Leoš on 31.12.2016.
 */

public class TestItemAdapter extends BaseAdapter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestItemAdapter.class);

    private List<TestItem> records;

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TestItem item = records.get(position);
        View view;
        TextView caption;

        if (convertView == null) {
            if (item.getRecord() != null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_script_item, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_script_locked, parent, false);
            }
        } else {
            view = convertView;
            caption = (TextView) view.findViewById(R.id.caption);
            if (item.getRecord() != null) {
                if (caption == null) { // recycling wrong view, we must replace it anyway
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_script_item, parent, false);
                }
            } else {
                if (caption != null) { // recycling wrong view, we must replace it anyway
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_script_locked, parent, false);
                }
            }
        }

        if (item.getRecord() != null) {
            ImageView star1 = (ImageView) view.findViewById(R.id.star1);
            ImageView star2 = (ImageView) view.findViewById(R.id.star2);
            ImageView star3 = (ImageView) view.findViewById(R.id.star3);

            caption = (TextView) view.findViewById(R.id.caption);
            caption.setText(position);

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

    public TestItemAdapter(TestScript script) {
        log.debug("TestItemAdapter()");
        this.records = script.getItems();
        if (records == null) {
            records = Collections.emptyList();
        }
    }
}
