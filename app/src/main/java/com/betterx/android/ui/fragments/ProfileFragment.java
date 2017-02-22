package com.betterx.android.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betterx.android.R;
import com.betterx.featureslogger.data.UIDGenerator;

import butterknife.Bind;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment {

    @Bind(R.id.profile_device_id)
    TextView deviceId;
    @Bind(R.id.profile_status)
    TextView status;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String id = UIDGenerator.getUID(getActivity());
        deviceId.setText(getString(R.string.device_id, id));
        status.setText(getString(R.string.status, getString(R.string.collecting_data)));
    }

    @OnClick(R.id.profile_device_id)
    public void sendDataToServer() {
        final String uid = UIDGenerator.getUID(getActivity());
        final ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = android.content.ClipData.newPlainText("uid", uid);
        clipboard.setPrimaryClip(clip);

        showToast(R.string.uid_copied_msg);
    }

}
