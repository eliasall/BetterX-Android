package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class ShareFragment extends BaseFragment {

    @Inject
    PersistentDataStore dataStore;

    @Bind(R.id.share_score)
    TextView shareScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_share, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int score = dataStore.getSharesCount();
        shareScore.setText(getString(R.string.share_score, score));
    }

    @OnClick(R.id.share_btn)
    protected void onShareClicked() {
        replaceFragment(new ShareContactsFragment(), true);
    }




}
