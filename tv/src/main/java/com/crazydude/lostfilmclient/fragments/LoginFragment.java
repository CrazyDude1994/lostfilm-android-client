package com.crazydude.lostfilmclient.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.widget.Toast;

import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.events.LoginEvent;
import com.crazydude.interactors.LoginInteractor;
import com.crazydude.lostfilmclient.R;
import com.crazydude.models.LoginResult;
import com.crazydude.models.User;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Crazy on 07.01.2017.
 */

public class LoginFragment extends GuidedStepFragment implements Observer<LoginResult> {

    private static final long LOGIN_ACTION = 0;
    private static final long EMAIL = 1;
    private static final long PASSWORD = 2;

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(LoginResult loginResult) {
        EventBus.getDefault().post(new LoginEvent(loginResult.getLogin()));
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), getString(R.string.wrong_login_or_password), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {

    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        String title = getString(R.string.login_fragment_title);
        String description = getString(R.string.login_fragment_description);
        return new GuidanceStylist.Guidance(title, description, "", null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction enterUsername = new GuidedAction.Builder(getActivity())
                .id(EMAIL)
                .description("crazyserega1994@mail.ru")
                .title(R.string.email)
                .descriptionEditable(true)
                .build();
        GuidedAction enterPassword = new GuidedAction.Builder(getActivity())
                .id(PASSWORD)
                .title(R.string.password)
                .description("80505071491")
                .descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)
                .build();
        GuidedAction login = new GuidedAction.Builder(getActivity())
                .id(LOGIN_ACTION)
                .title(R.string.login)
                .build();
        actions.add(enterUsername);
        actions.add(enterPassword);
        actions.add(login);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == LOGIN_ACTION) {
            String email = findActionById(EMAIL).getDescription().toString();
            String password = findActionById(PASSWORD).getDescription().toString();
            LoginInteractor interactor = new LoginInteractor(new User(email, password), AndroidSchedulers.mainThread(), new LostFilmApi());
            interactor.getObservable().subscribe(this);
        }
    }
}
