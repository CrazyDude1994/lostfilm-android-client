package com.crazydude.common.player;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CrazyDude on 5/14/17.
 */

public class AndroidPlayer implements Player, ExoPlayer.EventListener {

    private SimpleExoPlayer mPlayer;
    private Disposable mProgressDisposable;
    private Listener mListener;

    public AndroidPlayer(Context context, SurfaceHolder surfaceHolder, File videoFile,
                         Listener listener, boolean playWhenReady) {
        mListener = listener;
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        mPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
        mPlayer.setVideoSurfaceHolder(surfaceHolder);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "LostFilm.TV"));
        MediaSource mediaSource = new ExtractorMediaSource(Uri.fromFile(videoFile), dataSourceFactory,
                new DefaultExtractorsFactory(), null, null);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(playWhenReady);
        mPlayer.addListener(this);
    }

    @Override
    public void play() {
        mProgressDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .takeWhile(aLong -> mPlayer != null)
                .subscribe(aLong -> {
                    if (mListener != null) {
                        mListener.onProgressUpdate(mPlayer.getCurrentPosition());
                    }
                });
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void resume() {
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void stop() {
        mPlayer.stop();
        mPlayer.release();
        if (mProgressDisposable != null) {
            mProgressDisposable.dispose();
        }
        mPlayer = null;
    }

    @Override
    public void seekForward(long ms) {
        mPlayer.seekTo(mPlayer.getCurrentPosition() + ms);
    }

    @Override
    public void seekBackward(long ms) {
        mPlayer.seekTo(mPlayer.getCurrentPosition() - ms);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        stop();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY) {
            if (mListener != null) {
                mListener.onPlayerReady(mPlayer.getDuration());
            }
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mListener != null) {
            mListener.onPlayerError(true);
        }
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    public interface Listener {

        void onProgressUpdate(long progress);

        void onPlayerReady(long duration);

        void onPlayerError(boolean unsupportedFormat);
    }
}
