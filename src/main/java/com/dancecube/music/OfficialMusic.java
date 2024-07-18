package com.dancecube.music;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

class OfficialMusic {
    @SerializedName("MusicID")
    private final int id;
    @SerializedName("Name")
    private final String name;
    @SerializedName("CoverUrl")
    private final String coverUrl;
//    private final ArrayList<Integer> levels;

    public OfficialMusic(int id, String name, String coverUrl) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
//        this.levels = levels;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    @Override
    public boolean equals(Object o) {
        if(this==o) return true;
        if(o==null || getClass()!=o.getClass()) return false;

        OfficialMusic music = (OfficialMusic) o;

        if(id!=music.id) return false;
        return Objects.equals(name, music.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name!=null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OfficialMusic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
