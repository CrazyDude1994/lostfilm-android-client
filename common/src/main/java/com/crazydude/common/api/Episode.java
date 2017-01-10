package com.crazydude.common.api;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Episode extends RealmObject {

    @PrimaryKey
    private String mId;
    private String mName;

    public Episode() {
    }

    public Episode(String id, String name) {
        mId = id;
        mName = name;
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
}
