package com.dancecube.music;

// 无视官方铺面/自制铺面获取封面
public class Music {
    private final String name;
    private final int id;
    private final String coverUrl;

    public Music(String name, int id, String coverUrl) {
        this.name = name;
        this.id = id;
        this.coverUrl = coverUrl;
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
