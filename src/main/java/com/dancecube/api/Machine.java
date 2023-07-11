package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.tools.HttpUtil;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Machine {
    private String placeName;
    private String address;
    private boolean show;
    private boolean online;


    public static List<Machine> getMachineList(String lng, String lat) {
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/OAuth/GetMachineListByLocation?lng=" + lng + "&lat=" + lat);
            Type type = new TypeToken<List<Machine>>() {
            }.getType();
            String json = null;
            ArrayList<Machine> machineList = new ArrayList<>();
            if(response!=null && response.body()!=null) {
                json = response.body().string();
                response.close();
            }
            JsonParser.parseString(json).getAsJsonArray().forEach(element -> {
                Machine machine = new Machine();
                JsonObject object = element.getAsJsonObject();
                machine.placeName = object.get("PlaceName").getAsString();
                machine.address = object.get("Address").getAsString();
                machine.online = object.get("Online").getAsBoolean();
                machine.show = object.get("MachineType").getAsInt()==1;
                machineList.add(machine);
            });
            return machineList;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Machine> getMachineList(String region) {
        if(region==null || region.isBlank()) return null;
        String json = HttpUtil.getLocationInfo(region);
        if(json==null) return null;
        String result;
        try {
            result = JsonParser.parseString(json).getAsJsonObject().get("geocodes")
                    .getAsJsonArray().get(0).getAsJsonObject()
                    .get("location").getAsString();
        } catch(NullPointerException e) {
            return List.of();
        }
        String[] location = result.split(",");
        return getMachineList(location[0], location[1]);
    }

    public boolean isOnline() {
        return online;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public boolean isShow() {
        return show;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "PlaceName='" + placeName + '\'' +
                ", Address='" + address + '\'' +
                ", Online=" + online +
                '}';
    }

    public static Response qrLogin(Token token, String qrUrl) {
        String url = "https://dancedemo.shenghuayule.com/Dance/api/Machine/AppLogin?qrCode="
                + URLEncoder.encode(qrUrl, StandardCharsets.UTF_8);
        return HttpUtil.httpApi(url
                , Map.of("Authorization", token.getBearerToken()));
    }
//
//    @Test
//    public void test() {
//        System.out.println(getMachineList("六安"));
//    }
}

