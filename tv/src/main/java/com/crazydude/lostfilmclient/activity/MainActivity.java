package com.crazydude.lostfilmclient.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.WelcomeFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Crazy on 07.01.2017.
 */

public class MainActivity extends Activity {

    @Subscribe
    public void handleEvent(WelcomeFragment.TutorialCompletedEvent event) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(
                WelcomeFragment.COMPLETED_ONBOARDING_PREF_NAME, false)) {
            setContentView(R.layout.activity_main_first_time);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
