package com.crazydude.lostfilmclient;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.crazydude.common.ads.AdMob;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Crazy on 09.01.2017.
 */

public class TvApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded().build());

        AdMob.init(this);
    }
}
