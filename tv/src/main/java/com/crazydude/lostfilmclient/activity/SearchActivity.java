package com.crazydude.lostfilmclient.activity;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.MainFragment;
import com.crazydude.lostfilmclient.fragments.TvShowDetailsFragment;
import com.crazydude.lostfilmclient.utils.EventBusWrapper;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by CrazyDude on 5/28/17.
 */

public class SearchActivity extends LifecycleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        getLifecycle().addObserver(new EventBusWrapper(this));
    }

    @Subscribe
    public void handle(MainFragment.TvShowClickedEvent event) {
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
}
