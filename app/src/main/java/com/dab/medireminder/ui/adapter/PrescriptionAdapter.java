package com.dab.medireminder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context activity;
    private List<String> itemList;
    private PrescriptionListener itemListener;

    public PrescriptionAdapter(Context activity, List<String> itemList, PrescriptionListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final String image = itemList.get(position);

        if (image.equals("ADD")) {
            itemHolder.btnAdd.setVisibility(View.VISIBLE);
            itemHolder.btnImage.setVisibility(View.GONE);
        } else {
            itemHolder.btnAdd.setVisibility(View.GONE);
            itemHolder.btnImage.setVisibility(View.VISIBLE);

            Glide.with(activity).load(image).into(itemHolder.ivIcon);
        }

        itemHolder.btnAdd.setOnClickListener(v -> itemListener.onClickAddImage());

        itemHolder.btnImage.setOnClickListener(v -> itemListener.onZoomImage(image));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_image)
        ImageView ivIcon;

        @BindView(R.id.btn_add)
        CardView btnAdd;

        @BindView(R.id.btn_image)
        CardView btnImage;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface PrescriptionListener {
        void onZoomImage(String image);

        void onClickAddImage();

        void onClickDeleteImage(String image, int position);
    }

}

