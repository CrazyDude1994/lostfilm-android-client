package com.crazydude.common.api;

import android.webkit.CookieManager;

import com.crazydude.api.Api;
import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.models.LoginResult;
import com.crazydude.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Crazy on 08.01.2017.
 */
public class LostFilmApi implements Api {

    private static final LostFilmApi mLostFilmApi = new LostFilmApi();
    private final LostFilmService mLostFilmServiceGsonless;
    private Retrofit mRetrofit;
    private Retrofit mRetrofitGsonless;
    private LostFilmService mLostFilmService;

    public LostFilmApi() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.lostfilm.tv/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(LostFilmApiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        for (Cookie cookie : cookies) {
                            cookieManager.setCookie(url.host(), cookie.name() + "=" + cookie.value());
                        }
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

        mRetrofitGsonless = new Retrofit.Builder()
                .baseUrl("https://www.lostfilm.tv/")
                .addConverterFactory(LostFilmApiConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        for (Cookie cookie : cookies) {
                            cookieManager.setCookie(url.host(), cookie.name() + "=" + cookie.value());
                        }
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
        mLostFilmServiceGsonless = mRetrofitGsonless.create(LostFilmService.class);
    }

    public static LostFilmApi getInstance() {
        return mLostFilmApi;
    }

    public Observable<List<TvShowsResponse.TvShow>> getTvShows(int offset, SearchType searchType) {
        return mLostFilmService.getTvShows("serial", "search", offset, searchType.ordinal() + 1, 0)
                .map(TvShowsResponse::getData);
    }

    public Observable<Season[]> getTvShowSeasons(String alias) {
        return mLostFilmServiceGsonless.getTvShowSeasons(alias);
    }

    public Observable<DownloadLink[]> getTvShowDownloadLink(int tvShowId, String seasonId, String episodeId) {
        return mLostFilmServiceGsonless.getTvShowHash(tvShowId, seasonId, episodeId)
                .flatMap(new Function<String, ObservableSource<DownloadLink[]>>() {
                    @Override
                    public ObservableSource<DownloadLink[]> apply(@NonNull String response) throws Exception {
                        Pattern hashPattern = Pattern.compile("h=([\\w\\d]+)");
                        Pattern userIdPattern = Pattern.compile("u=(\\d+)");
                        Matcher hashMatcher = hashPattern.matcher(response);
                        Matcher userIdMatcher = userIdPattern.matcher(response);
                        if (hashMatcher.find() && userIdMatcher.find()) {
                            String hash = hashMatcher.group(1);
                            String userId = userIdMatcher.group(1);
                            return mLostFilmServiceGsonless.getTvShowDownloadLink(tvShowId, seasonId, episodeId, hash, userId);
                        } else {
                            return null;
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<LoginResult> login(User user) {
        return mLostFilmService.login("users", "login", user.getLogin(), user.getPassword(), "1")
                .doOnNext(loginResponse -> {
                    if (loginResponse.getError() != null) {
                        if (loginResponse.getError() == 1) {
                            return;
                        }
                        throw new AuthException();
                    }
                }).map(loginResponse -> new LoginResult(user.getLogin(), user.getPassword(),
                        loginResponse.isSuccess()));
    }

    public enum SearchType {
        RATING, NAME, DATE;
    }
}
