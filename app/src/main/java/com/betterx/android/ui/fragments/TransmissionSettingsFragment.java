package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.TransmissionSettings;
import com.betterx.android.dataModel.TransmissionTime;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class TransmissionSettingsFragment extends BaseFragment {

    @Inject
    PersistentDataStore persistentDataStore;

    @Bind(R.id.transmission_settings_toolbar)
    Toolbar toolbar;
    @Bind(R.id.transmission_settings_charging)
    CheckBox chargingCheckBox;
    @Bind(R.id.transmission_settings_wifi_connected)
    CheckBox wifiConnectedCheckBox;
    @Bind(R.id.transmission_settings_time)
    RadioGroup transmissionSettingsRadioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_transmission_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareToolbar();
    }

    @OnClick(R.id.transmission_settings_btn_next)
    public void onNextBtnClick() {
        TransmissionSettings settings = new TransmissionSettings();
        settings.isCharging = chargingCheckBox.isChecked();
        settings.isWifiConnected = wifiConnectedCheckBox.isChecked();
        settings.transmissionTime = getTransmissionTime();
        persistentDataStore.saveTransmissionSettings(settings);
        replaceFragment(new SetupFinishedFragment(), true);
    }

    private void prepareToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private TransmissionTime getTransmissionTime() {
        switch (transmissionSettingsRadioGroup.getId()) {
            case R.id.transmission_settings_night:
                return TransmissionTime.NIGHT;
            case R.id.transmission_settings_morning:
                return TransmissionTime.MORNING;
            case R.id.transmission_settings_afternoon:
                return TransmissionTime.AFTERNOON;
            default:
                return TransmissionTime.ANYTIME;
        }
    }

}
