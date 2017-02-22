package com.betterx.android.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.betterx.android.BuildConfig;
import com.betterx.android.dagger.DaggerComponent;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BetterxApp extends MultiDexApplication {

    private DaggerComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initTimber();

        buildComponentAndInject();
    }

    /**
     * Init timber library.
     * Timber logs will be shown only in debug mode
     */
    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public void buildComponentAndInject() {
        component = DaggerComponent.Initializer.init(this);
    }

    public static DaggerComponent component(Context context) {
        return ((BetterxApp) context.getApplicationContext()).component;
    }

}
