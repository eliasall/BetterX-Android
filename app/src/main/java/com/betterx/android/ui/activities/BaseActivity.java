package com.betterx.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.betterx.android.R;
import com.betterx.android.app.BetterxApp;
import com.betterx.android.dagger.DaggerComponent;
import com.betterx.android.ui.dialogs.BaseDialogFragment;
import com.betterx.android.ui.fragments.NavigationRequestListener;
import com.betterx.android.utils.FragmentUtils;

import timber.log.Timber;

public class BaseActivity extends AppCompatActivity implements NavigationRequestListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag("Navigation").v("%s created!", (this).getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.tag("Navigation").v("%s resumed", (this).getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.tag("Navigation").v("%s paused", (this).getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.tag("Navigation").v("%s destroyed", (this).getClass().getSimpleName());
    }

    @Override
    public void onReplaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentUtils.commitFragment(getSupportFragmentManager(), R.id.container, fragment, addToBackStack);
    }

    @Override
    public void onReplaceFragmentWithAnim(Fragment fragment, boolean addToBackStack) {
        FragmentUtils.commitFragmentWithAnim(getSupportFragmentManager(), R.id.container, fragment, addToBackStack);
    }

    @Override
    public void onAddFragment(Fragment fragment, boolean addToBackStack) {
        FragmentUtils.addFragment(getSupportFragmentManager(), R.id.container, fragment, addToBackStack);
    }

    @Override
    public void onStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onGoBack() {
        onBackPressed();
    }

    public void clearStack() {
        FragmentUtils.clearFragmentStack(getSupportFragmentManager());
    }

    @Override
    public void showDialogFragment(BaseDialogFragment dialogFragment) {
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
    }

    protected DaggerComponent getDaggerComponent() {
        return BetterxApp.component(getApplication());
    }

}
