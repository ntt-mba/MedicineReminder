package com.dab.medireminder.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dab.medireminder.utils.AppUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getLayout();

    public abstract void initView();

    public abstract void setEvents();

    public abstract void setData();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        AppUtils.setTransparentStatusBar(this);
        initView();
        setData();
        setEvents();
    }

}
