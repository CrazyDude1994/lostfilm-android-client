package com.crazydude.lostfilmclient.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.crazydude.common.db.models.Episode;
import com.crazydude.lostfilmclient.activity.PlayerActivity;

/**
 * Created by Crazy on 09.05.2017.
 */

public class SeasonWatchFragment extends SeasonListFragment {

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Episode episode = (Episode) item;

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_EPISODE_ID, episode.getId());
        intent.putExtra(PlayerActivity.EXTRA_SEASON_ID, episode.getSeason().getId());
        intent.putExtra(PlayerActivity.EXTRA_TV_SHOW_ID, episode.getSeason().getTvShow().getId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
