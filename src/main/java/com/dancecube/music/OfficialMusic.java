package com.dancecube.music;

import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class OfficialMusic {
    @SerializedName("MusicID")
    private final int id;
    @SerializedName("Name")
    private final String name;
    @SerializedName("Cover")
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


    public static void saveOfficialImg(OkHttpClient client, OfficialMusic music) {
        String name = music.getName();
        int id = music.getId();
        String url = music.getCoverUrl();

        try {
            Response response = client.newCall(new Request.Builder().url(url).build()).execute();

            File file = new File("C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\src\\officialImg\\" + id + ".jpg");
            if(file.exists()) {
                System.out.println("#" + id + " " + name + " 已存在，未保存");
            } else {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(inputStream.readAllBytes());
                outputStream.close();
                System.out.println("#" + id + " " + name + " 已保存");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
