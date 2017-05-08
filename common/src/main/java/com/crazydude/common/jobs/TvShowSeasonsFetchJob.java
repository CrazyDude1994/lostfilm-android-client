package com.crazydude.common.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.api.Season;
import com.crazydude.common.api.TvShowDetails;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.events.TvShowUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

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
        Observable.zip(lostFilmApi.getTvShowSeasons(mAlias), lostFilmApi.getTvShowDetails(mAlias), new BiFunction<Season[], TvShowDetails, FullTvShowDetails>() {
            @Override
            public FullTvShowDetails apply(@io.reactivex.annotations.NonNull Season[] seasons, @io.reactivex.annotations.NonNull TvShowDetails tvShowDetails) throws Exception {
                return new FullTvShowDetails(seasons, tvShowDetails);
            }
        }).subscribe(fullTvShowDetails -> {
            databaseManager.updateTvShowSeasons(mId, fullTvShowDetails.getSeasons());
            databaseManager.updatTvshowDetails(mId, fullTvShowDetails.getTvShowDetails());
        }, throwable -> databaseManager.close(), databaseManager::close);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    private class FullTvShowDetails {

        private final Season[] mSeasons;
        private final TvShowDetails mTvShowDetails;

        public FullTvShowDetails(Season[] seasons, TvShowDetails tvShowDetails) {
            mSeasons = seasons;
            mTvShowDetails = tvShowDetails;
        }

        public Season[] getSeasons() {
            return mSeasons;
        }

        public TvShowDetails getTvShowDetails() {
            return mTvShowDetails;
        }
    }
}
