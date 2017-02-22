package com.betterx.android.dagger;

import android.app.Application;
import android.content.Context;

import com.betterx.android.classes.MainThreadBus;
import com.betterx.android.network.ApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseModule {

    private final Application application;

    public BaseModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    ApiClient apiClient() {
        return new ApiClient();
    }

    @Provides
    @Singleton
    MainThreadBus provideOttoBus() {
        return new MainThreadBus();
    }

}
