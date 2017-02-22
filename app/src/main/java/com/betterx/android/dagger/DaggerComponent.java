package com.betterx.android.dagger;

import com.betterx.android.app.BetterxApp;
import com.betterx.android.receivers.NeedRegistrationAlarmReceiver;
import com.betterx.android.receivers.PhoneBootReceiver;
import com.betterx.android.receivers.UploadReceiver;
import com.betterx.android.services.BetterXGCMListenerService;
import com.betterx.android.services.RegistrationIntentService;
import com.betterx.android.ui.activities.MainActivity;
import com.betterx.android.ui.activities.StartActivity;
import com.betterx.android.ui.fragments.MainMenuFragment;
import com.betterx.android.ui.fragments.MessageFragment;
import com.betterx.android.ui.fragments.SetupFinishedFragment;
import com.betterx.android.ui.fragments.SetupUserInfoStep1Fragment;
import com.betterx.android.ui.fragments.SetupUserInfoStep2Fragment;
import com.betterx.android.ui.fragments.SetupUserInfoStep3Fragment;
import com.betterx.android.ui.fragments.ShareContactsFragment;
import com.betterx.android.ui.fragments.ShareFragment;
import com.betterx.android.ui.fragments.TransmissionSettingsFragment;
import com.betterx.android.ui.fragments.UsageFragment;
import com.betterx.android.ui.fragments.UserInfoSetupFragment;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {BaseModule.class})
public interface DaggerComponent {

    public final static class Initializer {

        public static DaggerComponent init(BetterxApp app) {
            return DaggerDaggerComponent.builder()
                    .baseModule(new BaseModule(app))
                    .build();
        }

    }

    void inject(UserInfoSetupFragment fragment);

    void inject(TransmissionSettingsFragment fragment);

    void inject(StartActivity activity);

    void inject(ShareContactsFragment shareContactsFragment);

    void inject(ShareFragment shareFragment);

    void inject(MessageFragment messageFragment);

    void inject(RegistrationIntentService registrationIntentService);

    void inject(UsageFragment usageFragment);

    void inject(SetupFinishedFragment setupFinishedFragment);

    void inject(BetterXGCMListenerService betterXGCMListenerService);

    void inject(SetupUserInfoStep3Fragment setupUserInfoStep3Fragment);

    void inject(SetupUserInfoStep2Fragment setupUserInfoStep2Fragment);

    void inject(SetupUserInfoStep1Fragment setupUserInfoStep1Fragment);

    void inject(MainMenuFragment mainMenuFragment);

    void inject(PhoneBootReceiver phoneBootReceiver);

    void inject(UploadReceiver uploadReceiver);

    void inject(MainActivity mainActivity);

    void inject(NeedRegistrationAlarmReceiver needRegistrationAlarmReceiver);

}