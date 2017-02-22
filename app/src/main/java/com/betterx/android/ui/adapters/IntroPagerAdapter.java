package com.betterx.android.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.betterx.android.R;
import com.betterx.android.ui.fragments.IntroFragment;

public class IntroPagerAdapter extends FragmentPagerAdapter {

    public static final int PAGES_COUNT = 8;

    private final Context context;

    public IntroPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        final int color = getColor(position);
        final String msg = getMessage(position);
        final boolean showLogo = showLogo(position);
        return IntroFragment.newInstance(color, msg, showLogo);
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    private int getColor(int position) {
        switch (position) {
            case 3:
                return context.getResources().getColor(R.color.intro_bg_orange);
            case 4:
                return context.getResources().getColor(R.color.intro_bg_purple);
            case 5:
                return context.getResources().getColor(R.color.intro_bg_red);
            case 6:
                return context.getResources().getColor(R.color.intro_bg_green);
            default:
                return context.getResources().getColor(R.color.intro_bg_blue);
        }
    }

    private String getMessage(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.page_1_msg);
            case 1:
                return context.getString(R.string.page_2_msg);
            case 2:
                return context.getString(R.string.page_3_msg);
            case 3:
                return context.getString(R.string.page_4_msg);
            case 4:
                return context.getString(R.string.page_5_msg);
            case 5:
                return context.getString(R.string.page_6_msg);
            case 6:
                return context.getString(R.string.page_7_msg);
            case 7:
                return context.getString(R.string.page_8_msg);
        }
        return "";
    }

    private boolean showLogo(int position) {
        return position == 4;
    }
}
