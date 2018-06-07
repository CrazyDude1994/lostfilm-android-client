package com.crazydude.lostfilmclient.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
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
import android.util.Log;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.Season;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.activity.SeasonListActivity;
import com.crazydude.lostfilmclient.presenters.EpisodePresenter;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmChangeListener;

/**
 * Created by Crazy on 10.01.2017.
 */

public abstract class SeasonListFragment extends BrowseFragment implements OnItemViewClickedListener,
        RealmChangeListener<TvShow>, OnItemViewSelectedListener, LifecycleOwner {

    private int mTvShowId;
    protected DatabaseManager databaseManager;
    private ArrayObjectAdapter categoriesAdapter;
    private TvShow tvShow;
    private Lifecycle lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Episode) {
            EventBus.getDefault().post(new EpisodeSelectedEvent(((Episode) item)));
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
        databaseManager = new DatabaseManager();
        getLifecycle().addObserver(databaseManager);
        setupUI();
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private void setupUI() {
        loadData();
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void loadData() {
        tvShow = databaseManager.getTvShow(mTvShowId);
        tvShow.addChangeListener(this);
        Log.d("TvShow", String.format("Data loaded %d Seasons: %d", mTvShowId, tvShow.getSeasons().size()));
        updateData();
    }

    private void updateData() {
        categoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (Season season : tvShow.getSeasons()) {
            ArrayObjectAdapter episodeAdapter = new ArrayObjectAdapter(new EpisodePresenter());
            episodeAdapter.addAll(0, season.getEpisodes());
            if (season.isHasFullSeasonDownloadUrl()) {
                episodeAdapter.add(new Episode("99", getString(R.string.full_season), season, -1, null, null, 0, false));
            }
            categoriesAdapter.add(new ListRow(new HeaderItem(season.getName()), episodeAdapter));
        }
        setAdapter(categoriesAdapter);
    }

    public static class EpisodeSelectedEvent {

        private Episode mEpisode;

        public EpisodeSelectedEvent(Episode episode) {
            mEpisode = episode;
        }

        public Episode getEpisode() {
            return mEpisode;
        }
    }
}
