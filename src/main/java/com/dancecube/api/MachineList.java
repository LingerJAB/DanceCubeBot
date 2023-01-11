package com.dancecube.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class MachineList {
    public String PlaceName;
    public String Address;
    public boolean Online;


    public static List<MachineList> get() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/OAuth/GetMachineListByLocation?lng=116.5&lat=31.7")
                .get()
//                .addHeader("Connection", "Keep-Alive")
//                .addHeader("Accept-Encoding", "gzip")
//                .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 8.1.0; V1818T Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/96.0.4664.104 Mobile Safari/537.36 uni-app Html5Plus/1.0 (Immersed/28.0)")
//                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            response.close();
            return new Gson().fromJson(string, new TypeToken<List<MachineList>>() {
            }.getType());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
