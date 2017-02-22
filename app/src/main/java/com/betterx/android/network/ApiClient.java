package com.betterx.android.network;

import com.betterx.android.BuildConfig;
import com.betterx.android.dataModel.RegistrationData;

import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;

public class ApiClient {

    private static final String ENDPOINT = "";

    private BetterXClient client;

    public ApiClient() {
        client = prepareRestClient();
    }

    /**
     * send gcm regid to the server
     */
    public void register(RegistrationData registrationData, Callback<Object> callback) {
        client.register(registrationData.getRegistrationRequestBody(), callback);
    }

    /**
     * send message to the server
     */
    public void sendMessage(Map<String, String> body, Callback<Object> callback) {
        client.sendMessage(body, callback);
    }

    /**
     * prepare api client so we can send gcm regId to the server and send messages
     */
    private BetterXClient prepareRestClient() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(ENDPOINT).build();
        return restAdapter.create(BetterXClient.class);
    }


}
