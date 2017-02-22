package com.betterx.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.betterx.android.R;
import com.betterx.android.app.BetterxApp;
import com.betterx.android.dagger.DaggerComponent;
import com.betterx.android.ui.dialogs.AlertDialog;
import com.betterx.android.ui.dialogs.BaseDialogFragment;

import butterknife.ButterKnife;
import timber.log.Timber;

public class BaseFragment extends Fragment {

    private NavigationRequestListener navigationRequestListener;

    private AlertDialog alertDialog;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        Timber.tag("Navigation").v("%s was attached", ((Object) this).getClass().getSimpleName());
        try {
            final AppCompatActivity activity = (AppCompatActivity) context;
            navigationRequestListener = (NavigationRequestListener) activity;
        } catch(ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " must implement " + NavigationRequestListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag("Navigation").v("%s created", (this).getClass().getSimpleName());
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.tag("Navigation").v("%s resumed", (this).getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.tag("Navigation").v("%s paused", (this).getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag("Navigation").v("%s destroyed", (this).getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.tag("Navigation").v("%s detached", (this).getClass().getSimpleName());
        navigationRequestListener = null;
    }

    public void goBack() {
        hideKeyboard(getView());
        navigationRequestListener.onGoBack();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        navigationRequestListener.onReplaceFragment(fragment, addToBackStack);
    }

    public void replaceFragmentWithAnim(Fragment fragment, boolean addToBackStack) {
        navigationRequestListener.onReplaceFragmentWithAnim(fragment, addToBackStack);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        navigationRequestListener.onAddFragment(fragment, addToBackStack);
    }

    public void showDialog(BaseDialogFragment dialogFragment) {
        navigationRequestListener.showDialogFragment(dialogFragment);
    }

    public void startActivity(Intent intent) {
        navigationRequestListener.onStartActivity(intent);
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService
                    (Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showToast(int stringId) {
        Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(int msgId) {
        showAlert(getString(msgId));
    }

    protected void showAlert(String msg) {
        closeAlert();
        alertDialog = new AlertDialog();
        alertDialog.setAlertMessage(msg);
        showDialog(alertDialog);
    }

    protected void showError(String msg) {
        closeAlert();

        alertDialog = new AlertDialog();
        alertDialog.setAlertMessage(msg);
        alertDialog.setAlertTitle(getString(R.string.native_dialog_error_caption));
        alertDialog.setAlertIconId(android.R.drawable.ic_dialog_alert);
        showDialog(alertDialog);
    }

    protected void closeAlert() {
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    protected DaggerComponent getDaggerComponent() {
        return BetterxApp.component(getActivity());
    }

    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

}
