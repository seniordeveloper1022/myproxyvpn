package com.browser.myproxyvpn.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.browser.myproxyvpn.BrowserApp;
import com.browser.myproxyvpn.database.bookmark.BookmarkDatabase;
import com.browser.myproxyvpn.database.bookmark.BookmarkModel;
import com.browser.myproxyvpn.database.downloads.DownloadsDatabase;
import com.browser.myproxyvpn.database.downloads.DownloadsModel;
import com.browser.myproxyvpn.database.history.HistoryDatabase;
import com.browser.myproxyvpn.database.history.HistoryModel;
import com.browser.myproxyvpn.download.DownloadHandler;

import net.i2p.android.ui.I2PAndroidHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final BrowserApp mApp;

    public AppModule(BrowserApp app) {
        this.mApp = app;
    }

    @Provides
    public Application provideApplication() {
        return mApp;
    }

    @Provides
    public Context provideContext() {
        return mApp.getApplicationContext();
    }

    @NonNull
    @Provides
    @Singleton
    public BookmarkModel provideBookmarkModel() {
        return new BookmarkDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public DownloadsModel provideDownloadsModel() {
        return new DownloadsDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public HistoryModel providesHistoryModel() {
        return new HistoryDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public DownloadHandler provideDownloadHandler() {
        return new DownloadHandler();
    }

    @NonNull
    @Provides
    @Singleton
    public I2PAndroidHelper provideI2PAndroidHelper() {
        return new I2PAndroidHelper(mApp.getApplicationContext());
    }

}
