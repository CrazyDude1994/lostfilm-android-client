package com.crazydude.lostfilmclient.activity;

import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.MainFragment;
import com.crazydude.lostfilmclient.fragments.TvShowDetailsFragment;
import com.crazydude.lostfilmclient.fragments.WelcomeFragment;
import com.crazydude.lostfilmclient.utils.DebouncedImageLoader;
import com.crazydude.lostfilmclient.utils.EventBusWrapper;
import com.crazydude.lostfilmclient.utils.SettingsManager;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Crazy on 07.01.2017.
 */

public class MainActivity extends FragmentActivity {

    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private DebouncedImageLoader mImageLoader;

    @Subscribe
    public void handleEvent(WelcomeFragment.TutorialCompletedEvent event) {
        switchToMainFragment();
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

        SettingsManager settingsManager = new SettingsManager(getApplicationContext());
        if (settingsManager.shouldShowWelcomeScreen()) {
            setContentView(R.layout.activity_main_first_time);
        } else {
            switchToMainFragment();
        }

        getLifecycle().addObserver(new EventBusWrapper(this));

        prepareBackgroundManager();
    }

    private void switchToMainFragment() {
        setContentView(R.layout.activity_main);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.placeholder, new MainFragment())
                .commit();
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
