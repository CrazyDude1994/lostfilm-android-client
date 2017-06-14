package com.crazydude.common.db;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.crazydude.common.api.Season;
import com.crazydude.common.api.TvShowDetails;
import com.crazydude.common.api.TvShowsResponse;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.TvShow;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Crazy on 09.01.2017.
 */

public class DatabaseManager implements LifecycleObserver {

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
                    updatedTvShow.setDescription(tvShow.getDescription());
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
        return mRealm.where(TvShow.class)
                .equalTo("mId", id)
                .findFirst();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void close() {
        try {
            mRealm.removeAllChangeListeners();
        } catch (IllegalStateException ignored) {

        }
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

    public List<TvShow> getTvShows() {
        return mRealm.where(TvShow.class)
                .findAll();
    }

    public void updateTvShowSeasons(int id, Season[] seasons) {
        mRealm.executeTransaction(realm -> {
            TvShow tvShow = realm.where(TvShow.class)
                    .equalTo("mId", id)
                    .findFirst();
            RealmList<com.crazydude.common.db.models.Season> seasonRealmList = new RealmList<>();

            for (Season season : seasons) {
                if (season.getEpisodes().size() > 0) {
                    String seasonId = season.getEpisodes().get(0).getSeasonId();
                    RealmList<Episode> episodeRealmList = new RealmList<>();
                    for (Season.Episode episode : season.getEpisodes()) {
                        Episode managedEpisode = realm.copyToRealm(new Episode(episode.getId(), episode.getName(), null, 0, null));
                        episodeRealmList.add(managedEpisode);
                    }

                    com.crazydude.common.db.models.Season managedSeason =
                            realm.copyToRealm(new com.crazydude.common.db.models.Season(seasonId, season.getName(), false, episodeRealmList, tvShow));

                    for (Episode episode : managedSeason.getEpisodes()) {
                        episode.setSeason(managedSeason);
                    }

                    seasonRealmList.add(managedSeason);
                }
            }
            tvShow.setSeasons(seasonRealmList);
            realm.copyToRealmOrUpdate(tvShow);
        });
    }

    public void updatTvshowDetails(int id, TvShowDetails tvShowDetails) {
        mRealm.executeTransaction(realm -> {
            TvShow tvShow = realm.where(TvShow.class)
                    .equalTo("mId", id)
                    .findFirst();
            tvShow.setDescription(tvShowDetails.getDescription());
            realm.copyToRealmOrUpdate(tvShow);
        });
    }

    public List<TvShow> searchTvShows(String query) {
        return mRealm.where(TvShow.class)
                .contains("mTitle", query, Case.INSENSITIVE)
                .or()
                .contains("mTitleOriginal", query, Case.INSENSITIVE)
                .findAll();
    }
}