package com.crazydude.common.torrent;

import android.os.Environment;
import android.util.Log;

import com.crazydude.common.player.AndroidPlayer;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

/**
 * Created by CrazyDude on 5/14/17.
 */

public class AndroidTorrent implements Torrent, TorrentListener {

    private final Listener mListener;
    private TorrentStream mTorrentStream;

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

    @Override
    public void onStreamPrepared(com.github.se_bastiaan.torrentstream.Torrent torrent) {
        Log.d("Torrent", "Prepared");
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
    public void stop() {
        mTorrentStream.stopStream();
    }

    @Override
    public void onStreamProgress(com.github.se_bastiaan.torrentstream.Torrent torrent, StreamStatus streamStatus) {
        mListener.onTorrentProgress(streamStatus.progress, (long) streamStatus.downloadSpeed);
    }

    @Override
    public void onStreamStopped() {
        Log.d("Torrent", "Stopped");
    }
}
