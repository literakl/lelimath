package lelisoft.com.lelimath.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.event.DressPartSelectedEvent;
import lelisoft.com.lelimath.gui.DressPartPriceView;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.view.DressPart;

/**
 * Adapter for dress parts selection
 * Created by Leoš on 06.05.2016.
 */
public class DressPartAdapter extends RecyclerView.Adapter<DressPartAdapter.ViewHolder> {
    private static final Logger log = LoggerFactory.getLogger(DressPartAdapter.class);

    DressPart[] parts;
    Context context;
    int balance;

    CustomItemClickListener listener = new CustomItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            log.debug("onItemClick {}", position);
            DressPart part = parts[position];
            EventBus.getDefault().post(new DressPartSelectedEvent(part));
        }
    };

    public DressPartAdapter(Context context, DressPart[] parts, int balance) {
        this.parts = parts;
        this.context = context;
        this.balance = balance;
    }

    @Override
    public DressPartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tmpl_dress_part, viewGroup, false);

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
    public void onBindViewHolder(DressPartAdapter.ViewHolder vh, int position) {
        DressPart dressPart = parts[position];
        vh.priceView.setText(LeliMathApp.resources.getString(R.string.caption_price, dressPart.getPrice()));
        boolean enabled = dressPart.getPrice() <= balance;
        vh.itemView.setClickable(enabled);
        vh.priceView.setEnabled(enabled);
        Picasso.with(context).load(dressPart.getIcon()).into(vh.imageView);
    }

    @Override
    public int getItemCount() {
        return parts.length;
    }

    /**
     * This class simply holds view of an item in list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        DressPartPriceView priceView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            priceView = (DressPartPriceView) view.findViewById(R.id.part_price);
            imageView = (ImageView) view.findViewById(R.id.part_image);
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
