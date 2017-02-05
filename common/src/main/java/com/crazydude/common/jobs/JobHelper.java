package com.crazydude.common.jobs;

import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;

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

    public void scheduleTvShowEpisodeDetailsUpdate(int id) {
        List<Episode> episodes = mDatabaseManager.getOutdatedTvShowEpisodes(id);
        for (Episode episode : episodes) {
            mJobManager.addJobInBackground(new EpisodeDetailsFetchJob(episode.getDetailsId()));
        }
    }

    public void scheduleTvShowsUpdate() {
        mJobManager.addJobInBackground(new TvShowDatabaseFetchJob());
    }

    public void close() {
        mDatabaseManager.close();
        mContext = null;
    }
}
