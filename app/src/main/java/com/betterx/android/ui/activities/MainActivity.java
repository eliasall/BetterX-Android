package com.betterx.android.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.betterx.android.R;
import com.betterx.android.app.BetterxApp;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.services.RegistrationIntentService;
import com.betterx.android.ui.fragments.MainMenuFragment;
import com.betterx.android.ui.fragments.MessageFragment;
import com.betterx.android.ui.fragments.ShareFragment;
import com.betterx.android.ui.fragments.SupportFragment;
import com.betterx.android.ui.fragments.UserPanelFragment;
import com.betterx.android.utils.AlarmUtils;
import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.networklogger.data.NetworkLogger;
import com.betterx.sensorlogger.receivers.BootReceiver;
import com.betterx.sensorlogger.utils.CommSharedPreff;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMenuFragment.NavigationDrawerCallbacks,
        FragmentManager.OnBackStackChangedListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Inject
    PersistentDataStore dataStore;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.main_activity_toolbar)
    Toolbar toolbar;
    @Bind(R.id.navigation_drawer)
    View drawerContainer;

    private MainMenuFragment mainMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        BetterxApp.component(getApplicationContext()).inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            AlarmUtils.startUploadAlarm(this, dataStore.getTransmissionSettings());
            AlarmUtils.stopRegistrationNotificationAlarm(this);
            AlarmUtils.startFileZippingAlarm(this);
            subscribeOnNotifications();
            startLoggers();

            onReplaceFragment(new UserPanelFragment(), false);
        }
        setupMainMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (isMainMenuOpen()) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        final int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        final ActionBarDrawerToggle drawerToggle = mainMenuFragment.getDrawerTogle();
        if (backStackEntryCount > 0) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(getBackBtnDrawable());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(mainMenuFragment.getOriginalToolbarListener());
        }
    }

    @Override
    public void onNavigationDrawerItemSelectionChanged(int from, int to) {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            clearStack();
        }

        switch (to) {
            case 0:
                onReplaceFragment(new UserPanelFragment(), false);
                break;
            case 1:
                onReplaceFragment(new MessageFragment(), false);
                break;
            case 2:
                onReplaceFragment(new ShareFragment(), false);
                break;
            case 3:
                onReplaceFragment(new SupportFragment(), false);
                break;
        }
    }

    private boolean isMainMenuOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(drawerContainer);
    }

    private void startLoggers() {
        NetworkLogger.startLogging(this);
        FeatureLogger.startLogging(this);
        // start sensor logging and data upload system
        BootReceiver.startSensorLogger(getApplicationContext());
//            BootReceiver.startUploadAlarm(MainActivity.this.getApplicationContext());
        // save sensor logging stopped state in sharedpreff
        CommSharedPreff.saveBooleanPreferences(CommSharedPreff.spKey_isLoggerStarted, true, getApplicationContext());
    }

    private void setupMainMenu() {
        mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mainMenuFragment.setUp(drawerLayout, toolbar);
    }

    private Drawable getBackBtnDrawable() {
        Drawable drawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), Color.WHITE);
        return drawable;
    }

    private void subscribeOnNotifications() {
        if(isPlayServicesAvailable()) {
            final Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean isPlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                        resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Timber.i("This device is not supported.");
            }
            return false;
        }
        return true;
    }


}
