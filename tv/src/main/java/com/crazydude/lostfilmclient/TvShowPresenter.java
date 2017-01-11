package com.crazydude.lostfilmclient;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.crazydude.common.api.TvShow;

/**
 * Created by Crazy on 08.01.2017.
 */

public class TvShowPresenter extends Presenter {

    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();
        ImageCardView cardView = new ImageCardView(mContext) {
            @Override
            public void setSelected(boolean selected) {
                int selected_background = mContext.getResources().getColor(R.color.detail_background);
                int default_background = mContext.getResources().getColor(R.color.default_background);
                int color = selected ? selected_background : default_background;
                findViewById(R.id.info_field).setBackgroundColor(color);
                super.setSelected(selected);
            }
        };
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TvShow tvShow = (TvShow) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        ((ImageCardView) viewHolder.view).setTitleText(tvShow.getName());

        // Set card size from dimension resources.
        Resources res = cardView.getResources();
        int width = res.getDimensionPixelSize(R.dimen.card_width);
        int height = res.getDimensionPixelSize(R.dimen.card_height);
        cardView.setMainImageDimensions(width, height);

        Glide.with(cardView.getContext())
                .load(tvShow.getImageUrl())
                .crossFade()
                .placeholder(R.color.color_primary)
                .error(R.color.color_primary)
                .into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
