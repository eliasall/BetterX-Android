package com.betterx.android.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.betterx.android.ui.dialogs.BaseDialogFragment;

public interface NavigationRequestListener {

    void onReplaceFragment(Fragment fragment, boolean addToBackStack);

    void onReplaceFragmentWithAnim(Fragment fragment, boolean addToBackStack);

    void onAddFragment(Fragment fragment, boolean addToBackStack);

    void onStartActivity(Intent intent);

    void onGoBack();

    void showDialogFragment(BaseDialogFragment dialogFragment);

}
