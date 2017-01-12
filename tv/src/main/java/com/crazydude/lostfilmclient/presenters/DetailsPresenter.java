package com.crazydude.lostfilmclient.presenters;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

/**
 * Created by Crazy on 12.01.2017.
 */

public class DetailsPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        vh.getTitle().setText("Hello");
        vh.getSubtitle().setText("Hello");
        vh.getBody().setText("Hello");
    }
}
