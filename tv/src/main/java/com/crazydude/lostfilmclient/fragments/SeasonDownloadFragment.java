package com.crazydude.lostfilmclient.fragments;

import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;

/**
 * Created by Crazy on 09.05.2017.
 */

public class SeasonDownloadFragment extends SeasonListFragment {

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Episode episode = (Episode) item;
        databaseManager.setEpisodeDownloading(episode);
    }
}
