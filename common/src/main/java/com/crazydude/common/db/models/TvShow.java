package com.crazydude.common.db.models;

import com.crazydude.common.api.TvShowsResponse;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Crazy on 08.01.2017.
 */

public class TvShow extends RealmObject {

    @PrimaryKey
    private int mId;
    private String mAlias;
    private String mChannels;
    private String mDate;
    private String mGenres;
    private int mHasIcon;
    private boolean mHasImage;
    private String mImage;
    private String mLink;
    private boolean mNotFavorited;
    private float mRating;
    private String mTitle;
    private String mTitleOriginal;
    private RealmList<Season> mSeasons;

    public TvShow(TvShowsResponse.TvShow tvShow) {
        mId = tvShow.getId();
        mAlias = tvShow.getAlias();
        mChannels = tvShow.getChannels();
        mDate = tvShow.getDate();
        mGenres = tvShow.getGenres();
        mHasIcon = tvShow.getHasIcon();
        mHasImage = tvShow.isHasImage();
        mImage = "http:" + tvShow.getImage();
        mLink = tvShow.getLink();
        mNotFavorited = tvShow.isNotFavorited();
        mRating = tvShow.getRating();
        mTitle = tvShow.getTitle();
        mTitleOriginal = tvShow.getTitleOriginal();
    }

    public TvShow() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public String getChannels() {
        return mChannels;
    }

    public void setChannels(String channels) {
        mChannels = channels;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        mGenres = genres;
    }

    public int getHasIcon() {
        return mHasIcon;
    }

    public void setHasIcon(int hasIcon) {
        mHasIcon = hasIcon;
    }

    public boolean isHasImage() {
        return mHasImage;
    }

    public void setHasImage(boolean hasImage) {
        mHasImage = hasImage;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public boolean isNotFavorited() {
        return mNotFavorited;
    }

    public void setNotFavorited(boolean notFavorited) {
        mNotFavorited = notFavorited;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitleOriginal() {
        return mTitleOriginal;
    }

    public void setTitleOriginal(String titleOriginal) {
        mTitleOriginal = titleOriginal;
    }

    public RealmList<Season> getSeasons() {
        return mSeasons;
    }

    public void setSeasons(RealmList<Season> seasons) {
        mSeasons = seasons;
    }
}
