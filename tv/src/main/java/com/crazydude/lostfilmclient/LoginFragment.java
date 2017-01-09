package com.crazydude.lostfilmclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;

import com.crazydude.common.api.LostFilmApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Crazy on 07.01.2017.
 */

public class LoginFragment extends GuidedStepFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LostFilmApi lostFilmApi = new LostFilmApi();
    }

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
}
