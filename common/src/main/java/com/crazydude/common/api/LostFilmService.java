package com.crazydude.common.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Crazy on 08.01.2017.
 */

public interface LostFilmService {

    @GET("serials.php")
    Observable<TvShow[]> getTvShows();

    @GET("browse.php")
    Observable<TvShow> getTvShow(@Query("cat") int id);

    @GET("nrdr2.php")
    Observable<String> getTvShowHash(@Query("c") int tvShowId,
                                     @Query("s") String seasonId,
                                     @Query("e") String episodeId);

    @GET("http://retre.org/")
    Observable<DownloadLink[]> getTvShowDownloadLink(@Query("c") int tvShowId,
                                                     @Query("s") String seasonId,
                                                     @Query("e") String episodeId,
                                                     @Query("h") String hash,
                                                     @Query("u") String userId);
}
