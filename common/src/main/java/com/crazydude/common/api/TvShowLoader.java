package com.crazydude.common.api;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import retrofit2.Response;

/**
 * Created by Crazy on 08.01.2017.
 */

public class TvShowLoader extends AsyncTaskLoader<List<TvShow>> {

    private LostFilmApi mLostFilmApi;
    private DatabaseManager mDatabaseManager;

    public TvShowLoader(Context context) {
        super(context);

        mLostFilmApi = new LostFilmApi();
        mDatabaseManager = new DatabaseManager();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<TvShow> loadInBackground() {
        try {
            Response<TvShow[]> response = mLostFilmApi.getTvShows().execute();
            ArrayList<TvShow> shows = new ArrayList<>(Arrays.asList(response.body()));
            List<TvShow> tvShows = mDatabaseManager.updateTvShows(shows);
            return tvShows;
        } catch (IOException e) {
            return null;
        }
    }
}
