package com.crazydude.lostfilmclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
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
import android.util.DisplayMetrics;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.common.events.TvShowsUpdateEvent;
import com.crazydude.common.jobs.JobHelper;
import com.crazydude.common.utils.Utils;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.activity.LoginActivity;
import com.crazydude.lostfilmclient.presenters.TvShowPresenter;
import com.crazydude.lostfilmclient.utils.DebouncedImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Crazy on 08.01.2017.
 */

public class MainFragment extends BrowseFragment implements OnItemViewClickedListener, OnItemViewSelectedListener {

    private ArrayObjectAdapter mCategoriesAdapter;
    private JobHelper mJobHelper;
    private DatabaseManager mDatabaseManager;
    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private Map<String, ArrayObjectAdapter> mAlphabetAdapterMap = new HashMap<>();
    private DebouncedImageLoader mImageLoader;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdate(TvShowsUpdateEvent event) {
        List<TvShow> tvShows = mDatabaseManager.getTvShows(event.getTvShows());

        updateTvShows(tvShows);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Utils.PosterProvider) {
            mImageLoader.feed(((Utils.PosterProvider) item));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobHelper = new JobHelper(getActivity());
        mDatabaseManager = new DatabaseManager();
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mCategoriesAdapter);
        prepareBackgroundManager();
        setupUI();
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
        mJobHelper.scheduleTvShowsUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mJobHelper.close();
        mDatabaseManager.close();
        mBackgroundManager.release();
        mImageLoader.close();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        TvShow selectedTvShow = (TvShow) item;
        mJobHelper.scheduleTvShowUpdate(selectedTvShow.getId(), selectedTvShow.getAlias());
        EventBus.getDefault().post(new OnTvShowSelectedEvent(selectedTvShow.getId()));
/*        Intent intent = new Intent(getActivity(), TvShowActivity.class);
        intent.putExtra(TvShowActivity.EXTRA_TVSHOW_ID, ((TvShow) item).getId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());*/
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.hasSession()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            loadTvShows();
        }
        EventBus.getDefault().register(this);
    }

    private void updateTvShows(List<TvShow> tvShows) {
        for (TvShow tvShow : tvShows) {
            String firstLetter = tvShow.getTitle().substring(0, 1).toUpperCase();
            if (!mAlphabetAdapterMap.containsKey(firstLetter)) {
                ArrayObjectAdapter tvShowsAdapter = new ArrayObjectAdapter(new TvShowPresenter());
                tvShowsAdapter.add(tvShow);
                mAlphabetAdapterMap.put(firstLetter, tvShowsAdapter);
                mCategoriesAdapter.add(new ListRow(new HeaderItem(firstLetter), tvShowsAdapter));
            } else {
                ArrayObjectAdapter tvShowsAdapter = mAlphabetAdapterMap.get(firstLetter);
                boolean found = false;
                for (int i = 0; i < tvShowsAdapter.size(); i++) {
                    if (((TvShow) tvShowsAdapter.get(i)).getId() == tvShow.getId()) {
                        found = true;
                        tvShowsAdapter.notifyItemRangeChanged(i, 1);
                        break;
                    }
                }

                if (!found) {
                    tvShowsAdapter.add(tvShow);
                }
            }
        }
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mImageLoader = new DebouncedImageLoader(getActivity(), mBackgroundManager, mMetrics.widthPixels, mMetrics.heightPixels);
    }

    private void setupUI() {
        setBadgeDrawable(getResources().getDrawable(R.drawable.logo, null));
        loadTvShows();
    }

    private void loadTvShows() {
        List<TvShow> tvShows = mDatabaseManager.getTvShows();
        updateTvShows(tvShows);
    }

    public static class OnTvShowSelectedEvent {
        private int mId;

        public OnTvShowSelectedEvent(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }
}
