package com.dancecube.music;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

// 无视官方铺面/自制铺面获取封面
public class AnyMusic {
    private final String name;
    private final String coverUrl;
    private final int id;

    public AnyMusic(int id) {
        int page = 1;
        int index = 6;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/api/User/GetMusicRanking?page=%d&musicIndex=%d&musicId=%d".formatted(page, index, id))
                .get().build();
        String string;
        try {
            Response response = client.newCall(request).execute();
            string = response.body().string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        JsonObject object = JsonParser.parseString(string).getAsJsonObject().get("List").getAsJsonArray().get(0).getAsJsonObject();
        this.name = object.get("Name").getAsString();
        this.id = id;
        this.coverUrl = object.get("Cover").getAsString();
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public int getId() {
        return id;
    }
}
