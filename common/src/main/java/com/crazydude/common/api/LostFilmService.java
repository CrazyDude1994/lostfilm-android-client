package com.crazydude.common.api;

import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.common.db.models.TvShow;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Crazy on 08.01.2017.
 */

public interface LostFilmService {

    @POST("ajaxik.php")
    @FormUrlEncoded
    Observable<LoginResponse> login(@Field("act") String act,
                                    @Field("type") String type,
                                    @Field("mail") String email,
                                    @Field("pass") String password,
                                    @Field("rem") String remember);

    @POST("ajaxik.php")
    @FormUrlEncoded
    Observable<TvShowsResponse> getTvShows(@Field("act") String act,
                                           @Field("type") String type,
                                           @Field("o") int offset,
                                           @Field("s") int searchType,
                                           @Field("t") int t);

    @GET("series/{alias}/seasons")
    Observable<Season[]> getTvShowSeasons(@Path("alias") String alias);

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
