package com.crazydude.lostfilmclient.presenters;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.crazydude.common.db.models.TvShow;

/**
 * Created by Crazy on 12.01.2017.
 */

public class DetailsPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        TvShow tvShow = (TvShow) item;
        vh.getTitle().setText(tvShow.getTitle());
        vh.getSubtitle().setText(tvShow.getTitleOriginal());
        vh.getBody().setText(tvShow.getDescription());
    }
}
