package com.crazydude.common.utils;

import android.webkit.CookieManager;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Utils {

    public static boolean hasSession() {
        if (!CookieManager.getInstance().hasCookies()) {
            return false;
        }
        String cookies = CookieManager.getInstance().getCookie("www.lostfilm.tv");
        if (cookies != null) {
            String[] cookieList = cookies.split(";");
            for (String cookie : cookieList) {
                String name = cookie.split("=")[0].trim();
                String value = cookie.split("=")[1].trim();
                if (name.equals("lf_session")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String generatePosterUrl(int id) {
        return String.format("http://static.lostfilm.tv/Images/%d/Posters/poster.jpg", id);
    }

    public static String generatePosterUrl(int id, String season, String episode) {
        return String.format("http://static.lostfilm.tv/Images/%d/Posters/e_%s_%s.jpg", id, season, episode);
    }
}
