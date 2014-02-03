package dxat.controller.module;

import java.util.Date;

/**
 * Created by xavier on 1/20/14.
 */
public class Chronometer {
    private long startTime;
    private long stopTime;

    public void tic() {
        startTime = new Date().getTime();
    }

    public long toc() {
        stopTime = new Date().getTime();
        return getElapsed();
    }

    public long getElapsed() {
        return (stopTime - startTime);
    }
}
