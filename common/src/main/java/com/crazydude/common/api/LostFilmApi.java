package com.crazydude.common.api;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Crazy on 08.01.2017.
 */

public class LostFilmApi {

    private static final LostFilmApi mLostFilmApi = new LostFilmApi();
    private Retrofit mRetrofit;
    private LostFilmService mLostFilmService;

    private LostFilmApi() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.lostfilm.tv/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(LostFilmApiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        ArrayList<Cookie> cookieList = new ArrayList<>();
                        if (CookieManager.getInstance().hasCookies()) {
                            String[] cookies = CookieManager.getInstance().getCookie(url.host()).split(";");
                            for (String cookie : cookies) {
                                String name = cookie.split("=")[0].trim();
                                String value = cookie.split("=")[1].trim();
                                cookieList.add(new Cookie.Builder().domain(url.host()).name(name).value(value).build());
                            }
                        }
                        return cookieList;
                    }
                }).build())
                .build();

        mLostFilmService = mRetrofit.create(LostFilmService.class);
    }

    public static LostFilmApi getInstance() {
        return mLostFilmApi;
    }

    public Observable<TvShow[]> getTvShows() {
        return mLostFilmService.getTvShows().compose(applySchedulers());
    }

    public Observable<TvShow> getTvShowData(int id) {
        return mLostFilmService.getTvShow(id).compose(applySchedulers());
    }

    public Observable<DownloadLink[]> getTvShowDownloadLink(int tvShowId, String seasonId, String episodeId) {
        mLostFilmService.getTvShowHash(tvShowId, seasonId, episodeId);
        return null;
    }

    <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
