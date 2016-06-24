package lelisoft.com.lelimath.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Adapter for ListView holding PlayRecords
 * Created by Leoš on 06.05.2016.
 */
public class PlayRecordAdapter extends RecyclerView.Adapter<PlayRecordAdapter.GenericViewHolder> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayRecordAdapter.class);
    List<Pair<String, PlayRecord>> records;

    public PlayRecordAdapter(List<PlayRecord> playRecords) {
        records = new ArrayList<>((int)(playRecords.size() * 1.25));
        Calendar calendar = Calendar.getInstance();
        long startOfDay = calendar.getTimeInMillis();

        for (PlayRecord record : playRecords) {
            if (record.getDate().getTime() < startOfDay) {
                calendar.setTime(record.getDate());
                Misc.clearTime(calendar);
                startOfDay = calendar.getTimeInMillis();
//                String caption = String.format(Locale.getDefault(), "%1$td.%1$tm.%1$tY", calendar.getTime());
                String caption = DateUtils.formatDateTime(LeliMathApp.getInstance(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
                records.add(new Pair<String, PlayRecord>(caption, null));
            }
            records.add(new Pair<String, PlayRecord>(null, record));
        }
    }

    @Override
    public PlayRecordAdapter.GenericViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == 0) {
            View itemView = inflater.inflate(R.layout.template_list_date_header, viewGroup, false);
            return new SectionViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.template_list_formula_record, viewGroup, false);
            return new PlayRecordViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(PlayRecordAdapter.GenericViewHolder viewHolder, int position) {
        viewHolder.setDataOnView(position);
    }

    @Override
    public int getItemViewType(int position) {
        Pair<String, PlayRecord> pair = records.get(position);
        return pair.first != null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    // http://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type
    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(View itemView) {
            super(itemView);
        }

        public abstract  void setDataOnView(int position);
    }

    /**
     * This class simply holds view of an item in list
     */
    public class SectionViewHolder extends GenericViewHolder {
        TextView dateView;

        /**
         * Creates new ViewHolder and init look with data from playRecord
         * @param view view its views will be injected to these fields
         */
        public SectionViewHolder(View view) {
            super(view);
            dateView = (TextView) view.findViewById(R.id.fr_date);
        }

        @Override
        public void setDataOnView(int position) {
            Pair<String, PlayRecord> pair = records.get(position);
            String caption = pair.first;
            dateView.setText(caption);
        }
    }

    /**
     * This class simply holds view of an item in list
     */
    public class PlayRecordViewHolder extends GenericViewHolder {
        TextView formulaView;
        ImageView statusView;

        /**
         * Creates new ViewHolder and init look with data from playRecord
         * @param view view its views will be injected to these fields
         */
        public PlayRecordViewHolder(View view) {
            super(view);
            formulaView = (TextView) view.findViewById(R.id.fr_formula);
            statusView = (ImageView) view.findViewById(R.id.fr_status);
        }

        @Override
        public void setDataOnView(int position) {
            Pair<String, PlayRecord> pair = records.get(position);
            PlayRecord record = pair.second;
            formulaView.setText(getFormula(record));
            statusView.setImageResource(record.isCorrect() ? R.drawable.ic_correct : R.drawable.ic_wrong);
        }
    }

    private CharSequence getFormula(PlayRecord record) {
        if (record.isCorrect()) {
            return record.getFormulaString();
        } else {
            FormulaPart unknown = record.getUnknown();
            SpannableStringBuilder sb = new SpannableStringBuilder();
            insertMistake(sb, FormulaPart.FIRST_OPERAND, unknown, record.getWrongValue());
            sb.append(record.getFirstOperand().toString()).append(' ');
            insertMistake(sb, FormulaPart.OPERATOR, unknown, record.getWrongValue());
            sb.append(record.getOperator().toString()).append(' ');
            insertMistake(sb, FormulaPart.SECOND_OPERAND, unknown, record.getWrongValue());
            sb.append(record.getSecondOperand().toString()).append(" = ");
            insertMistake(sb, FormulaPart.RESULT, unknown, record.getWrongValue());
            sb.append(record.getResult().toString());
            return sb;
        }
    }

    private void insertMistake(SpannableStringBuilder sb, FormulaPart currentFormulaPart, FormulaPart unknown, String wrongValue) {
        if (currentFormulaPart != unknown) {
            return;
        }

        int mistakeStart = sb.length();
        sb.append(wrongValue);
        sb.setSpan(new StrikethroughSpan(), mistakeStart, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.RED), mistakeStart, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(' ');
    }
}
