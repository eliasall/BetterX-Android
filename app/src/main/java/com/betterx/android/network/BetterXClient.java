package com.betterx.android.network;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface BetterXClient {

    @FormUrlEncoded
    @POST("/register.php")
    public void register(@FieldMap Map<String, String> params, Callback<Object> callback);

    @FormUrlEncoded
    @POST("/receive_message.php") 
    public void sendMessage(@FieldMap Map<String, String> params, Callback<Object> callback);

}
