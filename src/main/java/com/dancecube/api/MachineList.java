package com.dancecube.api;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.HttpUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class MachineList {
    public String PlaceName;
    public String Address;
    public boolean Online;


    public static List<MachineList> get(String  lng,String  lat) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/OAuth/GetMachineListByLocation?lng="+lng+"&lat="+lat)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            response.close(); // Todo 改为JsonParser
            return new Gson().fromJson(string, new TypeToken<List<MachineList>>() {
            }.getType());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MachineList> get(String region){
        if(region==null || region.isBlank()) return null;
        String json=HttpUtils.getLocationInfo(region);
        if(json==null) return null;
        String result= JsonParser.parseString(json).getAsJsonObject().get("geocodes").getAsJsonArray().get(0).getAsJsonObject().get("location").getAsString();
        String[] location = result.split(",");
        return get(location[0], location[1]);
    }
}
