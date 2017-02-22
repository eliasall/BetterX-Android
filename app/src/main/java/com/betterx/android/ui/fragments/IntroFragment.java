package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betterx.android.R;

import butterknife.Bind;

public class IntroFragment extends BaseFragment {

    public static final String KEY_COLOR = "bg_color";
    public static final String KEY_TEXT = "text_msg";
    public static final String KEY_SHOW_LOGO = "show_logo";

    @Bind(R.id.intro_message)
    TextView msgView;
    @Bind(R.id.intro_uclan_logo)
    View uclanLogo;

    public static IntroFragment newInstance(int bgColor, String msg, boolean showLogo) {
        final IntroFragment fragment = new IntroFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_COLOR, bgColor);
        args.putString(KEY_TEXT, msg);
        args.putBoolean(KEY_SHOW_LOGO, showLogo);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_intro, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final boolean showLogo = getArguments().getBoolean(KEY_SHOW_LOGO, false);
        final String msg = getArguments().getString(KEY_TEXT, "");
        final int bgColor = getArguments().getInt(KEY_COLOR, 0);
        msgView.setMovementMethod(LinkMovementMethod.getInstance());

        uclanLogo.setVisibility(showLogo ? View.VISIBLE : View.GONE);
        view.setBackgroundColor(bgColor);
        msgView.setText(msg);
    }
}
