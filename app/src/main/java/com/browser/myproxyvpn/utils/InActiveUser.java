package com.browser.myproxyvpn.utils;

import com.google.gson.annotations.SerializedName;

public class InActiveUser {

    @SerializedName("response_code")
    String response_code;
    @SerializedName("data")
    String data;

    public String getResponse_code() {
        return response_code;
    }

    public String getData() {
        return data;
    }
}
