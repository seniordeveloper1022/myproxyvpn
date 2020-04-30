package com.browser.myproxyvpn.di;

import com.browser.myproxyvpn.BrowserApp;
import com.browser.myproxyvpn.browser.BrowserPresenter;
import com.browser.myproxyvpn.browser.SearchBoxModel;
import com.browser.myproxyvpn.browser.TabsManager;
import com.browser.myproxyvpn.browser.activity.BrowserActivity;
import com.browser.myproxyvpn.browser.activity.ThemableBrowserActivity;
import com.browser.myproxyvpn.browser.fragment.BookmarksFragment;
import com.browser.myproxyvpn.browser.fragment.TabsFragment;
import com.browser.myproxyvpn.constant.BookmarkPage;
import com.browser.myproxyvpn.constant.DownloadsPage;
import com.browser.myproxyvpn.constant.HistoryPage;
import com.browser.myproxyvpn.constant.StartPage;
import com.browser.myproxyvpn.dialog.LightningDialogBuilder;
import com.browser.myproxyvpn.download.DownloadHandler;
import com.browser.myproxyvpn.download.LightningDownloadListener;
import com.browser.myproxyvpn.reading.activity.ReadingActivity;
import com.browser.myproxyvpn.search.SearchEngineProvider;
import com.browser.myproxyvpn.search.SuggestionsAdapter;
import com.browser.myproxyvpn.settings.activity.ThemableSettingsActivity;
import com.browser.myproxyvpn.settings.fragment.BookmarkSettingsFragment;
import com.browser.myproxyvpn.settings.fragment.DebugSettingsFragment;
import com.browser.myproxyvpn.settings.fragment.GeneralSettingsFragment;
import com.browser.myproxyvpn.settings.fragment.LightningPreferenceFragment;
import com.browser.myproxyvpn.settings.fragment.PrivacySettingsFragment;
import com.browser.myproxyvpn.utils.ProxyUtils;
import com.browser.myproxyvpn.view.LightningChromeClient;
import com.browser.myproxyvpn.view.LightningView;
import com.browser.myproxyvpn.view.LightningWebClient;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BrowserActivity activity);

    void inject(BookmarksFragment fragment);

    void inject(BookmarkSettingsFragment fragment);

    void inject(LightningDialogBuilder builder);

    void inject(TabsFragment fragment);

    void inject(LightningView lightningView);

    void inject(ThemableBrowserActivity activity);

    void inject(LightningPreferenceFragment fragment);

    void inject(BrowserApp app);

    void inject(ProxyUtils proxyUtils);

    void inject(ReadingActivity activity);

    void inject(LightningWebClient webClient);

    void inject(ThemableSettingsActivity activity);

    void inject(LightningDownloadListener listener);

    void inject(PrivacySettingsFragment fragment);

    void inject(StartPage startPage);

    void inject(HistoryPage historyPage);

    void inject(BookmarkPage bookmarkPage);

    void inject(DownloadsPage downloadsPage);

    void inject(BrowserPresenter presenter);

    void inject(TabsManager manager);

    void inject(DebugSettingsFragment fragment);

    void inject(SuggestionsAdapter suggestionsAdapter);

    void inject(LightningChromeClient chromeClient);

    void inject(DownloadHandler downloadHandler);

    void inject(SearchBoxModel searchBoxModel);

    void inject(SearchEngineProvider searchEngineProvider);

    void inject(GeneralSettingsFragment generalSettingsFragment);

}
