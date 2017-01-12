package com.crazydude.common.db.models;

import io.realm.RealmObject;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Episode extends RealmObject {

    private String mId;
    private String mName;
    private Season mSeason;

    public Episode() {
    }

    public Episode(String id, String name, Season season) {
        mId = id;
        mName = name;
        mSeason = season;
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
}
