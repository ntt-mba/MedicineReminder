package com.dab.medireminder.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.Advisory;
import com.dab.medireminder.ui.adapter.AdvisoryAdapter;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AdvisoryActivity extends BaseActivity implements AdvisoryAdapter.AdvisoryListener {

    public static final int REQUEST_ADD_ADVISORY = 200;
    public static final int RESULT_ADD_ADVISORY = 201;

    @BindView(R.id.rv_timer)
    RecyclerView rvTimer;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    @BindView(R.id.ll_no_data)
    View llNoData;

    @BindView(R.id.btn_add_medicine)
    FloatingActionButton btnAddMedicine;

    private DBApp dbApp;
    private AdvisoryAdapter advisoryAdapter;

    private List<Advisory> advisoryItem;

    @Override
    public int getLayout() {
        return R.layout.activity_advisory;
    }

    @Override
    public void initView() {
        RecyclerUtils.setupVerticalRecyclerView(this, rvTimer);
    }

    @Override
    public void setEvents() {
    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);
        loadData();
    }

    private void loadData() {
        pbLoading.setVisibility(View.VISIBLE);
        advisoryItem = new ArrayList<>();
        advisoryItem = dbApp.getAdvisory();

        if (advisoryItem == null || advisoryItem.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            rvTimer.setVisibility(View.GONE);
            btnAddMedicine.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            rvTimer.setVisibility(View.VISIBLE);
            btnAddMedicine.setVisibility(View.VISIBLE);
        }

        Collections.sort(advisoryItem, (o1, o2) -> {
            if (o1.getTime() > o2.getTime()) {
                return -1;
            } else if (o1.getTime() < o2.getTime()) {
                return 1;
            }
            return 0;
        });


        advisoryAdapter = new AdvisoryAdapter(this, advisoryItem, this);
        rvTimer.setAdapter(advisoryAdapter);
        pbLoading.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_add_medicine, R.id.btn_timer_new})
    public void addNew() {
        startActivityForResult(new Intent(this, AddAdvisoryActivity.class), REQUEST_ADD_ADVISORY);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ADVISORY && resultCode == RESULT_ADD_ADVISORY) {
            loadData();
        }
    }

    @Override
    public void onClickShowMore(Advisory advisory, int position) {
        if (advisoryItem.size() > position) {
            advisoryItem.get(position).setShowMore(true);
            advisoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickShowImage(Advisory advisory) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.alert_preview_photo, null);
        PhotoView photoView = mView.findViewById(R.id.photo_view);
        CardView btnClose = mView.findViewById(R.id.btn_close);
        Glide.with(this).load(advisory.getImage()).into(photoView);

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        btnClose.setOnClickListener(v -> mDialog.dismiss());
    }

    @Override
    public void onClickDeleteAdvisory(Advisory advisory, int position) {
        if (advisoryItem.size() > position) {
            dbApp.deleteAdvisory(String.valueOf(advisory.getTime()));
            advisoryItem.remove(position);
            advisoryAdapter.notifyDataSetChanged();

            AppUtils.toast(this, "Đã xoá bài viết này !");

            if (advisoryItem == null || advisoryItem.size() == 0) {
                llNoData.setVisibility(View.VISIBLE);
                rvTimer.setVisibility(View.GONE);
                btnAddMedicine.setVisibility(View.GONE);
            } else {
                llNoData.setVisibility(View.GONE);
                rvTimer.setVisibility(View.VISIBLE);
                btnAddMedicine.setVisibility(View.VISIBLE);
            }

        }
    }
}
