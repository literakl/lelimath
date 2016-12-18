package lelisoft.com.lelimath.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.TestScript;
import lelisoft.com.lelimath.helpers.CustomItemClickListener;
import lelisoft.com.lelimath.helpers.LeliMathApp;

/**
 * Provides TestScript data.
 * Created by Leoš on 18.12.2016.
 */

public class TestScriptAdapter extends RecyclerView.Adapter<TestScriptAdapter.ScriptViewHolder> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestScriptAdapter.class);

    private List<TestScript> records;

    private CustomItemClickListener listener = new CustomItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Context context = view.getRootView().getContext();
//            Intent intent = new Intent(context, BadgeAwardActivity.class);
//            intent.putExtra(BadgeAwardActivity.KEY_BADGE, records.get(position).badgeAward.getBadge());
//            context.startActivity(intent);
        }
    };

    public TestScriptAdapter(List<TestScript> records) {
        log.debug("TestScriptAdapter()");
        this.records = records;
    }

    @Override
    public ScriptViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tmpl_script, viewGroup, false);

        final ScriptViewHolder viewHolder = new ScriptViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getLayoutPosition());
            }
        });
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ScriptViewHolder vh, int position) {
        vh.setDataOnView(position);
        vh.itemView.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class ScriptViewHolder extends RecyclerView.ViewHolder {
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
