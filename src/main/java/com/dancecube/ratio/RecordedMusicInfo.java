package com.dancecube.ratio;

public abstract class RecordedMusicInfo {
    int id;
    String name;

    abstract float getBestRatio();

    public int getBestRatioInt() {
        return Math.round(getBestRatio());
    }
}
