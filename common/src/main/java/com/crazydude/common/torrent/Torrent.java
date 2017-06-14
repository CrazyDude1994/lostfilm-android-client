package com.crazydude.common.torrent;

import android.arch.lifecycle.LifecycleObserver;

import java.io.File;

/**
 * Created by CrazyDude on 5/14/17.
 */

public interface Torrent extends LifecycleObserver {

    void startTorrent(String url);

    void stop();

    interface Listener {

        void onTorrentReadyToStream(File videoFile);

        void onTorrentProgress(float progress, long downloadSpeed, String status, int seeds, int peers);
    }
}
