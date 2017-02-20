package com.crazydude.lostfilmclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v17.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crazydude.common.utils.Utils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Crazy on 09.02.2017.
 */

public class DebouncedImageLoader implements Observable.OnSubscribe<Utils.PosterProvider> {

    private BackgroundManager mBackgroundManager;
    private Subscriber<? super Utils.PosterProvider> mSubscriber;

    public DebouncedImageLoader(Context context, BackgroundManager backgroundManager, int width, int height) {
        mBackgroundManager = backgroundManager;
        Observable.create(this)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posterProvider -> {
                    Glide.with(context)
                            .load(posterProvider.providePosterURL())
                            .asBitmap()
                            .centerCrop()
                            .into(new SimpleTarget<Bitmap>(width, height) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                        glideAnimation) {
                                    mBackgroundManager.setBitmap(resource);
                                }
                            });
                });
    }

    public void feed(Utils.PosterProvider posterProvider) {
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(posterProvider);
        }
    }

    public void close() {
        mSubscriber.onCompleted();
    }

    @Override
    public void call(Subscriber<? super Utils.PosterProvider> subscriber) {
        mSubscriber = subscriber;
    }
}
