package com.dancecube.ratio;

/**
 * 已游玩谱面成绩
 */
public abstract class RecordedMusicInfo {
    int id;
    String name;

    abstract float getBestRatio();

    public int getBestRatioInt() {
        return Math.round(getBestRatio());
    }
}
