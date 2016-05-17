package lelisoft.com.lelimath.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Adapter for ListView holding Badges
 * Created by Leoš on 06.05.2016.
 */
public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BadgeAdapter.class);
    List<Badge> records;

    public BadgeAdapter(List<Badge> records) {
        this.records = records;
    }

    @Override
    public BadgeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.template_list_badge, viewGroup, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(BadgeAdapter.ViewHolder viewHolder, int position) {
        viewHolder.update(null, records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    /**
     * This class simply holds view of an item in list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView formulaView;
        ImageView typeView;
        ImageView statusView;

        /**
         * Creates new ViewHolder and init look with data from Badge
         * @param view view its views will be injected to these fields
         */
        public ViewHolder(View view) {
            super(view);
            formulaView = (TextView) view.findViewById(R.id.badge_title);
            typeView = (ImageView) view.findViewById(R.id.badge_type);
            statusView = (ImageView) view.findViewById(R.id.badge_status);
        }

        /**
         * Update views (if necessary) that are hold by this ViewHolder
         * @param view the view that views belong to
         * @param badge badge to be displayed on the view
         */
        public void update(View view, Badge badge) {
            formulaView.setText(badge.getTitle());
            switch (badge.getType()) {
                case 'G':
                    typeView.setImageResource(R.drawable.ic_gold_circle);
                    break;
                case 'S':
                    typeView.setImageResource(R.drawable.ic_silver_circle);
                    break;
                default:
                    typeView.setImageResource(R.drawable.ic_bronze_circle);
            }
            if (Misc.getRandom().nextInt(10) > 3) {
                statusView.setImageResource(R.drawable.ic_correct);
            } else {
                statusView.setImageResource(0);
            }
        }
    }
}
