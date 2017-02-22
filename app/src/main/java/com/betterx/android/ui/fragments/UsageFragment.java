package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;

public class UsageFragment extends BaseFragment {

    @Inject
    PersistentDataStore dataStore;

    @Bind(R.id.usage_msg)
    TextView usageMsgView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_usage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String usageMsg = getString(R.string.my_usage_msg, getDaysLeft());
        usageMsgView.setText(usageMsg);
    }

    private int getDaysLeft() {
        final long startCollectingTime = dataStore.getStartCollectingDate();
        final Calendar calCurr = Calendar.getInstance();
        final Calendar day = Calendar.getInstance();
        day.setTimeInMillis(startCollectingTime);
        return calCurr.get(Calendar.DAY_OF_YEAR) - day.get(Calendar.DAY_OF_YEAR);
    }

}
