package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.Frequency;
import com.betterx.android.dataModel.UserData;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class SetupUserInfoStep3Fragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    @Inject
    PersistentDataStore persistentDataStore;

    @Bind(R.id.user_info_toolbar)
    Toolbar toolbar;

    @Bind(R.id.user_info_phone_usage)
    SeekBar phoneUsageSeekBar;
    @Bind(R.id.user_info_web_usage)
    SeekBar webUsageSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_setup_other, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        phoneUsageSeekBar.setOnSeekBarChangeListener(this);
        webUsageSeekBar.setOnSeekBarChangeListener(this);
        prepareToolbar();
    }

    @OnClick(R.id.user_info_btn_next)
    public void onNextBtnClick(View v) {
        final UserData userData = getUserData();
        persistentDataStore.saveUserData(userData);
        replaceFragment(new TransmissionSettingsFragment(), true);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final int position = seekBar.getProgress();
        if(position < 13) {
            seekBar.setProgress(0);
        } else if (position <= 38) {
            seekBar.setProgress(25);
        } else if (position <= 63) {
            seekBar.setProgress(50);
        } else if (position <= 88) {
            seekBar.setProgress(75);
        } else {
            seekBar.setProgress(100);
        }
    }

    private UserData getUserData() {
        final UserData userData = persistentDataStore.getUserData();
        userData.phoneUseFrequency = getFrequency(phoneUsageSeekBar);
        userData.webOnPhoneUseFrequency = getFrequency(webUsageSeekBar);
        return userData;
    }

    private Frequency getFrequency(SeekBar seekBar) {
        final int progress = seekBar.getProgress();
        if(progress == 0) {
            return Frequency.RARELY;
        } else if(progress == 25) {
            return Frequency.NOT_OFTEN;
        } else if(progress == 50) {
            return Frequency.OFTEN;
        } else if(progress == 75) {
            return Frequency.VERY_OFTEN;
        } else {
            return Frequency.ADDICTED;
        }
    }

}
