package com.dancecube.music;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mirai.config.AbstractConfig;
import com.tools.HttpUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

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
        String json = "";
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/Music/GetMusicList",
                Map.of("getAdvanced", "true", "getNotDisplay", "false", "category", "0"))) {
            if(response!=null && response.body()!=null) {
                json = response.body().string();
            }
        } catch(IOException e) {
            e.printStackTrace();
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
        String name;
        String coverUrl = "";
        String json = "";
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/MusicData/GetInfo?musicId=" + id)) {
            if(response!=null && response.body()!=null) {
                json = response.body().string();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject MusicJsonObject = JsonParser.parseString(json).getAsJsonObject();
        name = MusicJsonObject.get("Name").getAsString();
        for(JsonElement element : MusicJsonObject.get("MusicFileList").getAsJsonArray()) {
            JsonObject obj = element.getAsJsonObject();
            if("背景图片".equals(obj.get("FileTypeText").getAsString())) {
                coverUrl = obj.get("Url").getAsString();
            }
        }

        return new Music(name, id, coverUrl);
    }

    // 刷新OFFICIAL_IDS与下载图片
    public static void main(String[] args) throws Exception {
        Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/MusicData/GetInfo?musicId=" + 6179);
        System.out.println(response.body().string());
        System.out.println("\n一共" + OFFICIAL_IDS.size() + "项");

        for(Integer id : OFFICIAL_IDS) {
            if(CoverUtil.isCoverAbsent(id)) {
                System.out.println("id" + id);
                CoverUtil.downloadCover(id);
                System.out.println("id=" + id + " is done!");
            }
        }
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
}