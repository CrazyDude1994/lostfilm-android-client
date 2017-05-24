package com.crazydude.lostfilmclient.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.MainFragment;
import com.crazydude.lostfilmclient.fragments.TvShowDetailsFragment;
import com.crazydude.lostfilmclient.fragments.WelcomeFragment;
import com.crazydude.lostfilmclient.utils.DebouncedImageLoader;
import com.crazydude.lostfilmclient.utils.EventBusWrapper;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Crazy on 07.01.2017.
 */

public class MainActivity extends LifecycleActivity {

    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private DebouncedImageLoader mImageLoader;

    @Subscribe
    public void handleEvent(WelcomeFragment.TutorialCompletedEvent event) {
        setContentView(R.layout.activity_main);
    }

    @Subscribe
    public void handleEvent(MainFragment.TvShowClickedEvent event) {
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

    @Subscribe
    public void handleEvent(MainFragment.TvShowSelectedEvent event) {
        mImageLoader.feed(event.getTvShow());
    }

    @Subscribe
    public void handleEvent(TvShowDetailsFragment.TvShowDetailsShowed event) {
        mImageLoader.feed(event.getTvShow());
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

        getLifecycle().addObserver(new EventBusWrapper(this));

        prepareBackgroundManager();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(this);
        mBackgroundManager.attach(getWindow());
        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mImageLoader = new DebouncedImageLoader(this, mBackgroundManager, mMetrics.widthPixels,
                mMetrics.heightPixels, getLifecycle());
    }
}
