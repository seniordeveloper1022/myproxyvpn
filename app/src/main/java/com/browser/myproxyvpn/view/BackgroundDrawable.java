package com.browser.myproxyvpn.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.utils.ThemeUtils;

public class BackgroundDrawable extends TransitionDrawable {

    private boolean mSelected;
    /**
     * Create a new transition drawable with the specified list of layers. At least
     * 2 layers are required for this drawable to work properly.
     */
    public BackgroundDrawable(@NonNull Context context) {
        super(new Drawable[]{new ColorDrawable(ContextCompat.getColor(context, R.color.transparent)),
            new ColorDrawable(ThemeUtils.getColor(context, R.attr.selectedBackground))});
    }

    @Override
    public void startTransition(int durationMillis) {
        if (!mSelected) {
            super.startTransition(durationMillis);
        }
        mSelected = true;
    }

    @Override
    public void reverseTransition(int duration) {
        if (mSelected) {
            super.reverseTransition(duration);
        }
        mSelected = false;
    }

}
