package com.dab.medireminder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class ViewUtils {

    public static int getScreenWidth(Activity activity) {
        if (null != activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;
        }
        return 0;
    }

    public static int convertDpToPixel(float dp, Context context) {
        if (context != null) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return (int) px;
        }
        return 0;
    }

    public static void updateBackgroundResourceWithRetainedPadding(View view, int resourceID) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(resourceID);
        view.setPadding(left, top, right, bottom);
    }

    public static void animationRoundImage(ImageView imageView) {
        float deg = (imageView.getRotation() == 180f) ? 0F : 180f;
        imageView.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public static int convertDpToPx(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
