package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.betterx.android.R;
import com.betterx.android.ui.adapters.IntroPagerAdapter;
import com.betterx.android.ui.views.viewPager.CirclePageIndicator;

import butterknife.Bind;
import butterknife.OnClick;

public class IntroPagerFragment extends BaseFragment {

    @Bind(R.id.intro_view_pager)
    ViewPager pager;
    @Bind(R.id.intro_page_indicator)
    CirclePageIndicator pageIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_intro_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final IntroPagerAdapter adapter = new IntroPagerAdapter(getChildFragmentManager(), getActivity());
        pager.setAdapter(adapter);
        pageIndicator.setViewPager(pager);
    }

    @OnClick(R.id.intro_btn_got_it)
    public void onGotItClick() {
        goBack();
    }

}
