package com.dancecube.ratio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirai.config.AbstractConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@Deprecated
public class IgnoredMusicUtil {
    private static final HashMap<Integer, IgnoredMusic> IGNORED_MUSIC_MAP;

    static {
        String json;
        try {
            json = Files.readString(Path.of(AbstractConfig.configPath + "/IgnoredMusic.json"));
        } catch(IOException e) {
            json = "";
        }
        HashMap<Integer, IgnoredMusic> map = new HashMap<>();
        JsonParser.parseString(json).getAsJsonArray().forEach(
                element -> {
                    JsonObject object = element.getAsJsonObject();
                    int id = object.get("id").getAsInt();
                    map.put(id, new Gson().fromJson(element.getAsString(), IgnoredMusic.class));
                }
        );
        IGNORED_MUSIC_MAP = map;
    }

    public static boolean isIgnoredMusic(RecentMusicInfo info) {
        if(IGNORED_MUSIC_MAP.containsKey(info.getId())) { //检测id后检测levelType
            return IGNORED_MUSIC_MAP.get(info.getId()).getLevelTypes().contains(info.getLevelType());
        }
        return false;
    }

    public static boolean isIgnoredMusic(RankMusicInfo info) {
        if(IGNORED_MUSIC_MAP.containsKey(info.getId())) { //检测id后检测levelType
            return IGNORED_MUSIC_MAP.get(info.getId()).getLevelTypes().contains(info.getLevelType());
        }
        return false;
    }


}
