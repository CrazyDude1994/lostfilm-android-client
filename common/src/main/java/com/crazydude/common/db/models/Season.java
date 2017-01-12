package com.crazydude.common.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Season extends RealmObject {

    RealmList<Episode> mEpisodes;
    private String mId;
    private boolean mHasFullSeasonDownloadUrl;
    private TvShow mTvShow;

    public Season() {
    }

    public Season(String id, boolean hasFullSeasonDownloadUrl, RealmList<Episode> episodes, TvShow tvShow) {
        mId = id;
        mHasFullSeasonDownloadUrl = hasFullSeasonDownloadUrl;
        mEpisodes = episodes;
        mTvShow = tvShow;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean isHasFullSeasonDownloadUrl() {
        return mHasFullSeasonDownloadUrl;
    }

    public void setHasFullSeasonDownloadUrl(boolean hasFullSeasonDownloadUrl) {
        mHasFullSeasonDownloadUrl = hasFullSeasonDownloadUrl;
    }

    public RealmList<Episode> getEpisodes() {
        return mEpisodes;
    }

    public void setEpisodes(RealmList<Episode> episodes) {
        mEpisodes = episodes;
    }

    public TvShow getTvShow() {
        return mTvShow;
    }

    public void setTvShow(TvShow tvShow) {
        mTvShow = tvShow;
    }
}
