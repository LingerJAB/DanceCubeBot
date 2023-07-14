package com.dancecube.ratio;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * 排名记录音乐信息
 */
public class RankMusicInfo extends RecordedMusicInfo {
    private boolean official;
    private final ArrayList<SingleRank> accList = new ArrayList<>();

    public static RankMusicInfo get(JsonObject object) {
        RankMusicInfo musicInfo = new RankMusicInfo();
        musicInfo.id = object.get("MusicID").getAsInt();
        musicInfo.name = object.get("Name").getAsString();
        musicInfo.official = object.get("OwnerType").getAsInt()==1;
        object.get("ItemRankList").getAsJsonArray().forEach(element -> {
            JsonObject json = element.getAsJsonObject();
            float acc = json.get("PlayerPercent").getAsFloat() / 100;
            int level = json.get("MusicRank").getAsInt();
            int difficulty = json.get("MusicLevOld").getAsInt();
            int rank = json.get("MusicRanking").getAsInt();
            int score = json.get("PlayerScore").getAsInt();
            int combo = json.get("ComboCount").getAsInt();
            int miss = json.get("PlayerMiss").getAsInt();
            musicInfo.accList.add(new SingleRank(difficulty, level, acc, score, rank, combo, miss));
        });
        return musicInfo;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

    public boolean isOfficial() {
        return official;
    }

    @Override
    public float getBestRatio() {
        return getBestInfo().ratio;
    }

    public SingleRank getBestInfo() {
        accList.sort((o1, o2) -> Float.compare(o2.ratio, o1.ratio));
//        return Math.round(accList.get(0).ratio);
        return accList.get(0);
    }

    @Override
    public String toString() {
        return "Name: %s\n#Ratio: %s\n#BestRatio: %.2f\n".formatted(name, accList, getBestRatio());
    }


}
