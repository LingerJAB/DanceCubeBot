package com.dancecube.music;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.config.AbstractConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class Officials {
    // 保存下来的所有官谱谱面id TODO 写入文件
    public static final HashSet<Integer> OFFICIAL_ID;
    //    public static final HashSet<Integer> IGNORED_ID;

    static {
        try {
            OFFICIAL_ID = getIdsFromJson();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HashSet<Integer> getIds() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://dancedemo.shenghuayule.com/Dance/MusicData/GetValidMusicList?ownerType=1").get().build();
        Response response = client.newCall(request).execute();

        ResponseBody body = response.body();
        String list = body.string();
        body.close();

        HashSet<Integer> ids = new HashSet<>();
        JsonParser.parseString(list).getAsJsonArray().forEach(element -> {
            int musicID = element.getAsJsonObject().get("MusicID").getAsInt();
//                    System.out.println(musicID);
            ids.add(musicID);
        });
        return ids;
    }

    private static HashSet<Integer> getIdsFromJson() throws IOException {
        Type type = new TypeToken<HashSet<Integer>>() {
        }.getType();
        return new Gson().fromJson(Files.readString(Path.of(AbstractConfig.configPath + "OfficialMusicIds.json"))
                , type);
    }

//    @Test
//    public void test() throws IOException {
//        HashSet<Integer> validIds = getIds();
//        System.out.println(validIds + "" + validIds.size());
//        ArrayList<Integer> list = new ArrayList<>(validIds);
//        list.sort(Integer::compareTo);
//        System.out.println(list);
//
//    }
}
