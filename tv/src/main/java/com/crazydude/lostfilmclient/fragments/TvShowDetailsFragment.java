package com.crazydude.lostfilmclient.fragments;

import android.app.ActivityOptions;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.lostfilmclient.activity.SeasonListActivity;
import com.crazydude.lostfilmclient.presenters.DetailsPresenter;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmChangeListener;

/**
 * Created by Crazy on 07.05.2017.
 */

public class TvShowDetailsFragment extends DetailsFragment implements RealmChangeListener<TvShow>,
        OnActionClickedListener, LifecycleOwner {

    private ArrayObjectAdapter mRowsAdapter;
    private DatabaseManager mDatabaseManager;
    private TvShow mTvShow;
    private DetailsOverviewRow mDetailsOverviewRow;
    private Lifecycle mLifecycle = new LifecycleRegistry(this);

    @Override
    public void onActionClicked(Action action) {
        Intent intent = new Intent(getActivity(), SeasonListActivity.class);
        intent.putExtra(SeasonListActivity.EXTRA_TVSHOW_ID, mTvShow.getId());
        switch ((int) action.getId()) {
            case 1: // download
                intent.putExtra(SeasonListActivity.EXTRA_MODE, SeasonListActivity.MODE_DOWNLOAD);
                break;
            case 2: //watch
                intent.putExtra(SeasonListActivity.EXTRA_MODE, SeasonListActivity.MODE_WATCH);
                break;
        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onChange(TvShow element) {
        mTvShow = mDatabaseManager.getTvShow(element.getId());
        mDetailsOverviewRow.setItem(mTvShow);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseManager = new DatabaseManager();
        getLifecycle().addObserver(mDatabaseManager);

        Bundle arguments = getArguments();
        int tvshowId = arguments.getInt("tvshow_id");

        mTvShow = mDatabaseManager.getTvShow(tvshowId);
        mTvShow.addChangeListener(this);

        buildDetails();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new TvShowDetailsShowed(mTvShow));
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        // Attach your media item details presenter to the row presenter:
        FullWidthDetailsOverviewRowPresenter rowPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsPresenter());

        rowPresenter.setOnActionClickedListener(this);

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        selector.addClassPresenter(ListRow.class,
                new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(selector);

        mDetailsOverviewRow = new DetailsOverviewRow(mTvShow);

        Glide.with(this)
                .load(mTvShow.providePosterURL())
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(1280, 720) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        mDetailsOverviewRow.setImageBitmap(getActivity(), resource);
                    }
                });

        mDetailsOverviewRow.addAction(new Action(1, "Скачать для оффлайна"));
        mDetailsOverviewRow.addAction(new Action(2, "Смотреть онлайн"));
        mRowsAdapter.add(mDetailsOverviewRow);

        setAdapter(mRowsAdapter);
    }

    public class TvShowDetailsShowed {
        private TvShow mTvShow;

        public TvShowDetailsShowed(TvShow tvShow) {
            mTvShow = tvShow;
        }

        public TvShow getTvShow() {
            return mTvShow;
        }
    }
}
