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
    private float victoryRates;
    private float teamVictoryRates;
    private String playedAge;
    private int danLevel;
    private int playedTimes;
    private int passedSongs;
    private int addedCoins;


    public static ReplyItem get(Token token) {
        ReplyItem replyItem = new ReplyItem();
        String itemsJson;
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/ReplyTextItem/GetAllList?machineId=0", Map.of("Authorization", token.getBearerToken()));
        try(Response response = call.execute()) {
            itemsJson = response.body().string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        for(JsonElement element : JsonParser.parseString(itemsJson).getAsJsonArray()) {
            JsonObject jsonObject = element.getAsJsonObject();
            int type = jsonObject.get("ItemType").getAsInt();
            String content = jsonObject.get("Content").getAsString();
            switch(type) {
                case 13 -> replyItem.victoryRates = Float.parseFloat(content.replace('%', ' '));
                case 9 -> replyItem.teamVictoryRates = Float.parseFloat(content.replace('%', ' '));
                case 3 -> replyItem.playedAge = content;
                case 7 -> replyItem.danLevel = Integer.parseInt(content);
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
