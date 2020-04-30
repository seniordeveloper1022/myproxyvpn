package com.browser.myproxyvpn.utils;

import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("response_code")
    int response_code;
    @SerializedName("data")
    UserClass data;
    @SerializedName("error")
    String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public UserClass getData() {
        return data;
    }
}
