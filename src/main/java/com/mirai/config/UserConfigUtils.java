package com.mirai.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

public class UserConfigUtils extends AbstractConfig {
    public static HashMap<Long, HashSet<String>> configsFromFile(String filePath) {
        Type type = new TypeToken<HashMap<Long, HashSet<String>>>() {
        }.getType();
        HashMap<Long, HashSet<String>> map = null;
        try {
            map = new Gson().fromJson(new FileReader(filePath), type);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void configsToFile(HashMap<Long, HashSet<String>> map, String filePath) {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(map);
        try {
            FileOutputStream stream = new FileOutputStream(filePath);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
