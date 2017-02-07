package com.crazydude.common.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.db.DatabaseManager;

/**
 * Created by Crazy on 04.02.2017.
 */

public class TvShowSeasonsFetchJob extends Job {

    private int mId;
    private String mAlias;

    public TvShowSeasonsFetchJob(int id, String alias) {
        super(new Params(1).requireNetwork().persist());
        mAlias = alias;
        mId = id;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        LostFilmApi lostFilmApi = LostFilmApi.getInstance();
        DatabaseManager databaseManager = new DatabaseManager();
        lostFilmApi.getTvShowSeasons(mAlias)
                .subscribe(seasons -> {
                    databaseManager.updateTvShowSeasons(mId, seasons);
                }, throwable -> databaseManager.close(), databaseManager::close);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }
}
