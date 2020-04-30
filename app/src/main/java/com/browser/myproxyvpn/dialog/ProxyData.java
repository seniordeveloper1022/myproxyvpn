package com.browser.myproxyvpn.dialog;

import java.util.ArrayList;

public class ProxyData {
    public ProxyList serverlist;
    public class ProxyList {
        public ArrayList<ProxyModel> proxy;
    }

    public ArrayList<ProxyModel> getProxies() {
        return this.serverlist.proxy;
    }
}
