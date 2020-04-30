package com.browser.myproxyvpn.settings.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import javax.inject.Inject;

import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.activity.AppCompatPreferenceActivity;
import com.browser.myproxyvpn.BrowserApp;
import com.browser.myproxyvpn.preference.PreferenceManager;
import com.browser.myproxyvpn.utils.ThemeUtils;

public abstract class ThemableSettingsActivity extends AppCompatPreferenceActivity {

    private int mTheme;

    @Inject PreferenceManager mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BrowserApp.getAppComponent().inject(this);
        mTheme = mPreferences.getUseTheme();

        // set the theme
        if (mTheme == 0) {
            setTheme(R.style.Theme_SettingsTheme);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(ThemeUtils.getPrimaryColor(this)));
        } else if (mTheme == 1) {
            setTheme(R.style.Theme_SettingsTheme_Dark);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(ThemeUtils.getPrimaryColorDark(this)));
        } else if (mTheme == 2) {
            setTheme(R.style.Theme_SettingsTheme_Black);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(ThemeUtils.getPrimaryColorDark(this)));
        }
        super.onCreate(savedInstanceState);

        resetPreferences();
    }

    private void resetPreferences() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mPreferences.getUseBlackStatusBar()) {
                getWindow().setStatusBarColor(Color.BLACK);
            } else {
                getWindow().setStatusBarColor(ThemeUtils.getStatusBarColor(this));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetPreferences();
        if (mPreferences.getUseTheme() != mTheme) {
            restart();
        }
    }

    private void restart() {
        recreate();
    }
}
