package lelisoft.com.lelimath.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.FormulaRecord;

/**
 * Adapter for ListView holding FormulaRecords
 * Created by Leoš on 06.05.2016.
 */
public class FormulaRecordAdapter  extends ArrayAdapter<FormulaRecord> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormulaRecordAdapter.class);

    private final LayoutInflater inflater;

    public FormulaRecordAdapter(Activity context, List<FormulaRecord> lst) {
        super(context, R.layout.template_list_formula_record, lst);
        inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FormulaRecord formulaRecord = getItem(position);
        ViewHolder holder;
        if (convertView != null) {
            // recycle unused view
            log.debug("getView({}) - recycle is true", formulaRecord.getId());
            holder = (ViewHolder) convertView.getTag();
        } else {
            // no recycle
            log.debug("getView({}) - recycle is false", formulaRecord.getId());
            convertView = inflater.inflate(R.layout.template_list_formula_record, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.update(convertView, formulaRecord);
        return convertView;
    }

    /**
     * This class simply holds view of an item in list
     */
    static class ViewHolder {
        TextView dateView;
        TextView firstOperandView;
        TextView operatorView;
        TextView secondOperandView;
        TextView resultView;
        TextView timeSpentView;
        TextView statusView;

        /**
         * Creates new ViewHolder and init look with data from formulaRecord
         * @param view view its views will be injected to these fields
         */
        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.fr_date);
            firstOperandView = (TextView) view.findViewById(R.id.fr_first_operand);
            operatorView = (TextView) view.findViewById(R.id.fr_operator);
            secondOperandView = (TextView) view.findViewById(R.id.fr_second_operand);
            resultView = (TextView) view.findViewById(R.id.fr_result);
            timeSpentView = (TextView) view.findViewById(R.id.fr_time_spent);
            statusView = (TextView) view.findViewById(R.id.fr_status);
        }

        /**
         * Update views (if necessary) that are hold by this ViewHolder
         *
         * @param view the view that views belong to
         * @param transaction transaction to be displayed on the view
         */
        public void update(View view, FormulaRecord transaction) {
            dateView.setText(String.format(Locale.getDefault(), "%1$td.%1$tm.%1$tY %1$tH:%1$tM", transaction.getDate()));
            firstOperandView.setText(String.format(Locale.getDefault(), "%d", transaction.getFirstOperand()));
            operatorView.setText(transaction.getOperator().toString());
            secondOperandView.setText(String.format(Locale.getDefault(), "%d", transaction.getSecondOperand()));
            resultView.setText(String.format(Locale.getDefault(), "%d", transaction.getResult()));
        }
    }
}
