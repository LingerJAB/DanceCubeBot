package com.dancecube.ratio;


import com.google.gson.JsonObject;

/**
 * RankMusic的一个其中难度/谱面记录
 */
public class RankMusicInfo extends RecordedMusicInfo {
    private final boolean isOfficial;
    private final int ranking;

    public int getRanking() {
        return ranking;
    }


    /**
     * 通过ItemRankList的列表分别创建对象，需要在创建时解析好源JSON并传递id name ownerType!!!
     */
    public RankMusicInfo(int id, String name, int ownerType, JsonObject details) {
        super(
                id, name, details.get("MusicLevOld").getAsInt(),
                details.get("MusicRank").getAsInt(),
                details.get("MusicLev").getAsInt(),
                details.get("PlayerPercent").getAsFloat() / 100,
                details.get("PlayerScore").getAsInt(),
                details.get("ComboCount").getAsInt(),
                details.get("PlayerMiss").getAsInt()
        );
        ranking = details.get("MusicRanking").getAsInt();
        isOfficial = ownerType==1;
    }

    @Override
    public String toString() {
        return "RankMusicInfo{" +
               "difficulty=" + getDifficulty() +
               ", level=" + getLevel() +
               ", acc=" + getAccuracy() +
               ", ratio=" + getRatio() +
               '}';
    }

    @Override
    public boolean isOfficial() {
        return isOfficial;
    }
}

