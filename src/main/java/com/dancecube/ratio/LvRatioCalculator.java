package com.dancecube.ratio;

import com.dancecube.info.UserInfo;
import com.dancecube.music.MusicUtil;
import com.dancecube.token.Token;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LvRatioCalculator {
    public static void main(String[] args) {

        //Input Your DanceCubeBase Bearer Token Here
        String auth = "bearer " + "rD_7AhyHSCrKNCXuPx31-U5VF5_ikh6rb9KoM7W_a_9zzBEwyShRYxnufk1utHMHPAOfi8kx6ZjQ-oEn3IeBLm-tzlzcILuSmnNJPfxD_U297pLVCjWChJjQg-ZwtxO8qRzdNLkrhzGXesTp8ZFh4TqFGf8e6kb-yOam-27cEzg39YKQEbm9qP4Pn7ZMUki85Wyx-9RrVfh8QoAV5_jhf69lrEBoaHmFjAlAh8CwaFHzJB_azqGojgJC5iPLpri-0ISGXSJZLkNO5fEL4-gKpjRtSlh-IiQKJBfzLAx4SN6yXAg5D7cZ7owg0CV1kKoVFHflK8i7r5Tdw2aGDXpNhxKutrxl8KHW4fCslVgSEoy8l9Al05wixV7Dj_ws21qlIpqZaufnDC2E-ZMnqLKCm4gggX6AM54IL3-MpszEtjgzR-caBsWg4EPTj926PLOUFEl-h8-JcfROuJtEb6pRFJOUCGmDaRjiqvEGHY_oMW4qFR0A1HYTbyvUqVOvb7vbjIwywq2xILDYhz8mzScoOfmUA3xN_syD7f8lzjHEaF_rDEEYRnO-6SneQ-hsj0TF8vJHYcI6Ah8xd3xyvC-Fd-Qqo7xrtS5Wwq9e2MiFw8jzpG1cJlzIJSxsYgCSMahc0bBnODWaZ0UXQameYVfpcF_klSbUgQWFGbNBb8UvEl5W3jMPkW55W8c1ChEP1DK6tCiJgHEHVIRs3km6jWqGlP0fB0kh1CoD9hRJRzxM1O2lq34G65byrhlrImnDPhhtF3xhM6D9umeMFB8Zg9TNb3ay_uWjDSfIc2SJXShMs9heYln5CjmoN7gvE9_qOhtfinqhQeXNVP0JEOx4U4MQG0iqfC1H1P8MdZPPEZGDA9-v1QAHey04eBfVFPCHZGX-";

        //The parameter officialOnly given true will ignore non-official music (including fan-made charts)
        ArrayList<RankMusicInfo> allRankList = getAllRankList(auth, true);
        ArrayList<RecentMusicInfo> allRecentList = getAllRecentList(auth, true);
        ArrayList<RankMusicInfo> rank15List = new ArrayList<>(getSubRank15List(allRankList));
        ArrayList<RecentMusicInfo> recent15List = new ArrayList<>(getSubRecent15List(allRecentList));

        //Best 15
        System.out.println("#The Best 15 PlayerMusic");
        for(int i = 0, rank15ListSize = rank15List.size(); i<rank15ListSize; i++) {
            RankMusicInfo musicInfo = rank15List.get(i);
            System.out.printf("#%d Name:%s, Ratio: %.2f\n", i + 1, musicInfo.getName(), musicInfo.getBestRatio());
        }

        //Recent 15
        System.out.println("\n\n#The Recent 15 PlayerMusic");
        for(int i = 0, recent15ListSize = recent15List.size(); i<recent15ListSize; i++) {
            RecentMusicInfo musicInfo = recent15List.get(i);
            System.out.printf("#%d Name:%s, Ratio: %.2f\n", i + 1, musicInfo.getName(), musicInfo.getBestRatio());
        }

        //Summing up
        System.out.println("\n# Sum #");
        float best15Avg = average(rank15List);
        float recent15Avg = average(recent15List);
        System.out.printf("Best 15 Ratio: %.2f\n", best15Avg);
        System.out.printf("Recent 15 Ratio: %.2f\n", recent15Avg);
        System.out.println("Your average ratio: " + (best15Avg + recent15Avg) / 2);

        System.out.println("Your Accurate Ratio:" + UserInfo.get(new Token(auth.substring(7)), 939088).getLvRatio());

    }

    /**
     * 取成绩平均值
     */
    public static <T extends RecordedMusicInfo> float average(List<T> multiInfo) {
        return average(new ArrayList<>(multiInfo));
    }

    public static <T extends RecordedMusicInfo> float average(ArrayList<T> multiInfo) {
        float sum = 0;
        for(RecordedMusicInfo info : multiInfo) {
            sum = info.getBestRatio() + sum;
        }
        return sum / multiInfo.size();
    }

    private static ArrayList<RankMusicInfo> getCategoryRankList(String json, boolean officialOnly) {
        List<JsonElement> list = JsonParser.parseString(json).getAsJsonArray().asList();
        ArrayList<RankMusicInfo> infos = new ArrayList<>();
        list.forEach(element -> {
            RankMusicInfo musicInfo = RankMusicInfo.get(element.getAsJsonObject());
            //仅官谱计入
            if(officialOnly) {
                if(musicInfo.isOfficial())
                    infos.add(musicInfo);
            } else {
//                infos.add(musicInfo);
            }

        });
        return infos;
    }

    public static ArrayList<RankMusicInfo> getAllRankList(String auth, boolean officialOnly) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetMyRankNew?musicIndex=";
        String json = "";
        ArrayList<RankMusicInfo> musicInfos = new ArrayList<>();

        for(int i = 1; i<=6; i++) {
            Request request = new Request.Builder().url(url + i).get().addHeader("Authorization", auth).build();
            try {
                try(Response response = client.newCall(request).execute()) {

                    ResponseBody body = response.body();
                    if(body!=null) json = body.string();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            musicInfos.addAll(getCategoryRankList(json, officialOnly));
        }
        return musicInfos;
    }

    public static List<RankMusicInfo> getSubRank15List(ArrayList<RankMusicInfo> list) {
        ((List<RankMusicInfo>) list).sort((o1, o2) -> Float.compare(o2.getBestRatio(), o1.getBestRatio()));
        if(list.size()<15) {
            return list;
        }
        return ((List<RankMusicInfo>) list).subList(0, 15);
    }

    public static ArrayList<RecentMusicInfo> getAllRecentList(String auth, boolean officialOnly) {

        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetLastPlay";
        String json = "";

        ResponseBody body = null;
        try(Response response = HttpUtil.httpApi(url, Map.of("Authorization", auth))) {
            if(response.body()!=null) body = response.body();
            if(body!=null) json = body.string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<RecentMusicInfo> musicInfoList = new ArrayList<>();
        for(JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
            RecentMusicInfo musicInfo = RecentMusicInfo.get(element.getAsJsonObject());

            if(officialOnly & !MusicUtil.isOfficial(musicInfo.id)) continue;
            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    public static List<RecentMusicInfo> getSubRecent15List(ArrayList<RecentMusicInfo> list) {
        if(list.size()<15) {
            return list;
        }
        return ((List<RecentMusicInfo>) list).subList(0, 15);
    }

}


