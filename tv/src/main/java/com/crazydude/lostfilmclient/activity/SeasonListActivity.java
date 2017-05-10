package com.crazydude.lostfilmclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.SeasonDownloadFragment;
import com.crazydude.lostfilmclient.fragments.SeasonListFragment;
import com.crazydude.lostfilmclient.fragments.SeasonWatchFragment;

/**
 * Created by Crazy on 10.01.2017.
 */

public class SeasonListActivity extends Activity {

    public static final String EXTRA_TVSHOW_ID = "extra_tvshow_id";
    public static final String EXTRA_MODE = "extra_tvshow_mode";

    public static final int MODE_WATCH = 0;
    public static final int MODE_DOWNLOAD = 1;

    private int mTvShowId;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_show);

        Intent intent = getIntent();
        mTvShowId = intent.getIntExtra(EXTRA_TVSHOW_ID, -1);
        mMode = intent.getIntExtra(EXTRA_MODE, -1);
        if (mTvShowId == -1 || mMode == -1) {
            finish();
        }

        SeasonListFragment seasonListFragment = null;

        switch (mMode) {
            case MODE_WATCH:
                seasonListFragment = new SeasonWatchFragment();
                break;
            case MODE_DOWNLOAD:
                seasonListFragment = new SeasonDownloadFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TVSHOW_ID, mTvShowId);
        seasonListFragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, seasonListFragment)
                .commit();
    }
}
