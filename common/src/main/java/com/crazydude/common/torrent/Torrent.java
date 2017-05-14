package com.crazydude.common.torrent;

import java.io.File;

/**
 * Created by CrazyDude on 5/14/17.
 */

public interface Torrent {

    void startTorrent(String url);

    void stop();

    interface Listener {

        void onTorrentReadyToStream(File videoFile);

        void onTorrentProgress(float progress, long downloadSpeed);
    }
}
