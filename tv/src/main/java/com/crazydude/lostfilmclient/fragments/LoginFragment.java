package com.crazydude.lostfilmclient.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;

import com.crazydude.lostfilmclient.R;

import java.util.List;

/**
 * Created by Crazy on 07.01.2017.
 */

public class LoginFragment extends GuidedStepFragment {

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance("Hi", "", "", getActivity().getDrawable(R.drawable.app_icon_quantum));
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(new GuidedAction.Builder(getActivity()).editTitle("").title(R.string.username).editable(true).build());
        actions.add(new GuidedAction.Builder(getActivity()).editTitle("").title(R.string.password).editable(true).editInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD).build());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
