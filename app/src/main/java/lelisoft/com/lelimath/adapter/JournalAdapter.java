package lelisoft.com.lelimath.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.activities.BadgeAwardActivity;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.FormulaPart;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Adapter for a ListView holding PlayRecords and BadgeAwards.
 * Created by Leoš on 06.05.2016.
 */
public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.GenericViewHolder> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JournalAdapter.class);

    List<JournalItem> records;

    CustomItemClickListener listener = new CustomItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Context context = view.getRootView().getContext();
            Intent intent = new Intent(context, BadgeAwardActivity.class);
            intent.putExtra(BadgeAwardActivity.KEY_BADGE, records.get(position).badgeAward.getBadge());
            context.startActivity(intent);
        }
    };

    public JournalAdapter(List<PlayRecord> playRecords, List<BadgeAward> awardList) {
        log.debug("JournalAdapter()");
        if (playRecords == null || playRecords.isEmpty()) {
            records = Collections.emptyList();
            return;
        }

        records = new ArrayList<>((int)(playRecords.size() * 1.25 + awardList.size()));
        Calendar calendar = Calendar.getInstance();
        long startOfDay = calendar.getTimeInMillis();

        Iterator<BadgeAward> iterator = awardList.iterator();
        BadgeAward award = iterator.next();
        for (PlayRecord record : playRecords) {
            if (record.getDate().getTime() < startOfDay) {
                calendar.setTime(record.getDate());
                Misc.clearTime(calendar);
                startOfDay = calendar.getTimeInMillis();
                String caption = DateUtils.formatDateTime(LeliMathApp.getInstance(), startOfDay, DateUtils.FORMAT_SHOW_DATE);
                records.add(new JournalItem(new Caption(caption, startOfDay)));
            }

            while (award != null && award.getDate().after(record.getDate())) {
                records.add(new JournalItem(award));
                award = iterator.hasNext() ? iterator.next() : null;
            }

            records.add(new JournalItem(record));
        }
    }

    @Override
    public JournalAdapter.GenericViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case 1: {
                View itemView = inflater.inflate(R.layout.tmpl_list_formula_record, viewGroup, false);
                return new PlayRecordViewHolder(itemView);
            }
            case 0: {
                View itemView = inflater.inflate(R.layout.tmpl_list_date_header, viewGroup, false);
                return new SectionViewHolder(itemView);
            }
            default: {
                View itemView = inflater.inflate(R.layout.tmpl_list_badge_journal, viewGroup, false);
                final BadgeViewHolder viewHolder = new BadgeViewHolder(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, viewHolder.getLayoutPosition());
                    }
                });
                return viewHolder;
            }
        }
    }

    @Override
    public void onBindViewHolder(JournalAdapter.GenericViewHolder viewHolder, int position) {
        viewHolder.setDataOnView(position);
    }

    @Override
    public int getItemViewType(int position) {
        return records.get(position).getViewType();
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
            JournalItem item = records.get(position);
            dateView.setText(item.caption.text);
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
            JournalItem item = records.get(position);
            PlayRecord record = item.playRecord;
            formulaView.setText(getFormula(record));
            statusView.setImageResource(record.isCorrect() ? R.drawable.ic_correct : R.drawable.ic_wrong);
        }
    }

    // what about localization?
    private CharSequence getFormula(PlayRecord record) {
        if (record.isCorrect() || record.getUnknown() == FormulaPart.EXPRESSION) {
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

    /**
     * This class simply holds view of an item in list
     */
    public class BadgeViewHolder extends GenericViewHolder {
        ImageView typeView;
        TextView nameView;

        /**
         * Creates new ViewHolder and init look with data from playRecord
         * @param view view its views will be injected to these fields
         */
        public BadgeViewHolder(View view) {
            super(view);
            typeView = (ImageView) view.findViewById(R.id.badge_type);
            nameView = (TextView) view.findViewById(R.id.badge_title);
        }

        @Override
        public void setDataOnView(int position) {
            JournalItem item = records.get(position);
            typeView.setImageResource(Misc.getBadgeImage(item.badgeAward.getBadge()));
            nameView.setText(item.badgeAward.getBadge().getTitle());
        }
    }

    public static class Caption {
        public long millis;
        public String text;

        public Caption(String text, long millis) {
            this.millis = millis;
            this.text = text;
        }
    }

    public static class JournalItem implements Comparable {
        public Caption caption;
        public PlayRecord playRecord;
        public BadgeAward badgeAward;

        public JournalItem(PlayRecord record) {
            this.playRecord = record;
        }

        public JournalItem(Caption caption) {
            this.caption = caption;
        }

        public JournalItem(BadgeAward badgeAward) {
            this.badgeAward = badgeAward;
        }

        public int getViewType() {
            if (playRecord != null) {
                return 1;
            }
            if (caption != null) {
                return 0;
            }
            return 2;
        }

        @Override
        public int compareTo(@NonNull Object another) {
            long left = getTime(), right = ((JournalItem)another).getTime();
            return (int)(right - left);
        }

        private long getTime() {
            if (playRecord != null) {
                return playRecord.getDate().getTime();
            }
            if (caption != null) {
                return caption.millis;
            }
            return badgeAward.getDate().getTime();
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
