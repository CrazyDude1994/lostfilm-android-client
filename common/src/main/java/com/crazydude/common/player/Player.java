package com.crazydude.common.player;

import android.arch.lifecycle.LifecycleObserver;

/**
 * Created by CrazyDude on 5/14/17.
 */

public interface Player extends LifecycleObserver {

    void play();

    void resume();

    void pause();

    void stop();

    void seekForward(long ms);

    void seekBackward(long ms);
}
