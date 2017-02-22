package com.betterx.android.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.betterx.android.R;

import timber.log.Timber;

public class FragmentUtils {

    /**
     * Replace fragment to new one
     * @param fragmentManager - support fragment manager
     * @param containerId - fragment container
     * @param fragment - new fragment
     * @param addToBackStack - add fragment to back stack or not
     */
    public static void commitFragment(FragmentManager fragmentManager, int containerId,
                                      Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        showStackLog(fragmentManager);
    }

    /**
     * Replace fragment to new one with animation
     */
    public static void commitFragmentWithAnim(FragmentManager fragmentManager, int containerId, Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        transaction.setCustomAnimations( R.anim.trans_left_in, R.anim.trans_left_out, R.anim.trans_right_in, R.anim.trans_right_out);
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        showStackLog(fragmentManager);
    }

    public static void addFragment(FragmentManager fragmentManager, int containerId,
                                   Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        transaction.add(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        showStackLog(fragmentManager);
    }


    public static Fragment getFragmentByTag(FragmentManager fragmentManager, Class<?> fragment) {
        return fragmentManager.findFragmentByTag(fragment.getSimpleName());
    }

    public static void clearFragmentStack(FragmentManager supportFragmentManager) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void showStackLog(FragmentManager fragmentManager){
        String logMsg = "BackStack was changed. Count " + fragmentManager.getBackStackEntryCount() + "\n";
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++){
            logMsg += fragmentManager.getBackStackEntryAt(i).getName() + "\n";
        }
        logMsg +="---------------------------------------------------------- \n\n";
        Timber.d(logMsg);

    }
}

