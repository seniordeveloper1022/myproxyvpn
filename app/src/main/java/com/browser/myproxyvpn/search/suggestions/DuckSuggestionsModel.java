package com.browser.myproxyvpn.search.suggestions;

import android.app.Application;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import com.browser.myproxyvpn.R;
import com.browser.myproxyvpn.database.HistoryItem;
import com.browser.myproxyvpn.utils.FileUtils;

/**
 * The search suggestions provider for the DuckDuckGo search engine.
 */
public final class DuckSuggestionsModel extends BaseSuggestionsModel {

    @NonNull private static final String ENCODING = "UTF-8";
    @NonNull private final String mSearchSubtitle;

    public DuckSuggestionsModel(@NonNull Application application) {
        super(application, ENCODING);
        mSearchSubtitle = application.getString(R.string.suggestion);
    }

    @NonNull
    @Override
    protected String createQueryUrl(@NonNull String query, @NonNull String language) {
        return "https://duckduckgo.com/ac/?q=" + query;
    }

    @Override
    protected void parseResults(@NonNull InputStream inputStream, @NonNull List<HistoryItem> results) throws Exception {
        String content = FileUtils.readStringFromStream(inputStream, ENCODING);
        JSONArray jsonArray = new JSONArray(content);
        int counter = 0;
        for (int n = 0, size = jsonArray.length(); n < size; n++) {
            JSONObject object = jsonArray.getJSONObject(n);
            String suggestion = object.getString("phrase");
            results.add(new HistoryItem(mSearchSubtitle + " \"" + suggestion + '"',
                suggestion, R.drawable.ic_search));
            counter++;
            if (counter >= MAX_RESULTS) {
                break;
            }
        }
    }

}
