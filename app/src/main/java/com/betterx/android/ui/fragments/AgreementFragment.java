package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.betterx.android.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.OnClick;

public abstract class AgreementFragment extends BaseFragment{

    @Bind(R.id.agreements_toolbar)
    Toolbar toolbar;
    @Bind(R.id.agreements_cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.agreements_title)
    TextView agreementsTitle;
    @Bind(R.id.agreements_text)
    TextView agreementsText;

    protected abstract String getAgreementFilename();

    protected abstract String getTitle();

    protected abstract void startNextPage();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_agreement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String agreement = loadAgreement();
        final String title = getTitle();

        agreementsText.setMovementMethod(LinkMovementMethod.getInstance());
        agreementsText.setText(Html.fromHtml(agreement));
        agreementsTitle.setText(title);
        prepareToolbar();
    }

    @OnClick(R.id.agreements_btn_next)
    public void onNextClick() {
        final boolean isAgree = cbAgree.isChecked();
        if(isAgree) {
            startNextPage();
        } else {
            showToast(R.string.agreements_warning_msg);
        }
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

    private String loadAgreement() {
        try {
            final String filename = getAgreementFilename();
            final InputStream stream = getActivity().getAssets().open(filename);
            final int size = stream.available();

            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
