package com.crazydude.common.api;

/**
 * Created by Crazy on 04.02.2017.
 */

public class LoginResponse extends Response {

    private String name;
    private boolean success;

    public String getName() {
        return name;
    }

    public boolean isSuccess() {
        return success;
    }
}
