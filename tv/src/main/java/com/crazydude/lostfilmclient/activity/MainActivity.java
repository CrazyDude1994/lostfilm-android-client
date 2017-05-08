package com.crazydude.lostfilmclient.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.MainFragment;
import com.crazydude.lostfilmclient.fragments.TvShowDetailsFragment;
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

    @Subscribe
    public void handleEvent(MainFragment.OnTvShowSelectedEvent event) {
        TvShowDetailsFragment tvShowDetailsFragment = new TvShowDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("tvshow_id", event.getId());
        tvShowDetailsFragment.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, tvShowDetailsFragment)
                .addToBackStack(null)
                .commit();
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
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.placeholder, new MainFragment())
                    .commit();
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
