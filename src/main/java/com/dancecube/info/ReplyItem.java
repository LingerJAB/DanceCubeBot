package com.dancecube.info;

import com.dancecube.token.Token;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ReplyItem {
    private float victoryRates; //对战胜率
    private float teamVictoryRates; //战队赛胜率
    private String playedAge; //舞龄
    private int danLevel; //段位
    private int playedTimes; //游玩次数
    private int passedSongs; //游玩次数
    private int addedCoins; //增加金币

    public static ReplyItem get(Token token) {
        ReplyItem replyItem = new ReplyItem();
        String itemsJson;
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/ReplyTextItem/GetAllList?machineId=0", Map.of("Authorization", token.getBearerToken()));
        try {
            try(Response response = call.execute()) {
                itemsJson = response.body().string();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        for(JsonElement element : JsonParser.parseString(itemsJson).getAsJsonArray()) {
            JsonObject jsonObject = element.getAsJsonObject();
            int type = jsonObject.get("ItemType").getAsInt();
            String content = jsonObject.get("Content").getAsString();
            switch(type) {
                case 13 ->
                        replyItem.victoryRates = content.contains("无") ? 0 : Float.parseFloat(content.replace('%', ' '));
                case 9 ->
                        replyItem.teamVictoryRates = content.contains("无") ? 0 : Float.parseFloat(content.replace('%', ' '));
                case 3 -> replyItem.playedAge = content;
                case 7 -> replyItem.danLevel = content.contains("无") ? 0 : Integer.parseInt(content);
                case 5 -> replyItem.playedTimes = Integer.parseInt(content);
                case 6 -> replyItem.passedSongs = Integer.parseInt(content);
                case 10 -> replyItem.addedCoins = Integer.parseInt(content);
            }
        }
        return replyItem;
    }


    public float getVictoryRates() {
        return victoryRates;
    }

    public float getTeamVictoryRates() {
        return teamVictoryRates;
    }

    public String getPlayedAge() {
        return playedAge;
    }

    public int getDanLevel() {
        return danLevel;
    }

    public int getPlayedTimes() {
        return playedTimes;
    }

    public int getPassedSongs() {
        return passedSongs;
    }

    public int getAddedCoins() {
        return addedCoins;
    }

    @Override
    public String toString() {
        return "ReplyItem{" +
                "victoryRates=" + victoryRates +
                ", teamVictoryRates=" + teamVictoryRates +
                ", playedAge='" + playedAge + '\'' +
                ", danLevel=" + danLevel +
                ", playedTimes=" + playedTimes +
                ", passedSongs=" + passedSongs +
                ", addedCoins=" + addedCoins +
                '}';
    }

}
