package com.crazydude.lostfilmclient;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Crazy on 09.01.2017.
 */

public class TvApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
