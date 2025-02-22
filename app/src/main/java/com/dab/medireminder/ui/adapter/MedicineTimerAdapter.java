package com.dab.medireminder.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.data.model.MedicineTimer;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicineTimerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context activity;
    private List<MedicineTimer> itemList;
    private MedicineTimerListener itemListener;

    public MedicineTimerAdapter(Context activity, List<MedicineTimer> itemList, MedicineTimerListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_timer, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final MedicineTimer medicineTimer = itemList.get(position);

        itemHolder.tvName.setText(medicineTimer.getName());
        itemHolder.tvDose.setText(medicineTimer.getDose());
        itemHolder.tvTime.setText(medicineTimer.getRepeat());
        itemHolder.tvTimeHour.setText(medicineTimer.getTimer());

        if (!TextUtils.isEmpty((medicineTimer.getIcon()))) {
            Glide.with(activity).load(medicineTimer.getIcon()).into(itemHolder.ivIcon);
        } else {
            itemHolder.ivIcon.setImageResource(R.drawable.img_medicine);
        }

        itemHolder.itemView.setOnClickListener(v -> itemListener.onClickMedicineTimer(medicineTimer));

        itemHolder.btnDelete.setOnClickListener(v -> itemListener.onClickDeleteTimer(medicineTimer, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        RoundedImageView ivIcon;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_dose)
        TextView tvDose;

        @BindView(R.id.tv_time)
        TextView tvTime;

        @BindView(R.id.tv_time_hour)
        TextView tvTimeHour;

        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface MedicineTimerListener {
        void onClickMedicineTimer(MedicineTimer medicineTimer);

        void onClickDeleteTimer(MedicineTimer medicineTimer, int position);
    }

}

