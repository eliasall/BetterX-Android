package com.betterx.android.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betterx.android.R;

import butterknife.Bind;
import butterknife.OnClick;

public class SetupComponentsFragment extends BaseFragment {

    public static final String FIREFOX_PACKAGE = "org.mozilla.firefox";

    @Bind(R.id.components_toolbar)
    Toolbar toolbar;
    @Bind(R.id.components_firefox)
    TextView tvFirefoxInstalled;
    @Bind(R.id.components_location)
    TextView tvLocationEnabled;
    @Bind(R.id.components_firefox_plugin)
    TextView tvInstallFirefoxPlugin;
    @Bind(R.id.firefox_plugin_description_container)
    View ffPluginDescriptionContainer;

    private FirefoxInstalledReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_setup_components, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        final boolean isLocationEnabled = isLocationEnabled();
        final boolean isFirefoxInstalled = isFirefoxInstalled();
        tvInstallFirefoxPlugin.setText(Html.fromHtml(getString(R.string.install_firefox_plugin_msg)));
        tvInstallFirefoxPlugin.setMovementMethod(LinkMovementMethod.getInstance());
        tvInstallFirefoxPlugin.setAutoLinkMask(Linkify.WEB_URLS);
        tvInstallFirefoxPlugin.setLinksClickable(true);

        tvFirefoxInstalled.setText(isFirefoxInstalled ? R.string.installed : R.string.install);
        tvLocationEnabled.setText(isLocationEnabled ? R.string.enabled : R.string.disabled);
        ffPluginDescriptionContainer.setVisibility(isFirefoxInstalled ? View.VISIBLE : View.GONE);
        tvFirefoxInstalled.setClickable(!isFirefoxInstalled);
        tvLocationEnabled.setClickable(!isLocationEnabled);
//        detectFirefoxPlugin();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @OnClick(R.id.components_btn_next)
    public void onNextBtnClick() {
        if(isLocationEnabled() && isFirefoxInstalled()) {
//            replaceFragment(new UserInfoSetupFragment(), true);
            replaceFragment(new SetupUserInfoStep1Fragment(), true);
        } else {
            showAlert(R.string.setup_components_error_msg);
        }
    }

    @OnClick(R.id.components_firefox)
    public void onInstallFirefoxClick() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + FIREFOX_PACKAGE)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + FIREFOX_PACKAGE)));
            e.printStackTrace();
        }
    }

    @OnClick(R.id.components_location)
    public void onEnableLocationClick() {
        final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
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

    private boolean isLocationEnabled() {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isFirefoxInstalled() {
        try {
            getActivity().getPackageManager().getApplicationInfo(FIREFOX_PACKAGE, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void registerReceiver() {
        receiver = new FirefoxInstalledReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void unregisterReceiver() {
        if(receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    private class FirefoxInstalledReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String dataString = intent.getDataString();
            if(isVisible() && dataString != null
                    && dataString.contains(FIREFOX_PACKAGE) && isFirefoxInstalled()) {
                ffPluginDescriptionContainer.setVisibility(View.VISIBLE);
                tvFirefoxInstalled.setText(R.string.installed);
                showAlert(R.string.install_firefox_plugin_msg);
            }
        }

    }

}
