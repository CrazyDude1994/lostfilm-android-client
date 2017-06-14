package com.crazydude.lostfilmclient;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

/**
 * Created by CrazyDude on 6/7/17.
 */

public class CleanPreferencesActivityRule<T extends Activity> extends ActivityTestRule<T> {

    public CleanPreferencesActivityRule(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit().clear().commit();
    }
}
