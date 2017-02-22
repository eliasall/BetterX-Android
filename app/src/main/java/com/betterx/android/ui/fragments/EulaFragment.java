package com.betterx.android.ui.fragments;

import com.betterx.android.R;

public class EulaFragment extends AgreementFragment {

    public static final String EULA_FILENAME = "eula.txt";

    @Override
    protected String getAgreementFilename() {
        return EULA_FILENAME;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.eula);
    }

    @Override
    protected void startNextPage() {
        replaceFragment(new SetupComponentsFragment(), true);
    }

}
