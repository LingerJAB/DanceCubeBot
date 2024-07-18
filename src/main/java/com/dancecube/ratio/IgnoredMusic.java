package com.dancecube.ratio;

import com.google.gson.Gson;

import java.util.HashSet;

@Deprecated
public class IgnoredMusic {
    private final String name;
    private final int id;
    private final boolean hasRatio;
    private final HashSet<Integer> levelTypes;
    private final HashSet<Integer> levels;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean hasRatio() {
        return hasRatio;
    }

    public HashSet<Integer> getLevelTypes() {
        return levelTypes;
    }

    public HashSet<Integer> getLevels() {
        return levels;
    }

    public static IgnoredMusic fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, IgnoredMusic.class);
    }

    public IgnoredMusic(String name, int id, boolean hasRatio, HashSet<Integer> levelTypes, HashSet<Integer> levels) {
        this.name = name;
        this.id = id;
        this.hasRatio = hasRatio;
        this.levelTypes = levelTypes;
        this.levels = levels;
    }
}
