package com.browser.myproxyvpn.utils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("billing/includes/login_api.php")
    Call<LoginData> getLoginDetails(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("billing/includes/timer_api.php")
    Call<LoginData> getSubscriptionDetail(@Field("email") String email);
}
