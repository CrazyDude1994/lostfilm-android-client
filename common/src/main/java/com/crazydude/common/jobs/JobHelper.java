package com.crazydude.common.jobs;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.crazydude.common.db.DatabaseManager;

/**
 * Created by Crazy on 09.01.2017.
 */

public class JobHelper implements LifecycleObserver {

    private final JobManager mJobManager;
    private Context mContext;
    private DatabaseManager mDatabaseManager;

    public JobHelper(Context context) {
        mContext = context;
        mJobManager = new JobManager(new Configuration.Builder(mContext)
                .maxConsumerCount(10)
                .build());
        mDatabaseManager = new DatabaseManager();
    }

    public void scheduleTvShowUpdate(int id, String alias) {
        mJobManager.addJobInBackground(new TvShowSeasonsFetchJob(id, alias));
    }

    public void scheduleTvShowsUpdate() {
        mJobManager.addJobInBackground(new TvShowDatabaseFetchJob());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void close() {
        mDatabaseManager.close();
        mContext = null;
    }
}
