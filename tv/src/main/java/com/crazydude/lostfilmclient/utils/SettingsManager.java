package com.crazydude.lostfilmclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.crazydude.lostfilmclient.fragments.WelcomeFragment;

/**
 * Created by CrazyDude on 6/14/17.
 */

public class SettingsManager {

    private final SharedPreferences mSharedPreferences;

    public SettingsManager(Context context) {
        mSharedPreferences =
                android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean shouldShowWelcomeScreen() {
        return !mSharedPreferences.getBoolean(
                WelcomeFragment.COMPLETED_ONBOARDING_PREF_NAME, false);
    }

    public void onWelcomeScreenCompleted() {
        mSharedPreferences.edit()
                .putBoolean(WelcomeFragment.COMPLETED_ONBOARDING_PREF_NAME, true)
                .apply();
    }

    public boolean removeTorrentAfterStop() {
        return mSharedPreferences.getBoolean("remove_after_stop", false);
    }

    public void setRemoveTorrentAfterStop(boolean remove) {
        mSharedPreferences.edit()
                .putBoolean("remove_after_stop", remove)
                .apply();
    }

    public String getDownloadFolder() {
        return mSharedPreferences.getString("download_folder",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath());
    }

    public void setDownloadDirectory(String downloadDirectory) {
        mSharedPreferences.edit()
                .putString("download_folder", downloadDirectory)
                .apply();
    }
}
