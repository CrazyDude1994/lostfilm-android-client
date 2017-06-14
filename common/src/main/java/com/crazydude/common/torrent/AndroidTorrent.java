package com.crazydude.common.torrent;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Environment;
import android.util.Log;

import com.frostwire.jlibtorrent.TorrentStatus;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by CrazyDude on 5/14/17.
 */

public class AndroidTorrent implements Torrent, TorrentListener {

    private final Listener mListener;
    private TorrentStream mTorrentStream;
    private Subscription mUpdateSubscription;
    private com.github.se_bastiaan.torrentstream.Torrent mTorrent;

    public AndroidTorrent(Torrent.Listener listener) {
        mListener = listener;
        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
//                .removeFilesAfterStop(true)
                .build();

        mTorrentStream = TorrentStream.init(torrentOptions);
        mTorrentStream.addListener(this);
    }

    @Override
    public void startTorrent(String url) {
        mTorrentStream.startStream(url);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void stop() {
        mTorrentStream.stopStream();
        if (mUpdateSubscription != null) {
            mUpdateSubscription.cancel();
            mUpdateSubscription = null;
        }
    }

    @Override
    public void onStreamPrepared(com.github.se_bastiaan.torrentstream.Torrent torrent) {
        Log.d("Torrent", "Prepared");
        mTorrent = torrent;
        Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        mUpdateSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        TorrentStatus status = mTorrent.getTorrentHandle().status();
                        mListener.onTorrentProgress(status.progress(),
                                status.downloadRate(),
                                status.state().name(), status.numSeeds(), status.numPeers());
                        mUpdateSubscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        torrent.startDownload();
    }

    @Override
    public void onStreamStarted(com.github.se_bastiaan.torrentstream.Torrent torrent) {
        Log.d("Torrent", "Started");
    }

    @Override
    public void onStreamError(com.github.se_bastiaan.torrentstream.Torrent torrent, Exception e) {
        Log.d("Torrent", "Error: " + e.getMessage());
    }

    @Override
    public void onStreamReady(com.github.se_bastiaan.torrentstream.Torrent torrent) {
        Log.d("Torrent", "Ready");
        torrent.getTorrentHandle().setSequentialDownload(true);
        if (mListener != null) {
            mListener.onTorrentReadyToStream(torrent.getVideoFile());
        }
    }

    @Override
    public void onStreamProgress(com.github.se_bastiaan.torrentstream.Torrent torrent, StreamStatus streamStatus) {
//        mListener.onTorrentProgress(streamStatus.progress, (long) streamStatus.downloadSpeed);
    }

    @Override
    public void onStreamStopped() {
        Log.d("Torrent", "Stopped");
    }
}
