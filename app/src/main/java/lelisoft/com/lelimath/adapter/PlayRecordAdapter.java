package lelisoft.com.lelimath.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.PlayRecord;

/**
 * Adapter for ListView holding PlayRecords
 * Created by Leoš on 06.05.2016.
 */
public class PlayRecordAdapter extends RecyclerView.Adapter<PlayRecordAdapter.ViewHolder> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayRecordAdapter.class);
    List<PlayRecord> records;

    public PlayRecordAdapter(List<PlayRecord> records) {
        this.records = records;
    }

    @Override
    public PlayRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.template_list_formula_record, viewGroup, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(PlayRecordAdapter.ViewHolder viewHolder, int position) {
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
        TextView dateView;
        TextView formulaView;
        ImageView statusView;

        /**
         * Creates new ViewHolder and init look with data from playRecord
         * @param view view its views will be injected to these fields
         */
        public ViewHolder(View view) {
            super(view);
//            dateView = (TextView) view.findViewById(R.id.fr_date);
            formulaView = (TextView) view.findViewById(R.id.fr_formula);
//            timeSpentView = (TextView) view.findViewById(R.id.fr_time_spent);
            statusView = (ImageView) view.findViewById(R.id.fr_status);
        }

        /**
         * Update views (if necessary) that are hold by this ViewHolder
         * @param view the view that views belong to
         * @param transaction transaction to be displayed on the view
         */
        public void update(View view, PlayRecord transaction) {
//            dateView.setText(String.format(Locale.getDefault(), "%1$td.%1$tm.%1$tY %1$tH:%1$tM", transaction.getDate()));
            formulaView.setText(transaction.getFormulaString());
            statusView.setImageResource(transaction.isCorrect() ? R.drawable.ic_correct : R.drawable.ic_wrong);
        }
    }
}
