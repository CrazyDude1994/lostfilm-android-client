package com.crazydude.common.api;

import java.util.List;

/**
 * Created by Crazy on 06.02.2017.
 */

public class Season {

    private String mName;
    private List<Episode> mEpisodes;

    public Season(String name, List<Episode> episodes) {
        mName = name;
        mEpisodes = episodes;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Episode> getEpisodes() {
        return mEpisodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        mEpisodes = episodes;
    }

    public static class Episode {

        private String mTitle;
        private String mName;
        private String mId;
        private String mSeasonId;

        public Episode(String title, String name, String id, String seasonId) {
            mTitle = title;
            mName = name;
            mId = id;
            mSeasonId = seasonId;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getSeasonId() {
            return mSeasonId;
        }

        public void setSeasonId(String seasonId) {
            mSeasonId = seasonId;
        }
    }
}
