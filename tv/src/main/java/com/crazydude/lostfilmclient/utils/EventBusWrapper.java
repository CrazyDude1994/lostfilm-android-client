package com.crazydude.lostfilmclient.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by CrazyDude on 5/24/17.
 */

public class EventBusWrapper implements LifecycleObserver {

    private Object mObject;

    public EventBusWrapper(Object object) {
        mObject = object;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        EventBus.getDefault().register(mObject);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        EventBus.getDefault().unregister(mObject);
    }
}
