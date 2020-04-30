package com.browser.myproxyvpn.browser;

import android.support.annotation.NonNull;

import com.browser.myproxyvpn.database.HistoryItem;

public interface BookmarksView {

    void navigateBack();

    void handleUpdatedUrl(@NonNull String url);

    void handleBookmarkDeleted(@NonNull HistoryItem item);

}
