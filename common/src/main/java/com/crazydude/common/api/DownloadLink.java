package com.crazydude.common.api;

/**
 * Created by Crazy on 11.01.2017.
 */

public class DownloadLink {

    private String mUrl;
    private String mName;

    public DownloadLink(String url, String name) {
        mUrl = url;
        mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
