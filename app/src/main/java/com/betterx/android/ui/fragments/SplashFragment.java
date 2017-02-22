package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.betterx.android.R;

import butterknife.OnClick;

public class SplashFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_splash, container, false);
    }

    @OnClick(R.id.splash_learn_more)
    public void onLearnMoreClick() {
        replaceFragment(new IntroPagerFragment(), true);
    }

    @OnClick(R.id.splash_continue)
    public void onContinueClick() {
        replaceFragment(new PrivacyPolicyFragment(), true);
    }

}
