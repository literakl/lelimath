package lelisoft.com.lelimath.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.TestScript;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Provides TestScript data.
 * Created by Leoš on 18.12.2016.
 */

public class TestScriptAdapter extends BaseAdapter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestScriptAdapter.class);

    private List<TestScript> records;

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
        View itemView;
        if (convertView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_script, parent, false);
//            itemView.setLayoutParams(new GridView.LayoutParams(85, 85));
        } else {
            itemView = convertView;
        }

        final ScriptViewHolder viewHolder = new ScriptViewHolder(itemView);
        viewHolder.setDataOnView(position);
        return itemView;
    }

    public TestScriptAdapter(List<TestScript> records) {
        log.debug("TestScriptAdapter()");
        this.records = records;
    }

    private class ScriptViewHolder extends RecyclerView.ViewHolder {
        TextView caption, stats;
        ImageView picture, star1, star2, star3;

        ScriptViewHolder(View view) {
            super(view);
            caption = (TextView) view.findViewById(R.id.caption);
            stats = (TextView) view.findViewById(R.id.stats);
            picture = (ImageView) view.findViewById(R.id.picture);
            star1 = (ImageView) view.findViewById(R.id.star1);
            star2 = (ImageView) view.findViewById(R.id.star2);
            star3 = (ImageView) view.findViewById(R.id.star3);
        }

        void setDataOnView(int position) {
            TestScript item = records.get(position);
            caption.setText(item.getTitle());
            String picture = item.getPicture();
            if (picture == null) {
                picture = "pic_kitten";
            }
            super.itemView.setBackgroundResource(Misc.getResourceId(picture));
            stats.setText(LeliMathApp.resources.getString(R.string.script_progress, item.getFinished(), item.getCount()));
            float score = item.getScore();
            /*
                in progress - 0 stars
                0.0 - 0.5999 - 1 star
                0.6 - 0.8999 - 2 stars
                0.9 - 1 - 3 stars
             */
            if (score >= 0.6) {
                star2.setImageResource(R.drawable.star_on);
                if (score >= 0.9) {
                    star3.setImageResource(R.drawable.star_on);
                }
            }
        }
    }
}
