package com.dab.medireminder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dab.medireminder.R;
import com.dab.medireminder.data.model.HourTimer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourTimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context activity;
    private List<HourTimer> itemList;
    private HourTimerListener itemListener;

    public HourTimerAdapter(Context activity, List<HourTimer> itemList, HourTimerListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final HourTimer medicineTimer = itemList.get(position);

        if (medicineTimer.isAdd()) {
            itemHolder.btnAdd.setVisibility(View.VISIBLE);
            itemHolder.tvTime.setVisibility(View.GONE);
            itemHolder.btnDelete.setVisibility(View.GONE);

        } else {
            itemHolder.btnAdd.setVisibility(View.GONE);
            itemHolder.tvTime.setVisibility(View.VISIBLE);
            itemHolder.btnDelete.setVisibility(View.VISIBLE);
        }

        itemHolder.tvTime.setText(medicineTimer.getName());

        itemHolder.btnAdd.setOnClickListener(v -> itemListener.onClickAddHourTimer(medicineTimer));

        itemHolder.btnDelete.setOnClickListener(v -> itemListener.onClickDeleteTimer(medicineTimer, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_add)
        RelativeLayout btnAdd;

        @BindView(R.id.tv_time)
        TextView tvTime;

        @BindView(R.id.btn_delete)
        CardView btnDelete;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface HourTimerListener {
        void onClickAddHourTimer(HourTimer medicineTimer);

        void onClickDeleteTimer(HourTimer medicineTimer, int position);
    }

}

