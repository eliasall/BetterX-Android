package com.betterx.android.ui.fragments;

import com.betterx.android.R;

public class PrivacyPolicyFragment extends AgreementFragment {

    public static final String PRIVACY_POLICY_FILENAME = "privacy_policy.txt";

    @Override
    protected String getAgreementFilename() {
        return PRIVACY_POLICY_FILENAME;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.privacy_policy);
    }

    @Override
    protected void startNextPage() {
        replaceFragment(new EulaFragment(), true);
    }

}
