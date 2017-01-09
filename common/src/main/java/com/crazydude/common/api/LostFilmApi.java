package com.crazydude.common.api;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
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

    public Call<TvShow[]> getTvShows() {
        return mLostFilmService.getTvShows();
    }

    public Call<TvShow> getTvShowData(int id) {
        return mLostFilmService.getTvShow(id);
    }
}
