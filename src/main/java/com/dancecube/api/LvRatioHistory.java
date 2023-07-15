package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class LvRatioHistory {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    public int getRatio() {
        return ratio;
    }

    private int ratio;

    public LvRatioHistory(Calendar calendar, int ratio) {
        this.calendar = calendar;
        this.ratio = ratio;
    }

    @Override
    public String toString() {

        return "LvRatioHistory{" +
                "calendar=" + format.format(calendar.getTime()) +
                ", ratio=" + ratio +
                '}';
    }

    public static ArrayList<LvRatioHistory> get(Token token) {
        ArrayList<LvRatioHistory> ratioList = new ArrayList<>();

        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/User/GetLvRatioHistory?userId=" + token.getUserId(),
                Map.of("Authorization", token.getBearerToken()))) {

            if(response!=null && response.body()!=null) {
                String json = response.body().string();

                for(JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
                    JsonObject object = element.getAsJsonObject();
                    try {
                        Calendar instance = Calendar.getInstance();
                        Date date = format.parse(object.get("LogTime").getAsString());
                        instance.setTime(date);
                        int ratio = object.get("LvRatio").getAsInt();
                        ratioList.add(new LvRatioHistory(instance, ratio));
                    } catch(ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return ratioList;
    }
}