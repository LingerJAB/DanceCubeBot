package com.dancecube.music;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mirai.config.AbstractConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class MusicUtil {
    public static HashSet<Integer> OFFICIAL_IDS;

    private static final String path = AbstractConfig.configPath + "OfficialMusicIds.json";

    static {
        // 启动默认读取本地OFFICIAL_ID
        updateIdsFromFile(path);
    }

    public static boolean isOfficial(int id) {
        return OFFICIAL_IDS.contains(id);
    }

    public static boolean updateIdsFromFile(String path) {
        try {
            String idsJson = Files.readString(Path.of(path));
            OFFICIAL_IDS = new Gson().fromJson(idsJson, new TypeToken<>() {
            });
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateIdsFromAPI(String path) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/Music/GetMusicList?getAdvanced=false&getNotDisplay=true&getAudio=true&category=0")
                .get().build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch(IOException e) {
            return false;
        }
        String json;
        try {
            json = response.body().string();
            response.close();
        } catch(IOException e) {
            return false;
        }
        if(OFFICIAL_IDS==null) OFFICIAL_IDS = new HashSet<>();
        JsonParser.parseString(json).getAsJsonArray().forEach(obj -> {
            OFFICIAL_IDS.add(obj.getAsJsonObject().get("MusicID").getAsInt());
            try {
                FileOutputStream outputStream = new FileOutputStream(path);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                outputStream.write(gson.toJson(OFFICIAL_IDS).getBytes());
                outputStream.close();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        });
        return true;
    }


    public static Music getMusic(int id) {
        // 获取歌曲对象（主要是AudioURL）
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
        String name = object.get("Name").getAsString();
        String coverUrl = object.get("Cover").getAsString();
        return new Music(name, id, coverUrl);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(updateIdsFromAPI(path));
        System.out.println(OFFICIAL_IDS);

        System.out.println("\n# All right!");

    }


    //用于遍历获取
    @Deprecated
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

    public static void saveCover(Music music) {

    }
}

