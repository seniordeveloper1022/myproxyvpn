package com.browser.myproxyvpn.search.engine

import com.browser.myproxyvpn.R
import com.browser.myproxyvpn.constant.Constants

/**
 * The DuckDuckGo Lite search engine.
 *
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
class DuckLiteSearch : BaseSearchEngine(
        "file:///android_asset/duckduckgo.png",
        Constants.DUCK_LITE_SEARCH,
        R.string.search_engine_duckduckgo_lite
)
