package com.crazydude.lostfilmclient.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v17.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crazydude.common.utils.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;


/**
 * Created by Crazy on 09.02.2017.
 */

public class DebouncedImageLoader implements ObservableOnSubscribe<Utils.PosterProvider>, LifecycleObserver {

    private BackgroundManager mBackgroundManager;
    private Lifecycle mLifecycle;
    private ObservableEmitter<Utils.PosterProvider> mEmitter;

    public DebouncedImageLoader(Context context, BackgroundManager backgroundManager, int width, int height, Lifecycle lifecycle) {
        mBackgroundManager = backgroundManager;
        mLifecycle = lifecycle;
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
        if (mEmitter != null && !mEmitter.isDisposed() &&
                mLifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            mEmitter.onNext(posterProvider);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void close() {
        mEmitter.onComplete();
        mEmitter = null;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Utils.PosterProvider> e) throws Exception {
        mEmitter = e;
    }
}
