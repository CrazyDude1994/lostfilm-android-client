package com.crazydude.models;

/**
 * Created by CrazyDude on 4/17/17.
 */

public class User {

    private final String mLogin;
    private final String mPassword;

    public User(String login, String password) {
        mLogin = login;
        mPassword = password;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }
}
