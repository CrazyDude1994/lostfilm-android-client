package com.crazydude.common.player;

/**
 * Created by CrazyDude on 5/14/17.
 */

public interface Player {

    void play();

    void resume();

    void pause();

    void stop();

    void seekForward(long ms);

    void seekBackward(long ms);
}
