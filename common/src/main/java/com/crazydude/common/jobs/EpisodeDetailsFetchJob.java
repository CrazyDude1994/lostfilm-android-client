package com.crazydude.common.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crazydude.common.api.LostFilmApi;

/**
 * Created by Crazy on 09.01.2017.
 */

public class EpisodeDetailsFetchJob extends Job {
    private int mId;

    public EpisodeDetailsFetchJob(int id) {
        super(new Params(1).requireNetwork().persist());
        mId = id;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        LostFilmApi lostFilmApi = LostFilmApi.getInstance();

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }
}
