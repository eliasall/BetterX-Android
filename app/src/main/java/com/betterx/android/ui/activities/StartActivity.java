package com.betterx.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.ui.fragments.SplashFragment;
import com.betterx.android.utils.AlarmUtils;

import javax.inject.Inject;

public class StartActivity extends BaseActivity {

    @Inject
    PersistentDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);
        getDaggerComponent().inject(this);

        if(savedInstanceState == null) {
            if(dataStore.getUserData() != null && dataStore.getTransmissionSettings() != null) {
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                onReplaceFragment(new SplashFragment(), false);
                AlarmUtils.startRegistrationNotificationAlarm(this);
            }
        }

    }
}
