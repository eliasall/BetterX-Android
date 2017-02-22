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
import com.betterx.android.services.DataBackupService;
import com.betterx.android.services.ZipAndEncryptService;
import com.betterx.featureslogger.data.UIDGenerator;

import butterknife.Bind;
import butterknife.OnClick;

public class UserPanelFragment extends BaseFragment {

    @Bind(R.id.user_panel_device_id)
    TextView deviceId;
    @Bind(R.id.user_panel_status)
    TextView status;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_user_panel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String id = UIDGenerator.getUID(getActivity());
        deviceId.setText(getString(R.string.device_id, id));
        status.setText(getString(R.string.status, getString(R.string.collecting_data)));

    }

    @OnClick(R.id.user_panel_usage)
    public void onUsageClick() {
        replaceFragment(new UsageFragment(), true);
    }

    @OnClick(R.id.user_panel_profile)
    public void onProfileClick() {
        replaceFragment(new ProfileFragment(), true);
    }

    @OnClick(R.id.user_panel_enhancements)
    public void onEnhancementsClick() {

    }

    @OnClick(R.id.user_panel_device_id)
    public void sendDataToServer() {
        final String uid = UIDGenerator.getUID(getActivity());
        final ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = android.content.ClipData.newPlainText("uid", uid);
        clipboard.setPrimaryClip(clip);

        showToast(R.string.uid_copied_msg);
//        DataBackupService.uploadStats(getActivity());
//        ZipAndEncryptService.zipAndEncryptStats(getActivity());
    }

}
