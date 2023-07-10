package com.dancecube.ratio.rankingMusic;

import com.dancecube.music.Officials;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LvRatioCalculator {
    public static void main(String[] args) throws IOException {

        //Input Your DanceCubeBase Bearer Token Here
        String auth = "bearer " + "iDQH1yM8yK_WhbZffU_GU641aYxUJXnFyiZFF8iSe-taI3Fk5EnyBeFMuAn00A8uuRWTy_DIzNaWi1hygQQLU_QiROBC6ztsAvxd8Itz-djHQOrth4EQOnLcwTSEBz2JsF5lSb4_14QwxJrzG9UzGcMhPXZ-A5yLR3da21VzXYbzKgcLi2_ykNWKTnJhqBa6V4iRIZUdFx_bittJWpw8XOzpB4n8lLcYZGcu4YEsGvYLTrqnHn41fpbN-NpPrQaxZfC7I4GYJV9sPdoZMrdnlVQUyHuEY4kaw5FF7QNofrtXhjCuZE2ejdNiGulNRJpIIdc43mzA1j7O-XghsXY4XuuAfSajn79GGwg-7N146clzPt7v4PHIoyDOixlo5JLVEReEk8MjEYowuKICoSeA3z5rvLe45zH8bYtN0ojxSIZ_VyGqRWBR1LMTYlq98Yf_M8RBBVOt98pj0aXL5SoJl8fm2bo0CK-i-RZXu0fKbG8oxf107keXHbEGZ274c16k-IhIfvy1J_5QoomvUglMY0sSY4dgwk1UhBHd8phpiayQIFECdH_WV9CwehWliK2BZwYwl2Xh6IRmmUqsY2XI3zLQnzq52SwBnWx-Nu78hPjAKnNZFlTlE0PfSYa6xcSzRz3aci-mY-m9pLp1t3oJDxTSLR5Pw9UdxcWZ-D9C-CfRim-OzLVBrtmn0dEF07XNYYPTZwxq8DZwfAi-Xa5at7mHfOWDt1RXVCvwhAf8uYKcq12zp4uaZDXmjJefSpx5-524g1IOwpcpswYGZIhdfclfI-_fIgY_t7ciFl9zmYOgvgf60u6LGkm1KeycHztfFFQAJrBU3REIOWDqYHNL5oUYk-CaM2mEKYzb1DoITNBENuqicDb3_whU0IryMEiK";

        //The parameter officialOnly given true will ignore non-official music (including fan-made chart)
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

        //Sum up
        System.out.println("\n# Sum #");
        float best15Avg = average(rank15List);
        float recent15Avg = average(recent15List);
        System.out.printf("Best 15 Ratio: %.2f\n", best15Avg);
        System.out.printf("Recent 15 Ratio: %.2f\n", recent15Avg);
        System.out.println("Your average ratio: " + (best15Avg + recent15Avg) / 2);


    }

    private static <T extends RecordedMusicInfo> float average(ArrayList<T> legacyInfos) {
        float sum = 0;
        for(RecordedMusicInfo info : legacyInfos) {
            sum = info.getBestRatio() + sum;
        }
        return sum / legacyInfos.size();
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
                infos.add(musicInfo);
            }

        });
        return infos;
    }

    public static ArrayList<RankMusicInfo> getAllRankList(String auth, boolean officialOnly) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetMyRankNew?musicIndex=";
        String json;
        ArrayList<RankMusicInfo> musicInfos = new ArrayList<>();

        for(int i = 1; i<=6; i++) {
            Request request = new Request.Builder().url(url + i).get().addHeader("Authorization", auth).build();
            try {
                Response response = client.newCall(request).execute();

                ResponseBody body = response.body();
                json = body.string();
                body.close();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            musicInfos.addAll(getCategoryRankList(json, officialOnly));
        }
        return musicInfos;
    }

    public static List<RankMusicInfo> getSubRank15List(ArrayList<RankMusicInfo> list) {
        ((List<RankMusicInfo>) list).sort((o1, o2) -> o1.getBestRatio()>o2.getBestRatio() ? -1 : (o1.getBestRatio()==o2.getBestRatio() ? 0 : 1));
        return ((List<RankMusicInfo>) list).subList(0, 15);
    }

    public static ArrayList<RecentMusicInfo> getAllRecentList(String auth, boolean officialOnly) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetLastPlay";
        String json;

        Request request = new Request.Builder().url(url).get().addHeader("Authorization", auth).build();
        try {
            Response response = client.newCall(request).execute();

            ResponseBody body = response.body();
            json = body.string();
            body.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<RecentMusicInfo> musicInfoList = new ArrayList<>();
        for(JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
            RecentMusicInfo musicInfo = RecentMusicInfo.get(element.getAsJsonObject());

            if(officialOnly & Officials.OFFICIAL_ID.contains(musicInfo.id))
                musicInfoList.add(musicInfo);
            else
                musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    public static List<RecentMusicInfo> getSubRecent15List(ArrayList<RecentMusicInfo> list) {
        //        musicInfos.sort((o1, o2) -> o1.getBestRatio()>o2.getBestRatio() ? -1 : (o1.getBestRatio()==o2.getBestRatio() ? 0 : 1));
        return ((List<RecentMusicInfo>) list).subList(0, 15);
    }

}


