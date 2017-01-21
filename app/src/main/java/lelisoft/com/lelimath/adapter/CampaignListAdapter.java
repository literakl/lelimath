package lelisoft.com.lelimath.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.Campaign;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;

/**
 * Provides TestScript data.
 * Created by Leoš on 18.12.2016.
 */

public class CampaignListAdapter extends BaseAdapter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CampaignListAdapter.class);

    private List<Campaign> records;

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

    public CampaignListAdapter(List<Campaign> records) {
        log.debug("CampaignListAdapter()");
        this.records = records;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmpl_campaign, parent, false);
        } else {
            view = convertView;
        }

        Campaign item = records.get(position);
        TextView caption = (TextView) view.findViewById(R.id.caption);
        caption.setText(item.getTitle());

        String pictureName = item.getPicture();
        RoundedImageView roundedPicture = (RoundedImageView) view.findViewById(R.id.picture);
        if (pictureName != null) {
            int resourceId = Misc.getResourceId(pictureName);
            roundedPicture.setImageResource(resourceId);
        } else {
            roundedPicture.setImageResource(R.drawable.bgt_gradient_salmon);
        }

        TextView stats = (TextView) view.findViewById(R.id.stats);
        Integer finished = item.getFinished();
        if (finished == null) {
            finished = 0;
        }
        stats.setText(LeliMathApp.resources.getString(R.string.script_progress, finished, item.getCount()));

        Integer score = item.getScore();
        if (score != null) {
        /*
            no progress - 0 stars
            0 - 59:   1 star
            60 - 89:  2 stars
            90 - 100: 3 stars
        */
            ImageView star1 = (ImageView) view.findViewById(R.id.star1);
            star1.setImageResource(R.drawable.star_on);
            if (score >= 60) {
                ImageView star2 = (ImageView) view.findViewById(R.id.star2);
                star2.setImageResource(R.drawable.star_on);
                if (score >= 90) {
                    ImageView star3 = (ImageView) view.findViewById(R.id.star3);
                    star3.setImageResource(R.drawable.star_on);
                }
            }
        }

        return view;
    }
}
