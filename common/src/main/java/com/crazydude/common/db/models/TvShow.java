package com.crazydude.common.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Crazy on 08.01.2017.
 */

public class TvShow extends RealmObject {

    @PrimaryKey
    @Required
    private Integer mId;
    @Required
    private String mName;
    private String mImageUrl;
    private RealmList<Season> mSeasons;

    public TvShow(Integer id, String name, String imageUrl, RealmList<Season> seasons) {
        mId = id;
        mName = name;
        mImageUrl = imageUrl;
        mSeasons = seasons;
    }

    public TvShow() {
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public RealmList<Season> getSeasons() {
        return mSeasons;
    }

    public void setSeasons(RealmList<Season> seasons) {
        mSeasons = seasons;
    }
}
