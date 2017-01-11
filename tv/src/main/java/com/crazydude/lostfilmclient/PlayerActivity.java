package com.crazydude.lostfilmclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crazydude.common.api.DatabaseManager;
import com.crazydude.common.api.LostFilmApi;

/**
 * Created by Crazy on 11.01.2017.
 */

public class PlayerActivity extends Activity {

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";
    public static final String EXTRA_SEASON_ID = "extra_season_id";
    public static final String EXTRA_TV_SHOW_ID = "extra_tv_show_id";

    private int mTvShowId;
    private String mSeasonId;
    private String mEpisodeId;
    private DatabaseManager mDatabaseManager;
    private LostFilmApi mLostFilmApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        mTvShowId = intent.getIntExtra(EXTRA_TV_SHOW_ID, -1);
        if (mTvShowId == -1) {
            finish();
        }
        mEpisodeId = intent.getStringExtra(EXTRA_EPISODE_ID);
        mSeasonId = intent.getStringExtra(EXTRA_SEASON_ID);

        mDatabaseManager = new DatabaseManager();
        mLostFilmApi = LostFilmApi.getInstance();

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
    }

    private void loadData() {

    }
}
