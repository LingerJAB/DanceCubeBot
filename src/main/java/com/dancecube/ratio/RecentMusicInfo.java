package com.dancecube.ratio;

import com.dancecube.music.MusicUtil;
import com.google.gson.JsonObject;
import com.tools.image.AccGrade;

/**
 * 最近游玩的记录音乐信息
 */
public class RecentMusicInfo extends RecordedMusicInfo {
    private final int perfect;
    private final int great;
    private final int good;
    private final String recordTime;

    public AccGrade getGrade() {
        return AccGrade.get(accuracy);
    }

    @Override
    boolean isOfficial() {
        return MusicUtil.isOfficial(id);
    }

    public int getPerfect() {
        return perfect;
    }

    public int getGreat() {
        return great;
    }

    public int getGood() {
        return good;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public RecentMusicInfo(JsonObject object) {
        super(
                object.get("MusicID").getAsInt(),
                object.get("MusicName").getAsString(),
                object.get("MusicLevOld").getAsInt(),
                object.get("MusicLevel").getAsInt(),
                object.get("MusicLev").getAsInt(),
                object.get("PlayerPercent").getAsFloat() / 100,
                object.get("PlayerScore").getAsInt(),
                object.get("ComboCount").getAsInt(),
                object.get("PlayerMiss").getAsInt()
        );
        this.perfect = object.get("PlayerPerfect").getAsInt();
        this.great = object.get("PlayerGreat").getAsInt();
        this.good = object.get("PlayerGood").getAsInt();
        this.recordTime = object.get("RecordTime").getAsString();
    }


    @Override
    public String toString() {
        return String.format("""
                Name: %s
                Level: %d
                Percent: %.2f
                #Ratio: %.2f
                """, getName(), level, getAccuracy(), getRatio());
    }
}
