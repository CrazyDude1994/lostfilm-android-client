package com.crazydude.lostfilmclient;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream = TorrentStream.init(torrentOptions);
        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d("Torrent", "Prepared");
                torrent.getTorrentHandle().setSequentialDownload(true);
                torrent.startDownload();
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d("Torrent", "Started");
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d("Torrent", "Error: " + e.getMessage());
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d("Torrent", "Ready");
                torrent.getTorrentHandle().setSequentialDownload(true);
                if (!isPlaying) {
                    Handler mainHandler = new Handler();
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);

                    LoadControl loadControl = new DefaultLoadControl();

                    SimpleExoPlayer player =
                            ExoPlayerFactory.newSimpleInstance(MainActivity.this, trackSelector, loadControl);
                    SimpleExoPlayerView playerView = (SimpleExoPlayerView) findViewById(R.id.videoView);
                    playerView.setPlayer(player);
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(MainActivity.this,
                            Util.getUserAgent(MainActivity.this, "yourApplicationName"));
                    MediaSource mediaSource = new ExtractorMediaSource(Uri.fromFile(torrent.getVideoFile()), dataSourceFactory,
                            new DefaultExtractorsFactory(), null, null);
                    player.prepare(mediaSource);
                    player.setPlayWhenReady(true);
                }
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
                Log.d("Torrent", "Progress " + streamStatus.progress);
            }

            @Override
            public void onStreamStopped() {
                Log.d("Torrent", "Stopped");
            }
        });

        torrentStream.startStream("http://tracktor.in/td.php?s=3rbCXJf9kkJoMhp4C1dfivsePMFJIi3NIWho6qqExGOlJ8voBea8DONzWbJpnG6n8Tt1KN3mo2ZQSK%2FK1fOtBLBr88TR8SlmBHEsfYYcV%2F0q4WtGrKQHnruUXh2bUE94dgQWmw%3D%3D");
    }
}
