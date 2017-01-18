package com.crazydude.common.db.models;

import io.realm.RealmObject;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Episode extends RealmObject {

    private String mId;
    private String mName;
    private Season mSeason;
    private int detailsId;
    private String mPosterUrl;

    public Episode() {
    }

    public Episode(String id, String name, Season season, int detailsId, String posterUrl) {
        mId = id;
        mName = name;
        mSeason = season;
        this.detailsId = detailsId;
        mPosterUrl = posterUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Season getSeason() {
        return mSeason;
    }

    public void setSeason(Season season) {
        mSeason = season;
    }

    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }
}
