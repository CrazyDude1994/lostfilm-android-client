package com.crazydude.lostfilmclient;

import android.app.ActivityOptions;
import android.content.Intent;
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
import com.crazydude.common.api.Episode;
import com.crazydude.common.api.Season;
import com.crazydude.common.api.TvShow;

/**
 * Created by Crazy on 10.01.2017.
 */

public class TvShowFragment extends BrowseFragment implements OnItemViewClickedListener {

    private int mTvShowId;
    private DatabaseManager mDatabaseManager;
    private ArrayObjectAdapter mCategoriesAdapter;

    public void setTvShowId(int tvShowId) {
        mTvShowId = tvShowId;
        loadData();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Episode episode = (Episode) item;

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_EPISODE_ID, episode.getId());
        intent.putExtra(PlayerActivity.EXTRA_SEASON_ID, episode.getSeason().getId());
        intent.putExtra(PlayerActivity.EXTRA_TV_SHOW_ID, episode.getSeason().getTvShow().getId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseManager = new DatabaseManager();
        setupUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
    }

    private void setupUI() {
        setHeadersState(HEADERS_DISABLED);
        setOnItemViewClickedListener(this);
    }

    private void loadData() {
        TvShow tvShow = mDatabaseManager.getTvShow(mTvShowId);
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (Season season : tvShow.getSeasons()) {
            ArrayObjectAdapter episodeAdapter = new ArrayObjectAdapter(new EpisodePresenter());
            episodeAdapter.addAll(0, season.getEpisodes());
            if (season.isHasFullSeasonDownloadUrl()) {
                episodeAdapter.add(new Episode("99", getString(R.string.full_season), season));
            }
            mCategoriesAdapter.add(new ListRow(new HeaderItem(getString(R.string.season, season.getId())), episodeAdapter));
        }
        setAdapter(mCategoriesAdapter);
    }
}
