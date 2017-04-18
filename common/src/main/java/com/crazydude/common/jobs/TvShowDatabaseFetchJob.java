package com.crazydude.common.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.events.TvShowsUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;


/**
 * Created by Crazy on 04.02.2017.
 */

public class TvShowDatabaseFetchJob extends Job {

    public TvShowDatabaseFetchJob() {
        super(new Params(1).requireNetwork().persist());
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        LostFilmApi lostFilmApi = LostFilmApi.getInstance();
        DatabaseManager databaseManager = new DatabaseManager();
        Observable.range(0, Integer.MAX_VALUE)
                .concatMap(integer -> lostFilmApi.getTvShows(integer * 10, LostFilmApi.SearchType.NAME))
                .takeWhile(tvShows -> tvShows.size() == 10)
                .subscribe(tvShows -> {
                    databaseManager.updateTvShows(tvShows);
                    EventBus.getDefault().post(new TvShowsUpdateEvent(tvShows));
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
