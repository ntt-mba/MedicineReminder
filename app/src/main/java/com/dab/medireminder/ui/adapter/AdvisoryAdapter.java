package com.dab.medireminder.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.data.model.Advisory;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvisoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Advisory> itemList;
    private AdvisoryListener itemListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public AdvisoryAdapter(Activity activity, List<Advisory> itemList, AdvisoryListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advisory, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final Advisory advisory = itemList.get(position);

        itemHolder.tv_timer.setText(simpleDateFormat.format(advisory.getTime()));

        if (advisory.isShowMore()) {
            itemHolder.tv_content.setMaxLines(Integer.MAX_VALUE);
            itemHolder.tv_more.setVisibility(View.GONE);
        } else {
            itemHolder.tv_content.setMaxLines(4);
            itemHolder.tv_more.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty((advisory.getContent()))) {
            itemHolder.tv_content.setVisibility(View.VISIBLE);
            itemHolder.tv_content.setText(advisory.getContent());
        } else {
            itemHolder.tv_content.setVisibility(View.GONE);
            itemHolder.tv_more.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty((advisory.getImage()))) {
            itemHolder.cardImage.setVisibility(View.VISIBLE);
            Glide.with(activity).load(advisory.getImage()).into(itemHolder.ivIcon);
        } else {
            itemHolder.cardImage.setVisibility(View.GONE);
        }

        itemHolder.tv_more.setOnClickListener(v -> itemListener.onClickShowMore(advisory, position));

        itemHolder.ivIcon.setOnClickListener(v -> itemListener.onClickShowImage(advisory));

        itemHolder.btnDelete.setOnClickListener(v -> itemListener.onClickDeleteAdvisory(advisory, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        @BindView(R.id.tv_timer)
        TextView tv_timer;

        @BindView(R.id.tv_more)
        TextView tv_more;

        @BindView(R.id.tv_content)
        TextView tv_content;

        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

        @BindView(R.id.card_image)
        CardView cardImage;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface AdvisoryListener {
        void onClickShowMore(Advisory advisory, int position);

        void onClickShowImage(Advisory advisory);

        void onClickDeleteAdvisory(Advisory advisory, int position);
    }

}

