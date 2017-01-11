package com.crazydude.lostfilmclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.crazydude.common.api.DatabaseManager;
import com.crazydude.common.api.DownloadLink;
import com.crazydude.common.api.LostFilmApi;

import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Crazy on 11.01.2017.
 */

public class PlayerActivity extends Activity implements Observer<DownloadLink[]> {

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";
    public static final String EXTRA_SEASON_ID = "extra_season_id";
    public static final String EXTRA_TV_SHOW_ID = "extra_tv_show_id";

    private int mTvShowId;
    private String mSeasonId;
    private String mEpisodeId;
    private DatabaseManager mDatabaseManager;
    private LostFilmApi mLostFilmApi;
    private Subscription mSubscription;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(DownloadLink[] downloadLinks) {

    }

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
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private void loadData() {
        mSubscription = mLostFilmApi.getTvShowDownloadLink(mTvShowId, mSeasonId, mEpisodeId)
                .subscribe(new Action1<DownloadLink[]>() {
                    @Override
                    public void call(DownloadLink[] downloadLinks) {
                        Toast.makeText(PlayerActivity.this, downloadLinks[0].getName(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
