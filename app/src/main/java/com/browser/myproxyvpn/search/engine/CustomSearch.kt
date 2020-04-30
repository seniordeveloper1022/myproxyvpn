package com.browser.myproxyvpn.search.engine

import com.browser.myproxyvpn.R

/**
 * A custom search engine.
 */
class CustomSearch(queryUrl: String) : BaseSearchEngine(
        "file:///android_asset/lightning.png",
        queryUrl,
        R.string.search_engine_custom
)
