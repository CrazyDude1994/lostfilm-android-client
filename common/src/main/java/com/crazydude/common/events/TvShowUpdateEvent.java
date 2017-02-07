package com.crazydude.common.events;

/**
 * Created by Crazy on 09.01.2017.
 */

public class TvShowUpdateEvent {

    private int mId;

    public TvShowUpdateEvent(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
