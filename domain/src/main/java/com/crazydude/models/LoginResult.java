package com.crazydude.models;

/**
 * Created by CrazyDude on 4/18/17.
 */

public class LoginResult {

    private final String mLogin;
    private final String mPassword;
    private final boolean mIsSuccess;

    public LoginResult(String login, String password, boolean isSuccess) {
        mLogin = login;
        mPassword = password;
        mIsSuccess = isSuccess;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }
}
