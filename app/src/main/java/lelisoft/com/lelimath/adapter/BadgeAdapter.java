package lelisoft.com.lelimath.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.BadgeAwardActivity;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.view.BadgeView;

/**
 * Adapter for ListView holding Badges
 * Created by Leoš on 06.05.2016.
 */
public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {
    private static final Logger log = LoggerFactory.getLogger(BadgeAdapter.class);
    List<BadgeView> records;
    CustomItemClickListener listener = new CustomItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Context context = view.getRootView().getContext();
            Intent intent = new Intent(context, BadgeAwardActivity.class);
            intent.putExtra(BadgeAwardActivity.KEY_BADGE, records.get(position).badge);
            context.startActivity(intent);
        }
    };

    public BadgeAdapter(List<BadgeView> records) {
        this.records = records;
    }

    @Override
    public BadgeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.template_list_badge, viewGroup, false);

        final ViewHolder viewHolder = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getLayoutPosition());
            }
        });
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(BadgeAdapter.ViewHolder viewHolder, int position) {
        viewHolder.update(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    /**
     * This class simply holds view of an item in list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView formulaView, newView;
        ImageView typeView, statusView;

        /**
         * Creates new ViewHolder and init look with data from Badge
         * @param view view its views will be injected to these fields
         */
        public ViewHolder(View view) {
            super(view);
            formulaView = (TextView) view.findViewById(R.id.badge_title);
            newView = (TextView) view.findViewById(R.id.badge_new);
            typeView = (ImageView) view.findViewById(R.id.badge_type);
            statusView = (ImageView) view.findViewById(R.id.badge_status);
        }

        /**
         * Update views (if necessary) that are hold by this ViewHolder
         * @param badgeView badge to be displayed on the view
         */
        public void update(BadgeView badgeView) {
            formulaView.setText(badgeView.badge.getTitle());
            typeView.setImageResource(Misc.getBadgeImage(badgeView.badge));
            if (badgeView.awarded) {
                statusView.setImageResource(R.drawable.ic_correct);
            } else {
                statusView.setImageResource(0);
            }
            if (! badgeView.awardedToday) {
                newView.setVisibility(View.GONE);
            } else {
                newView.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
