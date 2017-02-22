package com.betterx.android.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.util.Linkify;
import android.widget.TextView;

import com.betterx.android.R;

public class AlertDialog extends BaseDialogFragment {

    public static final String KEY_ALERT_MSG = "alert_message";
    public static final String KEY_ALERT_TITLE = "alert_title";
    public static final String KEY_ALERT_ICON_ID = "alert_icon_id";
    public static final String KEY_SHOW_NEGATIVE_BTN = "show_negative_btn";

    public static AlertDialog newInstance(String msg) {
        final AlertDialog fragment = new AlertDialog();
        final Bundle args = new Bundle();
        args.putString(KEY_ALERT_MSG, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String alertMsg = getArguments().getString(KEY_ALERT_MSG);
        final String alertTitle = getArguments().getString(KEY_ALERT_TITLE, getString(R.string.native_dialog_information_caption));
        final int iconId = getArguments().getInt(KEY_ALERT_ICON_ID, android.R.drawable.ic_dialog_info);
        final boolean showNegativeBtn = getArguments().getBoolean(KEY_SHOW_NEGATIVE_BTN, false);

        final int padding = getResources().getDimensionPixelSize(R.dimen.alert_dialog_padding);
        final TextView textView = new TextView(getActivity());
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(alertMsg);
        Linkify.addLinks(textView, Linkify.ALL);

        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(getActivity())
                .setIcon(iconId)
                .setTitle(alertTitle)
                .setView(textView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPositiveBtnClick(dialog, which);
                    }
                });

        if(showNegativeBtn) {
            builder.setNegativeButton(android.R.string.cancel, null);
        }

        return builder.create();
    }

    public void showNegativeBtn(boolean show) {
        final Bundle args = getOrCreateArguments();
        args.putBoolean(KEY_SHOW_NEGATIVE_BTN, show);
    }

    public void setAlertMessage(String msg) {
        final Bundle args = getOrCreateArguments();
        args.putString(KEY_ALERT_MSG, msg);
    }

    public void setAlertTitle(String title) {
        final Bundle args = getOrCreateArguments();
        args.putString(KEY_ALERT_TITLE, title);
    }

    public void setAlertIconId(int id) {
        final Bundle args = getOrCreateArguments();
        args.putInt(KEY_ALERT_ICON_ID, id);
    }

    public void onPositiveBtnClick(DialogInterface dialog, int which) {
        dismiss();
    }

}
