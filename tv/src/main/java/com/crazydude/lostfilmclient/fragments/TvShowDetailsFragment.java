package com.crazydude.lostfilmclient.fragments;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.lostfilmclient.presenters.DetailsPresenter;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmChangeListener;

/**
 * Created by Crazy on 07.05.2017.
 */

public class TvShowDetailsFragment extends DetailsFragment implements RealmChangeListener<TvShow> {

    private ArrayObjectAdapter mRowsAdapter;
    private DatabaseManager mDatabaseManager;
    private TvShow mTvShow;
    private DetailsOverviewRow mDetailsOverviewRow;

    @Override
    public void onChange(TvShow element) {
        mTvShow = mDatabaseManager.getTvShow(element.getId());
        mDetailsOverviewRow.setItem(mTvShow);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseManager = new DatabaseManager();

        Bundle arguments = getArguments();
        int tvshowId = arguments.getInt("tvshow_id");

        mTvShow = mDatabaseManager.getTvShow(tvshowId);
        mTvShow.addChangeListener(this);

        buildDetails();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        // Attach your media item details presenter to the row presenter:
        FullWidthDetailsOverviewRowPresenter rowPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsPresenter());

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        selector.addClassPresenter(ListRow.class,
                new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(selector);

        mDetailsOverviewRow = new DetailsOverviewRow(mTvShow);

        // Add images and action buttons to the details view
//        mDetailsOverviewRow.setImageDrawable();
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
}
