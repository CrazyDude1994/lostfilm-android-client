package com.crazydude.common.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Crazy on 08.01.2017.
 */

public interface LostFilmService {

    @GET("serials.php")
    Call<TvShow[]> getTvShows();

    @GET("browse.php")
    Call<TvShow> getTvShow(@Query("cat") int id);
}
