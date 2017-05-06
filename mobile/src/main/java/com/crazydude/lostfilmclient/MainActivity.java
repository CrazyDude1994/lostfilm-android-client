package com.crazydude.lostfilmclient;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private TorrentStream mTorrentStream;
    private Disposable mProgressDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTorrent();
    }

    private void startTorrent() {
        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        mTorrentStream = TorrentStream.init(torrentOptions);
        mTorrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d("Torrent", "Prepared");
                torrent.getTorrentHandle().setSequentialDownload(true);
                torrent.startDownload();
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d("Torrent", "Started");
                mProgressDisposable = Observable.interval(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(aLong -> {
                            Torrent.State state = torrent.getState();
                        });
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d("Torrent", "Error: " + e.getMessage());
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d("Torrent", "Ready");
                torrent.getTorrentHandle().setSequentialDownload(true);
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
            }

            @Override
            public void onStreamStopped() {
                Log.d("Torrent", "Stopped");
            }
        });

        mTorrentStream.startStream("http://tracktor.in/td.php?s=MIEVwylovFE2e0c%2BWcU4CgbCHT0gJ3eXueMcmu8N1i073FQ2M0pr9UZs78TwESm0r9sBcW7OHyITZ9xXsbLF0pcYag0aiH3q%2FQH6mTtGkx%2FsdSYJAeEWhDOx4cstHqZyYACZEw%3D%3D");
    }
}
