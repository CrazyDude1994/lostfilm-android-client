package com.crazydude.api;

import com.crazydude.models.LoginResult;
import com.crazydude.models.User;

import io.reactivex.Observable;

/**
 * Created by CrazyDude on 4/17/17.
 */

public interface Api {

    Observable<LoginResult> login(User user);
}
