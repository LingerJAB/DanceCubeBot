package com.dancecube.ratio;

import com.google.gson.JsonObject;
import com.tools.image.AccGrade;

/**
 * 最近游玩的记录音乐信息
 */
public class RecentMusicInfo extends RecordedMusicInfo {
    private int difficulty;
    private int level;
    private float acc;

    public int getScore() {
        return score;
    }

    public int getCombo() {
        return combo;
    }

    public int getMiss() {
        return miss;
    }

    private int score;
    private int combo;
    private int miss;

    public static RecentMusicInfo get(JsonObject object) {
        RecentMusicInfo musicInfo = new RecentMusicInfo();
        musicInfo.id = object.get("MusicID").getAsInt();
        musicInfo.name = object.get("MusicName").getAsString();
        musicInfo.level = object.get("MusicLevel").getAsInt();
        musicInfo.acc = object.get("PlayerPercent").getAsFloat() / 100;
        musicInfo.difficulty = object.get("MusicLevOld").getAsInt();
        musicInfo.score = object.get("PlayerScore").getAsInt();
        musicInfo.combo = object.get("ComboCount").getAsInt();
        musicInfo.miss = object.get("PlayerMiss").getAsInt();
        return musicInfo;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

    public int getLevel() {
        return level;
    }

    public float getAcc() {
        return acc;
    }

    public float getBestRatio() {
        return (level + 2) * getAcc();
    }

    public AccGrade getGrade() {
        return AccGrade.get(acc);
    }

    @Override
    public String toString() {


        return String.format("""
                Name: %s
                Level: %d
                Percent: %.2f
                #Ratio: %.2f
                """, name, level, getAcc(), getBestRatio());
    }

    public int getDifficulty() {
        return difficulty;
    }
}
