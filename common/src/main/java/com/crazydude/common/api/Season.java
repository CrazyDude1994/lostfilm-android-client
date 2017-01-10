package com.crazydude.common.api;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Season extends RealmObject {

    RealmList<Episode> mEpisodes;
    @PrimaryKey
    private String mId;
    private boolean mHasFullSeasonDownloadUrl;

    public Season() {
    }

    public Season(String id, boolean hasFullSeasonDownloadUrl, RealmList<Episode> episodes) {
        mId = id;
        mHasFullSeasonDownloadUrl = hasFullSeasonDownloadUrl;
        mEpisodes = episodes;
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
}
