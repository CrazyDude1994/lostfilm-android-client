package com.crazydude.lostfilmclient.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.view.View;

import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.Season;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.common.events.TvShowsUpdateEvent;
import com.crazydude.common.jobs.JobHelper;
import com.crazydude.common.utils.Utils;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.activity.LoginActivity;
import com.crazydude.lostfilmclient.activity.SearchActivity;
import com.crazydude.lostfilmclient.presenters.EpisodePresenter;
import com.crazydude.lostfilmclient.presenters.MenuPresenter;
import com.crazydude.lostfilmclient.presenters.TvShowPresenter;
import com.crazydude.lostfilmclient.utils.EventBusWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Crazy on 08.01.2017.
 */

public class MainFragment extends BrowseFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener, LifecycleOwner, View.OnClickListener {

    private static final int DOWNLOADS_ID = 0;
    private static final int OTHERS_ID = 1;
    private static final int SETTINGS_ID = 0;
    private ArrayObjectAdapter mCategoriesAdapter;
    private JobHelper mJobHelper;
    private DatabaseManager mDatabaseManager;
    private Map<String, ArrayObjectAdapter> mAlphabetAdapterMap = new HashMap<>();
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private ArrayObjectAdapter downloadsAdapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdate(TvShowsUpdateEvent event) {
        List<TvShow> tvShows = mDatabaseManager.getTvShows(event.getTvShows());

        updateTvShows(tvShows);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof TvShow) {
            EventBus.getDefault().post(new TvShowSelectedEvent((TvShow) item));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnSearchClickedListener(this);
        setSearchAffordanceColor(getBrandColor());

        mJobHelper = new JobHelper(getActivity().getApplicationContext());
        mDatabaseManager = new DatabaseManager();
        getLifecycle().addObserver(mJobHelper);
        getLifecycle().addObserver(mDatabaseManager);
        getLifecycle().addObserver(new EventBusWrapper(this));
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        downloadsAdapter = new ArrayObjectAdapter(new EpisodePresenter());
        mCategoriesAdapter.add(new ListRow(OTHERS_ID, new HeaderItem("Загрузки"), downloadsAdapter));
        mCategoriesAdapter.add(new DividerRow());
        mCategoriesAdapter.add(new DividerRow());
        ArrayObjectAdapter othersAdapter = new ArrayObjectAdapter(new MenuPresenter());
        othersAdapter.add("Настройки");
        mCategoriesAdapter.add(new ListRow(OTHERS_ID, new HeaderItem("Прочее"), othersAdapter));
        setAdapter(mCategoriesAdapter);
        setupUI();
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
        mJobHelper.scheduleTvShowsUpdate();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof TvShow) {
            TvShow selectedTvShow = (TvShow) item;
            mJobHelper.scheduleTvShowUpdate(selectedTvShow.getId(), selectedTvShow.getAlias());
            EventBus.getDefault().post(new TvShowClickedEvent(selectedTvShow.getId()));
        } else if (row.getId() == SETTINGS_ID) {
            onSettingsClicked();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.hasSession()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            loadTvShows();
        }
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void onSettingsClicked() {
        GuidedStepFragment.add(getFragmentManager(), new SettingsFragment(), R.id.placeholder);
    }

    private void updateTvShows(List<TvShow> tvShows) {
        for (TvShow tvShow : tvShows) {
            String firstLetter = tvShow.getTitle().substring(0, 1).toUpperCase();
            if (!mAlphabetAdapterMap.containsKey(firstLetter)) {
                ArrayObjectAdapter tvShowsAdapter = new ArrayObjectAdapter(new TvShowPresenter());
                tvShowsAdapter.add(tvShow);
                mAlphabetAdapterMap.put(firstLetter, tvShowsAdapter);
                mCategoriesAdapter.add(mCategoriesAdapter.size() - 2, new ListRow(new HeaderItem(firstLetter), tvShowsAdapter));
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

            for (Season season : tvShow.getSeasons()) {
                for (Episode episode : season.getEpisodes()) {
                    if (episode.isDownloading()) {
                        downloadsAdapter.add(episode);
                    }
                }
            }
        }
    }

    private void setupUI() {
        setBadgeDrawable(getResources().getDrawable(R.drawable.logo, null));
        loadTvShows();
    }

    private void loadTvShows() {
        List<TvShow> tvShows = mDatabaseManager.getTvShows();
        updateTvShows(tvShows);
    }

    public static class TvShowClickedEvent {
        private int mId;

        public TvShowClickedEvent(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public class TvShowSelectedEvent {
        private TvShow mTvShow;

        public TvShowSelectedEvent(TvShow tvShow) {
            mTvShow = tvShow;
        }

        public TvShow getTvShow() {
            return mTvShow;
        }
    }
}
