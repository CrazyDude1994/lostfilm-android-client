package com.crazydude.common.api;

import android.webkit.CookieManager;

import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.common.db.models.TvShow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
                        if (CookieManager.getInstance().hasCookies() && CookieManager.getInstance().getCookie(url.host()) != null) {
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
        return mLostFilmService.getTvShowHash(tvShowId, seasonId, episodeId)
                .flatMap(new Func1<String, Observable<DownloadLink[]>>() {
                    @Override
                    public Observable<DownloadLink[]> call(String response) {
                        Pattern hashPattern = Pattern.compile("h=([\\w\\d]+)");
                        Pattern userIdPattern = Pattern.compile("u=(\\d+)");
                        Matcher hashMatcher = hashPattern.matcher(response);
                        Matcher userIdMatcher = userIdPattern.matcher(response);
                        if (hashMatcher.find() && userIdMatcher.find()) {
                            String hash = hashMatcher.group(1);
                            String userId = userIdMatcher.group(1);
                            return mLostFilmService.getTvShowDownloadLink(tvShowId, seasonId, episodeId, hash, userId);
                        } else {
                            return null;
                        }
                    }
                }).compose(applySchedulers());
    }

    <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(3);
    }
}
