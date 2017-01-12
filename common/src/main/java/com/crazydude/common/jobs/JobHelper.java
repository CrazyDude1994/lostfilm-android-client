package com.crazydude.common.jobs;

import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.common.utils.Utils;

import java.util.List;

/**
 * Created by Crazy on 09.01.2017.
 */

public class JobHelper {

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

    public void scheduleBannerUpdate() {
        if (!Utils.hasSession()) {
            return;
        }
        List<TvShow> tvShows = mDatabaseManager.getBannerlessTvShows();

        for (TvShow tvShow : tvShows) {
            mJobManager.addJobInBackground(new TvShowFetchJob(tvShow.getId()));
        }
    }

    public void close() {
        mDatabaseManager.close();
        mContext = null;
    }
}
