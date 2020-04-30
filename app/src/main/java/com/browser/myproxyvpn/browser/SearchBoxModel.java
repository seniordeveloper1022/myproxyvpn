package com.browser.myproxyvpn.browser;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.BrowserApp;
import com.browser.myproxyvpn.preference.PreferenceManager;
import com.browser.myproxyvpn.utils.UrlUtils;
import com.browser.myproxyvpn.utils.Utils;

/**
 * A UI model for the search box.
 */
public class SearchBoxModel {

    @Inject PreferenceManager mPreferences;
    @Inject Application mApplication;

    @NonNull private final String mUntitledTitle;

    @Inject
    public SearchBoxModel() {
        BrowserApp.getAppComponent().inject(this);
        mUntitledTitle = mApplication.getString(R.string.untitled);
    }

    /**
     * Returns the contents of the search box based on a variety of factors.
     * <ul>
     * <li>The user's preference to show either the URL, domain, or page title</li>
     * <li>Whether or not the current page is loading</li>
     * <li>Whether or not the current page is a Lightning generated page.</li>
     * </ul>
     * This method uses the URL, title, and loading information to determine what
     * should be displayed by the search box.
     *
     * @param url       the URL of the current page.
     * @param title     the title of the current page, if known.
     * @param isLoading whether the page is currently loading or not.
     * @return the string that should be displayed by the search box.
     */
    @NonNull
    public String getDisplayContent(@NonNull String url, @Nullable String title, boolean isLoading) {
        if (UrlUtils.isSpecialUrl(url)) {
            return "";
        } else if (isLoading) {
            return url;
        } else {
            switch (mPreferences.getUrlBoxContentChoice()) {
                default:
                case 0: // Default, show only the domain
                    String domain = Utils.getDomainName(url);
                    return domain != null ? domain : url;
                case 1: // URL, show the entire URL
                    return url;
                case 2: // Title, show the page's title
                    if (!TextUtils.isEmpty(title)) {
                        return title;
                    } else {
                        return mUntitledTitle;
                    }
            }
        }
    }

}
