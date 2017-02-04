package com.crazydude.common.events;

/**
 * Created by Crazy on 04.02.2017.
 */

public class LoginEvent {

    private String username;

    public LoginEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
