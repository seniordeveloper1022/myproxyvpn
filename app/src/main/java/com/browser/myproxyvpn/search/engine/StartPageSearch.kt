package com.browser.myproxyvpn.search.engine

import com.browser.myproxyvpn.R
import com.browser.myproxyvpn.constant.Constants

/**
 * The StartPage search engine.
 */
class StartPageSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        Constants.STARTPAGE_SEARCH,
        R.string.search_engine_startpage
)
