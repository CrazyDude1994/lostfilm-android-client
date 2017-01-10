package com.crazydude.lostfilmclient;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.crazydude.common.api.DatabaseManager;
import com.crazydude.common.api.JobHelper;
import com.crazydude.common.api.TvShow;
import com.crazydude.common.api.TvShowLoader;
import com.crazydude.common.api.TvShowUpdateEvent;
import com.crazydude.common.api.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Crazy on 08.01.2017.
 */

public class MainFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<List<TvShow>>, OnItemViewClickedListener {

    private static final int TV_SHOWS_LOADER = 0;
    private ArrayObjectAdapter mCategoriesAdapter;
    private JobHelper mJobHelper;
    private DatabaseManager mDatabaseManager;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdate(TvShowUpdateEvent event) {
        TvShow tvShow = mDatabaseManager.getTvShow(event.getId());
        ArrayObjectAdapter adapter = (ArrayObjectAdapter) getAdapter();
        for (int i = 0; i < adapter.size(); i++) {
            ArrayObjectAdapter tvShowAdapter = (ArrayObjectAdapter) ((ListRow) adapter.get(i)).getAdapter();
            for (int j = 0; j < tvShowAdapter.size(); j++) {
                if (((TvShow) tvShowAdapter.get(j)).getId() == event.getId()) {
                    tvShowAdapter.replace(j, tvShow);
                }
            }
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Intent intent = new Intent(getActivity(), TvShowActivity.class);
        intent.putExtra(TvShowActivity.EXTRA_TVSHOW_ID, ((TvShow) item).getId());
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (!Utils.hasSession()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public Loader<List<TvShow>> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case TV_SHOWS_LOADER:
                return new TvShowLoader(getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<TvShow>> loader, List<TvShow> tvShows) {
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        String lastChar = "";
        ArrayObjectAdapter adapter = null;
        for (TvShow show : tvShows) {
            if (!lastChar.equalsIgnoreCase(show.getName().substring(0, 1))) {
                if (adapter != null) {
                    mCategoriesAdapter.add(new ListRow(new HeaderItem(lastChar.toUpperCase()), adapter));
                }
                adapter = new ArrayObjectAdapter(new TvShowPresenter());
                lastChar = show.getName().substring(0, 1);
            }
            adapter.add(show);
        }
        mCategoriesAdapter.add(new ListRow(new HeaderItem(lastChar.toUpperCase()), adapter));
        setAdapter(mCategoriesAdapter);
        mJobHelper.scheduleBannerUpdate();
    }

    @Override
    public void onLoaderReset(Loader<List<TvShow>> loader) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mJobHelper = new JobHelper(getActivity());
        mDatabaseManager = new DatabaseManager();
        loadTvShows();
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mJobHelper.close();
        mDatabaseManager.close();
    }

    private void loadTvShows() {
        getLoaderManager().initLoader(0, null, this);
    }
}
