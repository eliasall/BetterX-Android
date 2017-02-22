package com.betterx.android.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.SetupLog;
import com.betterx.android.services.SaveStatsService;
import com.betterx.android.ui.activities.MainActivity;
import com.betterx.android.utils.AlarmUtils;
import com.betterx.featureslogger.data.UIDGenerator;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class SetupFinishedFragment extends BaseFragment {

    private static final long SPLASH_DISPLAY_LENGTH = 3 * 1000;
    private static final String KEY_FINISHED = "finished";

    @Inject
    PersistentDataStore dataStore;

    private boolean finished;
    private ProgressDialog progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_finish_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SetupLog setupLog = new SetupLog(getActivity(), dataStore.getUserData(), dataStore.getTransmissionSettings());
        final Gson gson = new Gson();
        SaveStatsService.saveStats(getActivity(), gson.toJson(setupLog), getSetupFileName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final boolean finished = savedInstanceState != null && savedInstanceState.getBoolean(KEY_FINISHED, false);
        AlarmUtils.stopRegistrationNotificationAlarm(getActivity());
        if(!finished) {
            startSplashTimer();
        } else {
            startMainActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dataStore.saveStartCollectingDate(System.currentTimeMillis());
//        showProgress();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgress();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FINISHED, true);
    }

    private void startSplashTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(isVisible()) {
                    startMainActivity();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void startMainActivity() {
        final Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showProgress() {
        if(progress != null) {
            if(progress.isShowing()) {
                return;
            }
        } else {
            progress = new ProgressDialog(getActivity());
//            progress.setTitle(getString(R.string.progress_title));
            progress.setMessage(getString(R.string.loading));
            progress.setCancelable(false);
        }
        progress.show();
    }

    private void hideProgress() {
        if(progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public String getSetupFileName() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String uid = UIDGenerator.getUID(getActivity());
        return String.format("%s_setup_%s", uid, dateFormat.format(new Date(System.currentTimeMillis())));
    }

}
