package lelisoft.com.lelimath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.adapter.JournalAdapter;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.helpers.Metrics;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.PlayRecordProvider;

/**
 * Lists formula records from a database
 * Created by Leoš on 06.05.2016.
 */
public class PlayRecordListFragment extends LeliBaseFragment {
    private static final Logger log = LoggerFactory.getLogger(DashboardHomeFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log.debug("onCreateView()");
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.frg_dashboard_log, container, false);
        FragmentActivity activity = getActivity();

        PlayRecordProvider recordsProvider = new PlayRecordProvider(activity);
        List<PlayRecord> playRecords = recordsProvider.getAllInDescendingOrder();
        BadgeAwardProvider awardProvider = new BadgeAwardProvider(activity);
        List<BadgeAward> awardList = awardProvider.getAllInDescendingOrder();
        JournalAdapter adapter = new JournalAdapter(playRecords, awardList);
        recyclerView.setAdapter(adapter);

        return recyclerView;
/*
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onTouchEvent(RecyclerView recycler, MotionEvent event) {
                // Handle on touch events here
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recycler, MotionEvent event) {
                return false;
            }
        });
*/

/*
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                log.trace("onLoadMore({},{})", page, totalItemsCount);
                // fetch data here
                customLoadMoreDataFromApi(page);
                // update the adapter, saving the last known size
                int curSize = adapter.getItemCount();
                records.addAll(moreContacts);
                // for efficiency purposes, only notify the adapter of what elements that got changed
                // curSize will equal to the index of the first element inserted because the list is 0-indexed
                adapter.notifyItemRangeInserted(curSize, records.size() - 1);
            }
        });
*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        log.debug("onActivityCreated()");
        super.onActivityCreated(state);
        Metrics.saveContentDisplayed("dashboard", "records");
    }

/*
    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and addGame it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }
*/

    public static Fragment newInstance() {
        return new PlayRecordListFragment();
    }
}
