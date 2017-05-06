package com.crazydude.lostfilmclient.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v17.leanback.app.VideoFragment;
import android.support.v17.leanback.app.VideoFragmentGlueHost;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.presenters.DetailsPresenter;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Crazy on 11.01.2017.
 */

public class PlayerActivity extends Activity implements Observer<DownloadLink[]>, SurfaceHolder.Callback, OnActionClickedListener {

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";
    public static final String EXTRA_SEASON_ID = "extra_season_id";
    public static final String EXTRA_TV_SHOW_ID = "extra_tv_show_id";
    private static final int MY_PERMISSIONS_REQUEST_PLAY_MOVIE = 0;

    private int mTvShowId;
    private String mSeasonId;
    private String mEpisodeId;
    private DatabaseManager mDatabaseManager;
    private LostFilmApi mLostFilmApi;
    private Disposable mDisposable;
    private DownloadLink mSelectedLink;
    private TorrentStream mTorrentStream;
    private SurfaceHolder mSurfaceHolder;
    private SimpleExoPlayer mPlayer;
    private PlaybackControlsRow mControlsRow;
    private VideoFragmentGlueHost mGlue;
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private Disposable mProgressDisposable;

    @Override
    public void onActionClicked(Action action) {
        if (action == mPlayPauseAction) {
            mPlayPauseAction.nextIndex();
            if (mPlayer != null) {
                mPlayer.setPlayWhenReady(mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PAUSE);
            }
            mGlue.notifyPlaybackRowChanged();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //Will not be called on TV devices
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = null;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(DownloadLink[] downloadLinks) {
        String[] strings = new String[downloadLinks.length];
        for (int i = 0; i < downloadLinks.length; i++) {
            strings[i] = downloadLinks[i].getName();
        }
        new AlertDialog.Builder(this).setItems(strings, (dialogInterface, i) -> {
            mSelectedLink = downloadLinks[i];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                loadMovie();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_PLAY_MOVIE);
            }
        }).show();
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        mTvShowId = intent.getIntExtra(EXTRA_TV_SHOW_ID, -1);
        if (mTvShowId == -1) {
            finish();
        }
        mEpisodeId = intent.getStringExtra(EXTRA_EPISODE_ID);
        mSeasonId = intent.getStringExtra(EXTRA_SEASON_ID);

        mDatabaseManager = new DatabaseManager();
        mLostFilmApi = LostFilmApi.getInstance();

        loadData();
        setupUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mTorrentStream != null) {
            mTorrentStream.stopStream();
            mTorrentStream = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PLAY_MOVIE:
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    loadMovie();
                } else {
                    finish();
                }
                break;
        }
    }

    private void setupUI() {
        VideoFragment videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment);
        mGlue = new VideoFragmentGlueHost(videoFragment);
        mGlue.setSurfaceHolderCallback(this);
        mGlue.setOnActionClickedListener(this);

        mControlsRow = new PlaybackControlsRow(mSelectedLink);

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ControlButtonPresenterSelector());
        mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(this);
        mPlayPauseAction.nextIndex(); // set to play
        adapter.add(new PlaybackControlsRow.RewindAction(this));
        adapter.add(mPlayPauseAction);
        adapter.add(new PlaybackControlsRow.FastForwardAction(this));
        mControlsRow.setPrimaryActionsAdapter(adapter);

        PlaybackControlsRowPresenter presenter = new PlaybackControlsRowPresenter(new DetailsPresenter());

        mGlue.setPlaybackRow(mControlsRow);
        mGlue.setPlaybackRowPresenter(presenter);
    }

    private void loadMovie() {
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
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                LoadControl loadControl = new DefaultLoadControl();

                mPlayer = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, trackSelector, loadControl);
                mPlayer.setVideoSurfaceHolder(mSurfaceHolder);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this,
                        Util.getUserAgent(PlayerActivity.this, "yourApplicationName"));
                MediaSource mediaSource = new ExtractorMediaSource(Uri.fromFile(torrent.getVideoFile()), dataSourceFactory,
                        new DefaultExtractorsFactory(), null, null);
                mPlayer.prepare(mediaSource);
                mPlayer.setPlayWhenReady(mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PAUSE);
                mPlayer.addListener(new ExoPlayer.EventListener() {
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
                            mControlsRow.setTotalTimeLong(mPlayer.getDuration());
                            mGlue.notifyPlaybackRowChanged();
                        }
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }
                });
                mProgressDisposable = Observable.interval(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .takeWhile(aLong -> mPlayer != null)
                        .subscribe(aLong -> {
                            if (mPlayer != null) {
                                mControlsRow.setCurrentTimeLong(mPlayer.getCurrentPosition());
                                mGlue.notifyPlaybackRowChanged();
                            }
                        });
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
                if (mPlayer != null && mPlayer.getDuration() > 0) {
                    mControlsRow.setBufferedProgressLong((long) ((streamStatus.progress / 100) * mPlayer.getDuration()));
                    mGlue.notifyPlaybackRowChanged();
                }
                Log.d("Torrent", "Progress " + streamStatus.progress);
            }

            @Override
            public void onStreamStopped() {
                Log.d("Torrent", "Stopped");
            }
        });

        mTorrentStream.startStream("http://tracktor.in/td.php?s=MIEVwylovFE2e0c%2BWcU4CgbCHT0gJ3eXueMcmu8N1i073FQ2M0pr9UZs78TwESm0r9sBcW7OHyITZ9xXsbLF0pcYag0aiH3q%2FQH6mTtGkx%2FsdSYJAeEWhDOx4cstHqZyYACZEw%3D%3D");
    }

    private void loadData() {
        mLostFilmApi.getTvShowDownloadLink(mTvShowId, mSeasonId, mEpisodeId)
                .subscribe(this);
    }
}
