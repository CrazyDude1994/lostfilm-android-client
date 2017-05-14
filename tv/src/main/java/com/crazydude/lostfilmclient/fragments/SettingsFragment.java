package com.crazydude.lostfilmclient.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.crazydude.lostfilmclient.R;

import java.util.Arrays;
import java.util.List;

import static android.support.v17.leanback.widget.GuidedAction.DEFAULT_CHECK_SET_ID;

/**
 * Created by CrazyDude on 5/10/17.
 */

public class SettingsFragment extends GuidedStepFragment {

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance("Настройки", "Настройте приложение под себя", null, getResources().getDrawable(R.drawable.ic_settings));
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        super.onCreateActions(actions, savedInstanceState);
        actions.add(new GuidedAction.Builder(getActivity())
                .title("Кэшировать серии")
                .description("Кэшировать серии во время просмотра онлайн")
                .subActions(Arrays.asList(new GuidedAction.Builder(getActivity())
                        .title("Кэшировать")
                        .checked(true)
                        .checkSetId(DEFAULT_CHECK_SET_ID)
                        .build(), new GuidedAction.Builder(getActivity())
                        .checkSetId(DEFAULT_CHECK_SET_ID)
                        .title("Не кэшировать")
                        .build())).build());
    }
}
