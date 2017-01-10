package com.crazydude.common.api;

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

    public List<TvShow> updateTvShows(final List<TvShow> tvShows) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (TvShow show : tvShows) {
                    TvShow tvShow = realm.where(TvShow.class)
                            .equalTo("mId", show.getId())
                            .findFirst();

                    if (tvShow != null) {
                        tvShow.setName(show.getName());
                    } else {
                        TvShow realmObject = realm.createObject(TvShow.class, show.getId());
                        realmObject.setName(show.getName());
                        realmObject.setImageUrl(null);
                    }
                }
            }
        });

        RealmResults<TvShow> all = mRealm.where(TvShow.class)
                .findAll();

        ArrayList<TvShow> shows = new ArrayList<>();
        for (TvShow tvShow : all) {
            shows.add(new TvShow(tvShow.getId(), tvShow.getName(), tvShow.getImageUrl()));
        }
        return shows;
    }

    public List<TvShow> getBannerlessTvShows() {
        RealmResults<TvShow> all = mRealm.where(TvShow.class)
                .equalTo("mImageUrl", ((String) null))
                .findAll();
        ArrayList<TvShow> shows = new ArrayList<>();
        for (TvShow tvShow : all) {
            shows.add(new TvShow(tvShow.getId(), tvShow.getName(), tvShow.getImageUrl()));
        }
        return shows;
    }

    public void updateTvShow(final TvShow tvShow) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TvShow tvShowRealm = realm.where(TvShow.class)
                        .equalTo("mId", tvShow.getId())
                        .findFirst();
                tvShowRealm.setImageUrl(tvShow.getImageUrl());
            }
        });
    }

    public TvShow getTvShow(int id) {
        TvShow show = mRealm.where(TvShow.class)
                .equalTo("mId", id)
                .findFirst();
        TvShow tvShow = new TvShow(show.getId(), show.getName(), show.getImageUrl());

        return tvShow;
    }

    public void close() {
        mRealm.close();
    }
}
