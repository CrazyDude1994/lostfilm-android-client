package com.crazydude.lostfilmclient.fragments;

import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.Season;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.common.utils.Utils;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.activity.SeasonListActivity;
import com.crazydude.lostfilmclient.presenters.EpisodePresenter;
import com.crazydude.lostfilmclient.utils.DebouncedImageLoader;

import io.realm.RealmChangeListener;

/**
 * Created by Crazy on 10.01.2017.
 */

public abstract class SeasonListFragment extends BrowseFragment implements OnItemViewClickedListener, RealmChangeListener<TvShow>, OnItemViewSelectedListener {

    private int mTvShowId;
    private DatabaseManager mDatabaseManager;
    private ArrayObjectAdapter mCategoriesAdapter;
    private TvShow mTvShow;
    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private DebouncedImageLoader mImageLoader;

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Utils.PosterProvider) {
            mImageLoader.feed((Utils.PosterProvider) item);
        }
    }

    @Override
    public void onChange(TvShow element) {
        Log.d("TvShow", "Realm updated");
        updateData();
    }

    @Override
    public abstract void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mTvShowId = arguments.getInt(SeasonListActivity.EXTRA_TVSHOW_ID);
        mDatabaseManager = new DatabaseManager();
        setupUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
        mImageLoader.close();
    }

    private void setupUI() {
        prepareBackgroundManager();
        loadData();
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void loadData() {
        mTvShow = mDatabaseManager.getTvShow(mTvShowId);
        mTvShow.addChangeListener(this);
        Log.d("TvShow", String.format("Data loaded %d Seasons: %d", mTvShowId, mTvShow.getSeasons().size()));
        updateData();
    }

    private void updateData() {
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (Season season : mTvShow.getSeasons()) {
            ArrayObjectAdapter episodeAdapter = new ArrayObjectAdapter(new EpisodePresenter());
            episodeAdapter.addAll(0, season.getEpisodes());
            if (season.isHasFullSeasonDownloadUrl()) {
                episodeAdapter.add(new Episode("99", getString(R.string.full_season), season, -1, null));
            }
            mCategoriesAdapter.add(new ListRow(new HeaderItem(season.getName()), episodeAdapter));
        }
        setAdapter(mCategoriesAdapter);
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mImageLoader = new DebouncedImageLoader(getActivity(), mBackgroundManager, mMetrics.widthPixels, mMetrics.heightPixels);
    }
}
