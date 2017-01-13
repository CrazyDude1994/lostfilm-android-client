package com.crazydude.lostfilmclient.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.OnboardingFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazydude.lostfilmclient.R;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Crazy on 12.01.2017.
 */

public class WelcomeFragment extends OnboardingFragment {

    public static final String COMPLETED_ONBOARDING_PREF_NAME = "completed_tutorial";
    private static final int PAGE_COUNT_NUM = 3;
    private static final int[] PAGE_TITLES = new int[]{
            R.string.welcome,
            R.string.lost_film_info,
            R.string.login_info
    };
    private static final int[] PAGE_DESCRIPTIONS = new int[]{
            R.string.welcome,
            R.string.lost_film_info,
            R.string.login_info
    };

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Leanback_Onboarding;
    }

    @Override
    protected int getPageCount() {
        return PAGE_COUNT_NUM;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        return getResources().getString(PAGE_TITLES[pageIndex]);
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        return getResources().getString(PAGE_DESCRIPTIONS[pageIndex]);
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING_PREF_NAME, true);
        sharedPreferencesEditor.apply();
        EventBus.getDefault().post(new TutorialCompletedEvent());
    }

    public class TutorialCompletedEvent {
    }
}
