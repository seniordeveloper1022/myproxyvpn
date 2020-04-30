package com.browser.myproxyvpn.search.engine

import com.browser.myproxyvpn.R
import com.browser.myproxyvpn.constant.Constants

/**
 * The Baidu search engine.
 *
 * See http://www.baidu.com/img/bdlogo.gif for the icon.
 */
class BaiduSearch : BaseSearchEngine(
        "file:///android_asset/baidu.png",
        Constants.BAIDU_SEARCH,
        R.string.search_engine_baidu
)
