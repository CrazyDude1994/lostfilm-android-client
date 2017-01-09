package com.crazydude.common.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Crazy on 08.01.2017.
 */

public class LostFilmApi {

    private Retrofit mRetrofit;
    private LostFilmService mLostFilmService;

    public LostFilmApi() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.lostfilm.tv/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(LostFilmApiConverterFactory.create())
                .build();

        mLostFilmService = mRetrofit.create(LostFilmService.class);
    }

    public Call<TvShow[]> getTvShows() {
        return mLostFilmService.getTvShows();
    }

    public Call<TvShow> getTvShowData(int id) {
        return mLostFilmService.getTvShow(id);
    }
}
