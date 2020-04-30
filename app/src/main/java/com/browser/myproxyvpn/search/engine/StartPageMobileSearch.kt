package com.browser.myproxyvpn.search.engine

import com.browser.myproxyvpn.R
import com.browser.myproxyvpn.constant.Constants

/**
 * The StartPage mobile search engine.
 */
class StartPageMobileSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        Constants.STARTPAGE_MOBILE_SEARCH,
        R.string.search_engine_startpage_mobile
)
