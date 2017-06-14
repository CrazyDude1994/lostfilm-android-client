package com.crazydude.common.ads;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by CrazyDude on 5/25/17.
 */

public class AdMob {

    public static void init(Context context) {
        MobileAds.initialize(context, "ca-app-pub-3653607969878790~8802836261");
    }
}
