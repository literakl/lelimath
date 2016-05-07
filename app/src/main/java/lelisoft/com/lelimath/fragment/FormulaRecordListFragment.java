package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.FormulaRecordAdapter;
import lelisoft.com.lelimath.data.FormulaRecord;
import lelisoft.com.lelimath.provider.FormulaRecordProvider;

/**
 * Lists formula records from a database
 * Created by Leoš on 06.05.2016.
 */
public class FormulaRecordListFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DashboardHomeFragment.class);

    FragmentActivity activity;
    FormulaRecordProvider recordsProvider;
    FormulaRecordAdapter recordsListAdapter;
    List<FormulaRecord> records;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");

        View view = inflater.inflate(R.layout.fragment_dashboard_log, container, false);
        activity = getActivity();
        recordsProvider = new FormulaRecordProvider(getActivity());
        records = recordsProvider.getAll();
        recordsListAdapter = new FormulaRecordAdapter(activity, records);

        ListView listView = (ListView) view.findViewById(R.id.log_listView);
        listView.setAdapter(recordsListAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);
    }

    public static Fragment newInstance() {
        return new FormulaRecordListFragment();
    }
}
