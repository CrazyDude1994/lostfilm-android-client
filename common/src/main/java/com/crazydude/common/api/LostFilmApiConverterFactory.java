package com.crazydude.common.api;

import android.util.Log;

import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.Season;
import com.crazydude.common.db.models.TvShow;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmList;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Crazy on 08.01.2017.
 */

public class LostFilmApiConverterFactory extends Converter.Factory {

    private LostFilmApiConverterFactory() {
    }

    public static LostFilmApiConverterFactory create() {
        return new LostFilmApiConverterFactory();
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
                        tvShows[i] = new TvShow(id, name, null, null);
                    }

                    return tvShows;
                }
            };
        } else if (type.equals(TvShow.class)) {
            return new Converter<ResponseBody, TvShow>() {
                @Override
                public TvShow convert(ResponseBody value) throws IOException {
                    Document document = Jsoup.parse(value.string());
                    Elements episodes = document.getElementsByClass("t_episode_title");
                    TvShow tvShow = new TvShow(null, null, null, new RealmList<Season>());
                    Pattern pattern = Pattern.compile("ShowAllReleases\\('.+','(.+)','(.+)'\\)");
                    Pattern detailsPattern = Pattern.compile("id=(\\d+)");
                    Season season = null;
                    for (Element episode : episodes) {
                        String episodeName = episode.child(0).child(0).text();
                        String episodeInfo = episode.attr("onclick");
                        String episodeDetail = episode.parent().getElementsByClass("a_details").get(0).attr("href");
                        Matcher matcher = pattern.matcher(episodeInfo);
                        Matcher detailsMatcher = detailsPattern.matcher(episodeDetail);
                        if (matcher.find() && detailsMatcher.find()) {
                            String seasonNumber = matcher.group(1);
                            String episodeNumber = matcher.group(2);
                            int detailsId = Integer.parseInt(detailsMatcher.group(1));

                            if (season != null && !season.getId().equals(seasonNumber)) {
                                season = null;
                            }

                            if (season == null) {
                                season = new Season(seasonNumber, episodeNumber.equals("99"), new RealmList<Episode>(), null);
                                tvShow.getSeasons().add(season);
                            }

                            if (!episodeNumber.equals("99")) {
                                season.getEpisodes().add(new Episode(episodeNumber, episodeName, season, detailsId, null));
                            }
                        } else {
                            Log.e("Parser", episodeInfo);
                        }
                    }
                    String imgUrl = document.getElementsByClass("mid").get(0).getElementsByTag("img").get(0).attr("src");
                    tvShow.setImageUrl("http://www.lostfilm.tv/" + imgUrl);
                    return tvShow;
                }
            };
        } else if (DownloadLink[].class.getCanonicalName().equals(type.toString())) {
            return new Converter<ResponseBody, DownloadLink[]>() {
                @Override
                public DownloadLink[] convert(ResponseBody value) throws IOException {
                    String encodedData = new String(value.bytes(), "Cp1251");
                    Document document = Jsoup.parse(encodedData);
                    Elements links = document.getElementsContainingOwnText("http://");
                    DownloadLink[] downloadLinks = new DownloadLink[links.size()];
                    for (int i = 0; i < links.size(); i++) {
                        downloadLinks[i] = new DownloadLink(links.get(i).text(), links.get(i).parent().parent().parent().ownText());
                    }
                    return downloadLinks;
                }
            };
        } else {
            return null;
        }
    }
}
