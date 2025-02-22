package com.dab.medireminder.data.model;

public class BloodPressure {
    private int max;
    private int min;
    private long timer;

    public BloodPressure() {
    }

    public BloodPressure(int max, int min, long timer) {
        this.max = max;
        this.min = min;
        this.timer = timer;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
