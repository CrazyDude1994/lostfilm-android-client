package com.crazydude.lostfilmclient.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v17.leanback.app.VideoFragment;
import android.support.v17.leanback.app.VideoFragmentGlueHost;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.Toast;

import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.DownloadLink;
import com.crazydude.common.player.AndroidPlayer;
import com.crazydude.common.torrent.AndroidTorrent;
import com.crazydude.common.torrent.Torrent;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.presenters.DetailsPresenter;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by Crazy on 11.01.2017.
 */

public class PlayerActivity extends Activity implements Observer<DownloadLink[]>,
        SurfaceHolder.Callback, OnActionClickedListener, Torrent.Listener, AndroidPlayer.Listener {

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";
    public static final String EXTRA_SEASON_ID = "extra_season_id";
    public static final String EXTRA_TV_SHOW_ID = "extra_tv_show_id";
    private static final int MY_PERMISSIONS_REQUEST_PLAY_MOVIE = 0;

    private int mTvShowId;
    private String mSeasonId;
    private String mEpisodeId;
    private DatabaseManager mDatabaseManager;
    private LostFilmApi mLostFilmApi;
    private DownloadLink mSelectedLink;
    private SurfaceHolder mSurfaceHolder;
    private PlaybackControlsRow mControlsRow;
    private VideoFragmentGlueHost mGlue;
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private TextView mTorrentProgress;
    private TextView mDownloadRate;
    private Torrent mTorrent;
    private AndroidPlayer mPlayer;
    private long mVideoDuration = 0;
    private Disposable mDownloadLinkDisposable;

    @Override
    public void onActionClicked(Action action) {
        if (action == mPlayPauseAction) {
            mPlayPauseAction.nextIndex();
            if (mPlayer != null) {
                if (isCurrentActionPlaying()) {
                    mPlayer.play();
                } else {
                    mPlayer.pause();
                }
            }
            mGlue.notifyPlaybackRowChanged();
        } else if (action == mFastForwardAction) {
            if (mPlayer != null) {
                mPlayer.seekForward(10000);
            }
        } else if (action == mRewindAction) {
            if (mPlayer != null) {
                mPlayer.seekBackward(10000);
            }
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
        mDownloadLinkDisposable = d;
    }

    @Override
    public void onNext(DownloadLink[] downloadLinks) {
        String[] strings = new String[downloadLinks.length];
        for (int i = 0; i < downloadLinks.length; i++) {
            strings[i] = downloadLinks[i].getName();
        }
        new AlertDialog.Builder(this)
                .setOnCancelListener(dialog -> finish())
                .setItems(strings, (dialogInterface, i) -> {
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
    public void onProgressUpdate(long progress) {
        mControlsRow.setCurrentTimeLong(progress);
        mGlue.notifyPlaybackRowChanged();
    }

    @Override
    public void onPlayerReady(long duration) {
        mVideoDuration = duration;
        mControlsRow.setTotalTimeLong(duration);
        mGlue.notifyPlaybackRowChanged();
    }

    @Override
    public void onPlayerError(boolean unsupportedFormat) {
        Toast.makeText(PlayerActivity.this, "Данный формат не поддерживается. Выберите другой", Toast.LENGTH_LONG).show();
        loadData();
        mPlayer.stop();
        mTorrent.stop();
    }

    @Override
    public void onTorrentReadyToStream(File videoFile) {
        mPlayer = new AndroidPlayer(this, mSurfaceHolder, videoFile, this, isCurrentActionPlaying());
    }

    @Override
    public void onTorrentProgress(float progress, long downloadSpeed) {
        if (mVideoDuration > 0) {
            mControlsRow.setBufferedProgressLong((long) ((progress / 100) * mVideoDuration));
            mTorrentProgress.setText(String.valueOf(progress));
            mDownloadRate.setText(String.format("%s MB/SEC", String.valueOf(downloadSpeed / 1024 / 1024)));
            mGlue.notifyPlaybackRowChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        mTorrentProgress = (TextView) findViewById(R.id.torrent_progress);
        mDownloadRate = (TextView) findViewById(R.id.download_speed);

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
        if (mDownloadLinkDisposable != null) {
            mDownloadLinkDisposable.dispose();
        }
        if (mPlayer != null) {
            mPlayer.stop();
        }
        if (mTorrent != null) {
            mTorrent.stop();
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

    private boolean isCurrentActionPlaying() {
        return mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PAUSE;
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
        mRewindAction = new PlaybackControlsRow.RewindAction(this);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(this);
        adapter.add(mRewindAction);
        adapter.add(mPlayPauseAction);
        adapter.add(mFastForwardAction);
        mControlsRow.setPrimaryActionsAdapter(adapter);

        PlaybackControlsRowPresenter presenter = new PlaybackControlsRowPresenter(new DetailsPresenter());

        mGlue.setPlaybackRow(mControlsRow);
        mGlue.setPlaybackRowPresenter(presenter);
    }

    private void loadMovie() {
        startTorrent();
    }

    private void startTorrent() {
        mTorrent = new AndroidTorrent(this);
        mTorrent.startTorrent(mSelectedLink.getUrl());
    }

    private void loadData() {
        mLostFilmApi.getTvShowDownloadLink(mTvShowId, mSeasonId, mEpisodeId)
                .subscribe(this);
    }
}
