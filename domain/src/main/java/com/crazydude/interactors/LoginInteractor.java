package com.crazydude.interactors;

import com.crazydude.api.Api;
import com.crazydude.models.LoginResult;
import com.crazydude.models.User;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by CrazyDude on 4/17/17.
 */

public class LoginInteractor extends ObservableInteractor<User, LoginResult> {

    private Api mApi;

    public LoginInteractor(User data, Scheduler scheduler, Api api) {
        super(data, scheduler);
        mApi = api;
    }

    @Override
    protected Observable<LoginResult> buildObservable(User data) {
        return mApi.login(data);
    }
}
