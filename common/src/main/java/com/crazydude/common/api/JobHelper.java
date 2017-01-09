package com.crazydude.common.api;

import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import java.util.List;

/**
 * Created by Crazy on 09.01.2017.
 */

public class JobHelper {

    private Context mContext;

    public JobHelper(Context context) {
        mContext = context;
    }

    public void scheduleBannerUpdate() {
        DatabaseManager databaseManager = new DatabaseManager();
        List<TvShow> tvShows = databaseManager.getBannerlessTvShows();
        JobManager manager = new JobManager(new Configuration.Builder(mContext)
                .build());
        for (TvShow tvShow : tvShows) {
            manager.addJobInBackground(new TvShowFetchJob(tvShow.getId()));
        }
    }
}
