package com.crazydude.common.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Crazy on 04.02.2017.
 */

public class TvShowsResponse extends Response {

    @SerializedName("data")
    private List<TvShow> mData;

    public List<TvShow> getData() {
        return mData;
    }

    public static class TvShow {

        @SerializedName("alias")
        private String mAlias;
        @SerializedName("channels")
        private String mChannels;
        @SerializedName("date")
        private String mDate;
        @SerializedName("genres")
        private String mGenres;
        @SerializedName("has_icon")
        private int mHasIcon;
        @SerializedName("has_image")
        private boolean mHasImage;
        @SerializedName("id")
        private int mId;
        @SerializedName("img")
        private String mImage;
        @SerializedName("link")
        private String mLink;
        @SerializedName("not_favorited")
        private boolean mNotFavorited;
        @SerializedName("rating")
        private float mRating;
        @SerializedName("title")
        private String mTitle;
        @SerializedName("title_orig")
        private String mTitleOriginal;

        public String getAlias() {
            return mAlias;
        }

        public String getChannels() {
            return mChannels;
        }

        public String getDate() {
            return mDate;
        }

        public String getGenres() {
            return mGenres;
        }

        public int getHasIcon() {
            return mHasIcon;
        }

        public boolean isHasImage() {
            return mHasImage;
        }

        public int getId() {
            return mId;
        }

        public String getImage() {
            return mImage;
        }

        public String getLink() {
            return mLink;
        }

        public boolean isNotFavorited() {
            return mNotFavorited;
        }

        public float getRating() {
            return mRating;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getTitleOriginal() {
            return mTitleOriginal;
        }
    }
}
