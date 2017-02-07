package com.crazydude.common.events;

import com.crazydude.common.api.TvShowsResponse;

import java.util.List;

/**
 * Created by Crazy on 09.01.2017.
 */

public class TvShowsUpdateEvent {

    private List<TvShowsResponse.TvShow> mTvShows;

    public TvShowsUpdateEvent(List<TvShowsResponse.TvShow> tvShows) {
        mTvShows = tvShows;
    }

    public List<TvShowsResponse.TvShow> getTvShows() {
        return mTvShows;
    }

    public void setTvShows(List<TvShowsResponse.TvShow> tvShows) {
        mTvShows = tvShows;
    }
}
