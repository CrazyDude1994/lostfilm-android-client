package com.crazydude.common.db;

import com.crazydude.common.api.TvShowsResponse;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.TvShow;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Crazy on 09.01.2017.
 */

public class DatabaseManager {

    private final Realm mRealm;

    public DatabaseManager() {
        mRealm = Realm.getDefaultInstance();
    }

    public void updateTvShows(final List<TvShowsResponse.TvShow> tvShows) {
        mRealm.executeTransaction(realm -> {
            for (TvShowsResponse.TvShow show : tvShows) {
                TvShow tvShow = realm.where(TvShow.class)
                        .equalTo("mId", show.getId())
                        .findFirst();

                if (tvShow == null) {
                    realm.copyToRealm(new TvShow(show));
                } else {
                    TvShow updatedTvShow = new TvShow(show);
                    updatedTvShow.setSeasons(tvShow.getSeasons());
                    realm.copyToRealmOrUpdate(updatedTvShow);
                }
            }
        });
    }

    public void updateTvShow(TvShowsResponse.TvShow tvShow) {
        mRealm.executeTransaction(realm -> {
            TvShow show = realm.where(TvShow.class)
                    .equalTo("mId", tvShow.getId())
                    .findFirst();

            if (show == null) {
                realm.copyToRealm(new TvShow(tvShow));
            } else {
                TvShow updatedTvShow = new TvShow(tvShow);
                updatedTvShow.setSeasons(show.getSeasons());
                realm.copyToRealmOrUpdate(updatedTvShow);
            }
        });
    }

    public List<Episode> getOutdatedTvShowEpisodes(int id) {
        RealmResults<Episode> all = mRealm.where(Episode.class)
                .equalTo("mPosterUrl", (String) null)
                .equalTo("mSeason.mTvShow.mId", id)
                .findAll();

        return new ArrayList<>(all);
    }

    public TvShow getTvShow(int id) {
        TvShow show = mRealm.where(TvShow.class)
                .equalTo("mId", id)
                .findFirst();

        return show;
    }

    public void close() {
        mRealm.close();
    }

    public List<TvShow> getTvShows(List<TvShowsResponse.TvShow> tvShows) {
        Integer[] ids = new Integer[tvShows.size()];
        for (int i = 0; i < tvShows.size(); i++) {
            ids[i] = tvShows.get(i).getId();
        }
        return mRealm.where(TvShow.class)
                .in("mId", ids)
                .findAll();
    }
}
