package com.dab.medireminder.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.data.model.Medicine;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuggestMedicineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Medicine> itemList;
    private MedicineListener itemListener;

    public SuggestMedicineAdapter(Activity activity, List<Medicine> itemList, MedicineListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_medicine, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final Medicine medicine = itemList.get(position);

        itemHolder.tvName.setText(medicine.getName());
        itemHolder.tvDose.setText(medicine.getDose());

        if (!TextUtils.isEmpty((medicine.getImage()))) {
            Glide.with(activity).load(medicine.getImage()).into(itemHolder.ivIcon);
        } else {
            itemHolder.ivIcon.setImageResource(R.drawable.img_medicine);
        }

        itemHolder.itemView.setOnClickListener(v -> itemListener.onClickMedicine(medicine));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_dose)
        TextView tvDose;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface MedicineListener {
        void onClickMedicine(Medicine medicine);
    }

}

