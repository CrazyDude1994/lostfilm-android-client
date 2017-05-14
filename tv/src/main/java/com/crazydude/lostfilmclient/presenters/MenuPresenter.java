package com.crazydude.lostfilmclient.presenters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crazydude.common.db.models.Episode;
import com.crazydude.lostfilmclient.R;

/**
 * Created by CrazyDude on 5/10/17.
 */

public class MenuPresenter extends Presenter {

    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();
        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setMainImage(mContext.getDrawable(R.drawable.ic_settings));
        cardView.setMainImageScaleType(ImageView.ScaleType.CENTER_INSIDE
        );
        Resources res = cardView.getResources();
        int width = res.getDimensionPixelSize(R.dimen.card_width);
        int height = res.getDimensionPixelSize(R.dimen.card_height);
        cardView.setMainImageDimensions(width, height);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((ImageCardView) viewHolder.view).setTitleText(item.toString());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
