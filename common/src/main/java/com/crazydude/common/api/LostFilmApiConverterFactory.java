package com.crazydude.common.api;

import com.crazydude.common.db.models.DownloadLink;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (Season[].class.getCanonicalName().equals(type.toString())) {
            return new Converter<ResponseBody, Season[]>() {
                @Override
                public Season[] convert(ResponseBody value) throws IOException {
                    Document document = Jsoup.parse(value.string());
                    List<Season> seasons = new ArrayList<>();
                    Pattern pattern = Pattern.compile("PlayEpisode\\('.+','(.+)','(.+)'\\)");
                    for (Element season : document.getElementsByClass("serie-block")) {
                        List<Season.Episode> episodes = new ArrayList<>();
                        for (Element episode : season.getElementsByTag("tr")) {
                            String title = episode.getElementsByClass("beta").text();
                            String name = episode.getElementsByClass("gamma").text();
                            Elements ids = episode.getElementsByClass("zeta");
                            if (ids.size() > 0) {
                                String rawIds = ids.get(0).child(0).attr("onclick");
                                Matcher matcher = pattern.matcher(rawIds);
                                if (matcher.find()) {
                                    String seasonId = matcher.group(1);
                                    String episodeId = matcher.group(2);
                                    episodes.add(new Season.Episode(title, name, episodeId, seasonId));
                                }
                            }
                        }
                        String name = season.getElementsByTag("h2").text();
                        seasons.add(new Season(name, episodes));
                    }

                    Season[] seasonsArray = new Season[seasons.size()];
                    for (int i = 0; i < seasons.size(); i++) {
                        seasonsArray[i] = seasons.get(i);
                    }
                    return seasonsArray;
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
        } else if (type.equals(TvShowDetails.class)) {
            return new Converter<ResponseBody, TvShowDetails>() {
                @Override
                public TvShowDetails convert(ResponseBody value) throws IOException {
                    TvShowDetails tvShowDetails = new TvShowDetails(null);
                    Document document = Jsoup.parse(value.string());
                    Elements elements = document.getElementsByClass("text-block description");
                    if (elements.size() == 1) {
                        Elements body = elements.get(0).getElementsByClass("body");
                        if (body.size() > 0) {
                            tvShowDetails.setDescription(body.get(0).text());
                        }
                    }
                    return tvShowDetails;
                }
            };
        } else {
            return null;
        }
    }
}
