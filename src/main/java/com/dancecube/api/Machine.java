package com.dancecube.api;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.tools.HttpUtil;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Machine {
    public String PlaceName;
    public String Address;
    public boolean Online;


    public static List<Machine> getMachineList(String lng, String lat) {
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/OAuth/GetMachineListByLocation?lng=" + lng + "&lat=" + lat);
            Type type = new TypeToken<List<Machine>>() {
            }.getType();
            String string = null;
            if(response!=null && response.body()!=null) {
                string = response.body().string();
                response.close();
            }// Todo 改为JsonParser
            return new Gson().fromJson(string, type);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Machine> getMachineList(String region) {
        if(region==null || region.isBlank()) return null;
        String json = HttpUtil.getLocationInfo(region);
        if(json==null) return null;
        String result = JsonParser.parseString(json).getAsJsonObject().get("geocodes").getAsJsonArray().get(0).getAsJsonObject().get("location").getAsString();
        String[] location = result.split(",");
        return getMachineList(location[0], location[1]);
    }

    @Override
    public String toString() {
        return "Machine{" +
                "PlaceName='" + PlaceName + '\'' +
                ", Address='" + Address + '\'' +
                ", Online=" + Online +
                '}';
    }
}
