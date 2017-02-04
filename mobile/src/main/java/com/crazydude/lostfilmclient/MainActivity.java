package com.crazydude.lostfilmclient;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse("https://r7---sn-3c27ln7k.googlevideo.com/videoplayback?id=6fb497d0971b8cdf&itag=22&source=picasa&begin=0&requiressl=yes&mm=30&mn=sn-3c27ln7k&ms=nxu&mv=m&nh=IgphcjAzLmticDAxKgkxMjcuMC4wLjE&pl=22&sc=yes&mime=video/mp4&lmt=1486083166327499&mt=1486135406&ip=134.249.158.189&ipbits=8&expire=1486164239&sparams=ip,ipbits,expire,id,itag,source,requiressl,mm,mn,ms,mv,nh,pl,sc,mime,lmt&signature=3BB06D8D4294F8C49B3CE910B3D6849954302BB1.02ABE00700DFCEF715E72D0EFB73B67841E659F8&key=ck2&ratebypass=yes&title=%5BAnime365%5D%20Kuzu%20no%20Honkai%20-%2004%20(t1174045)"), dataSourceFactory,
                    new DefaultExtractorsFactory(), null, null);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);

        }
    }
}
