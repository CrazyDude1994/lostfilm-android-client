package com.crazydude.lostfilmclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.fragments.TvShowFragment;

/**
 * Created by Crazy on 10.01.2017.
 */

public class TvShowActivity extends Activity {

    public static final String EXTRA_TVSHOW_ID = "extra_tvshow_id";
    private int mTvShowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_show);

        Intent intent = getIntent();
        mTvShowId = intent.getIntExtra(EXTRA_TVSHOW_ID, -1);
        if (mTvShowId == -1) {
            finish();
        }


        TvShowFragment tvShowFragment = new TvShowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TVSHOW_ID, mTvShowId);
        tvShowFragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, tvShowFragment)
                .commit();
    }
}
