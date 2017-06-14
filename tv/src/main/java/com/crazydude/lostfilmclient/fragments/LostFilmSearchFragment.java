package com.crazydude.lostfilmclient.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.text.TextUtils;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.lostfilmclient.presenters.TvShowPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by CrazyDude on 5/28/17.
 */

public class LostFilmSearchFragment extends SearchFragment implements
        SearchFragment.SearchResultProvider, OnItemViewClickedListener, LifecycleOwner {

    private ArrayObjectAdapter mRowsAdapter;
    private DatabaseManager mDatabaseManager;
    private Lifecycle mLifecycle = new LifecycleRegistry(this);
    private ArrayObjectAdapter mShowsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mShowsAdapter = new ArrayObjectAdapter(new TvShowPresenter());
        mRowsAdapter.add(new ListRow(mShowsAdapter));
        mDatabaseManager = new DatabaseManager();
        getLifecycle().addObserver(mDatabaseManager);
        setSearchResultProvider(this);
        setOnItemViewClickedListener(this);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        mShowsAdapter.clear();
        if (!TextUtils.isEmpty(newQuery)) {
            mShowsAdapter.addAll(0, mDatabaseManager.searchTvShows(newQuery));
            mShowsAdapter.notifyItemRangeChanged(0, mShowsAdapter.size());
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mRowsAdapter.clear();
        if (!TextUtils.isEmpty(query)) {
            mShowsAdapter.addAll(0, mDatabaseManager.searchTvShows(query));
            mShowsAdapter.notifyItemRangeChanged(0, mShowsAdapter.size());
        }
        return true;
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        EventBus.getDefault().post(new MainFragment.TvShowClickedEvent(((TvShow) item).getId()));
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}
