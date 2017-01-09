package com.crazydude.common.api;

import android.webkit.CookieManager;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Utils {

    public static boolean hasSession() {
        if (!CookieManager.getInstance().hasCookies()) {
            return false;
        }
        String[] cookies = CookieManager.getInstance().getCookie("http://lostfilm.tv").split(";");
        for (String cookie : cookies) {
            String name = cookie.split("=")[0].trim();
            String value = cookie.split("=")[1].trim();
            if (name.equals("uid")) {
                return true;
            }
        }

        return false;
    }
}
