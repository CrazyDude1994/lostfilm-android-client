package com.crazydude.common.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Crazy on 08.01.2017.
 */

public class LostFilmApiConverterFactory extends Converter.Factory {

    public static LostFilmApiConverterFactory create() {
        return new LostFilmApiConverterFactory();
    }

    private LostFilmApiConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (TvShow[].class.getCanonicalName().equals(type.toString())) {
            return new Converter<ResponseBody, TvShow[]>() {
                @Override
                public TvShow[] convert(ResponseBody value) throws IOException {
                    Document document = Jsoup.parse(value.string());
                    Elements shows = document.getElementsByClass("mid").get(0).getElementsByClass("bb_a");
                    TvShow[] tvShows = new TvShow[shows.size()];
                    for (int i = 0; i < shows.size(); i++) {
                        Integer id = Integer.valueOf(shows.get(i).attr("href").split("=")[1]);
                        String name = shows.get(i).text();
                        tvShows[i] = new TvShow(id, name, null);
                    }

                    return tvShows;
                }
            };
        } else if (type.equals(TvShow.class)) {
            return new Converter<ResponseBody, TvShow>() {
                @Override
                public TvShow convert(ResponseBody value) throws IOException {
                    Document document = Jsoup.parse(value.string());
                    String imgUrl = document.getElementsByClass("mid").get(0).getElementsByTag("img").get(0).attr("src");
                    return new TvShow(null, null, "http://www.lostfilm.tv/" + imgUrl);
                }
            };
        } else {
            return null;
        }
    }
}
