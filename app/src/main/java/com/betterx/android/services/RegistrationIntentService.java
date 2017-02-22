package com.betterx.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.betterx.android.app.BetterxApp;
import com.betterx.android.app.Constants;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.RegistrationData;
import com.betterx.android.network.ApiClient;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();
    private static final String[] TOPICS = {"global"};

    @Inject
    ApiClient apiClient;
    @Inject
    PersistentDataStore dataStore;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BetterxApp.component(getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                //request gcm registrationId
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(Constants.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Timber.i("GCM Registration Token: " + token);
                sendRegistrationToServer(token);
                dataStore.saveGcmRegId(token);
                subscribeTopics(token);
            }
        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
        }
    }

    /**
     * send gcm registrationId with some additional information to the server
     */
    private void sendRegistrationToServer(String token) {
        final TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        RegistrationData data = new RegistrationData();
        data.androidid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        data.simserial = telephonyManager.getSimSerialNumber();
        data.regId = token;
        if(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            data.imei = telephonyManager.getDeviceId();
            data.meid = "";
        } else {
            data.meid = telephonyManager.getDeviceId();
            data.imei = "";
        }

        apiClient.register(data, new Callback<Object>() {
            @Override
            public void success(Object s, Response response) {
                Timber.d("registered on server: " + response);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, error.getMessage());
            }
        });
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}