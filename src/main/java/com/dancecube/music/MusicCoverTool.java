package com.dancecube.music;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static com.dancecube.music.GoodsMusic.saveGoodsImg;

public class MusicCoverTool {

    public static void main(String[] args) throws IOException {
        String json = Files.readString(Path.of("C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\src\\all\\GoodsMusic.json"));

        OkHttpClient client = new OkHttpClient();
        for(JsonElement element : JsonParser.parseString(json).getAsJsonObject().get("List").getAsJsonArray()) {
            GoodsMusic music = new Gson().fromJson(element.getAsJsonObject(), GoodsMusic.class);
            saveGoodsImg(client, music);
        }

        System.out.println("\n# All right!");

    }


    //用于遍历获取
    private static HashSet<OfficialMusic> getListMusicSet(int index, int page, int size) {
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetMusicRankingNew?musicIndex=%d&pagesize=%d&page=%d".formatted(index, size, page);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        String musicJsons = "";
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            musicJsons = body.string();
            body.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        HashSet<OfficialMusic> musicHashSet = new HashSet<>();
        JsonObject object = JsonParser.parseString(musicJsons).getAsJsonObject();
        int num = 0;
        for(JsonElement element : object.get("List").getAsJsonArray()) {
            Gson gson = new Gson();
            OfficialMusic music = gson.fromJson(element.getAsJsonObject(), OfficialMusic.class);
            System.out.println(++num + ": " + music);
            musicHashSet.add(music);
        }
        return musicHashSet;
    }

}

