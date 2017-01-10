package com.crazydude.common.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Crazy on 08.01.2017.
 */

public class TvShowLoader extends AsyncTaskLoader<List<TvShow>> {

    private LostFilmApi mLostFilmApi;

    public TvShowLoader(Context context) {
        super(context);
        mLostFilmApi = LostFilmApi.getInstance();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<TvShow> loadInBackground() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            Response<TvShow[]> response = mLostFilmApi.getTvShows().execute();
            ArrayList<TvShow> shows = new ArrayList<>(Arrays.asList(response.body()));
            List<TvShow> tvShows = databaseManager.updateTvShows(shows);
            return tvShows;
        } catch (IOException e) {
            return null;
        } finally {
            databaseManager.close();
        }
    }
}
