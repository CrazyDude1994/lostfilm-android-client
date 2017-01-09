package com.crazydude.common.api;

import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import java.util.List;

/**
 * Created by Crazy on 09.01.2017.
 */

public class JobHelper {

    private final JobManager mJobManager;
    private Context mContext;

    public JobHelper(Context context) {
        mContext = context;
        mJobManager = new JobManager(new Configuration.Builder(mContext)
                .minConsumerCount(5)
                .maxConsumerCount(20)
                .build());
    }

    public void scheduleBannerUpdate() {
        if (!Utils.hasSession()) {
            return;
        }
        DatabaseManager databaseManager = new DatabaseManager();
        List<TvShow> tvShows = databaseManager.getBannerlessTvShows();

        for (TvShow tvShow : tvShows) {
            mJobManager.addJobInBackground(new TvShowFetchJob(tvShow.getId()));
        }
    }
}
