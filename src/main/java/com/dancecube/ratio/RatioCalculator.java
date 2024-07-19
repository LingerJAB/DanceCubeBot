package com.dancecube.ratio;

import com.dancecube.info.UserInfo;
import com.dancecube.token.Token;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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


public class RatioCalculator {

    public static void main(String[] args) {

        //Input Your DanceCubeBase Bearer Token Here
        String auth = "bearer " + "tLB-nyd8xVnXMpn2TObtpUZAoB_of9WhB9Sye7jrLuVldr_8JfV73qQvQ1-i-hAs2DPm83U4_LVqh-j7M4jZeULkaLvros29EKcMlpuPd76pBFScElsWd8LS07K2NmFBWwjtkmSxs7lhmSeWk1W0wZb7qVyZQiw-oPwLa_6kq2UngZxY2pGrr3SOJw3nuc58DaCexkJ_Hz6bZRC-Mfzhj4e59n-nr-7JN2A5t2U9znVdmDlfN1mrVauoGxdW-R29QhqYp-78hTDisUhogStCi9K7VHRdt1AoC5I9fUSpU9ZrXzJiUzMJTumw0dQ8hSAPGycxUDaDqIXViWqs78-zSw6giMQauJgI-feTSdDJkp3M86xw4qVHCPTeeMNKtPM8";

        //The parameter officialOnly given true will ignore non-official music (including fan-made charts)
        List<RankMusicInfo> allRankList = getAllRankList(auth);
        List<RecentMusicInfo> allRecentList = getAllRecentList(auth);
        List<RankMusicInfo> rank15List = new ArrayList<>(getSubRank15List(allRankList, true));
        List<RecentMusicInfo> recent15List = new ArrayList<>(getSubRecent15List(allRecentList, false));

        //Best 15
        System.out.println("#The Best 15 PlayerMusic");
        for(int i = 0, rank15ListSize = rank15List.size(); i<rank15ListSize; i++) {
            RankMusicInfo musicInfo = rank15List.get(i);
            System.out.printf("#%d Name:%s, Ratio: %.2f\n", i + 1, musicInfo.getName(), musicInfo.getRatio());
        }

        //Recent 15
        System.out.println("\n\n#The Recent 15 PlayerMusic");
        for(int i = 0, recent15ListSize = recent15List.size(); i<recent15ListSize; i++) {
            RecentMusicInfo musicInfo = recent15List.get(i);
            System.out.printf("#%d Name:%s, Ratio: %.2f\n", i + 1, musicInfo.getName(), musicInfo.getRatio());
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
        float sum = 0;
        for(RecordedMusicInfo info : multiInfo) {
            sum = info.getRatio() + sum;
        }
        return sum / multiInfo.size();
    }

    private static List<RankMusicInfo> getCategoryRankList(String json) {
        List<JsonElement> list = JsonParser.parseString(json).getAsJsonArray().asList();
        List<RankMusicInfo> singleRankMusicList = new ArrayList<>();
        list.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            object.get("ItemRankList").getAsJsonArray().forEach(info ->
                    singleRankMusicList.add(new RankMusicInfo(
                            object.get("MusicID").getAsInt(),
                            object.get("Name").getAsString(),
                            object.get("OwnerType").getAsInt(),
                            info.getAsJsonObject()))
            );
        });
        return singleRankMusicList;
    }

    public static List<RankMusicInfo> getAllRankList(String auth) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetMyRankNew?musicIndex=";
        String json = "";
        ArrayList<RankMusicInfo> musicInfos = new ArrayList<>();

        for(int i = 2; i<=6; i++) { // 2 3 4 5 6 国语 粤语 韩语 欧美 其它
            Request request = new Request.Builder().url(url + i).get().addHeader("Authorization", auth).build();
            try {
                try(Response response = client.newCall(request).execute()) {
                    ResponseBody body = response.body();
                    if(body!=null) json = body.string();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            musicInfos.addAll(getCategoryRankList(json));
        }
        return musicInfos;
    }

    public static List<RecentMusicInfo> getAllRecentList(String auth) {
        String url = "https://dancedemo.shenghuayule.com/Dance/api/User/GetLastPlay";
        String json = "";

        ResponseBody body = null;
        try(Response response = HttpUtil.httpApi(url, Map.of("Authorization", auth))) {
            if(response!=null && response.body()!=null) body = response.body();
            if(body!=null) json = body.string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<RecentMusicInfo> musicInfoList = new ArrayList<>();
        for(JsonElement element : JsonParser.parseString(json).getAsJsonArray()) {
            RecentMusicInfo musicInfo = new RecentMusicInfo(element.getAsJsonObject());
            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    /**
     * 判断是否满足算入战力要求
     */
    public static boolean isRatioValid(RecordedMusicInfo musicInfo) {
        return musicInfo.level>0 && musicInfo.level<20 && musicInfo.isOfficial();
    }


    /**
     * 获取B15 (可能小于15)
     *
     * @param musicInfoList  源Best列表
     * @param ratioValidOnly 仅可计入战力模式
     * @return b15
     */
    public static List<RankMusicInfo> getSubRank15List(List<RankMusicInfo> musicInfoList, boolean ratioValidOnly) {
        musicInfoList.sort((o1, o2) -> Float.compare(o2.getRatio(), o1.getRatio()));
        List<RankMusicInfo> subRankList = new ArrayList<>();
        if(ratioValidOnly) {
            int count = 0; // 用于跟踪添加到subRankList中的项数
            for(int i = 0; i<musicInfoList.size() && count<15; i++) {
                RankMusicInfo musicInfo = musicInfoList.get(i);
                if(isRatioValid(musicInfo)) {
                    subRankList.add(musicInfo);
                    count++; // 增加计数器
                }
            }
        } else {
            for(int i = 0; i<Math.min(15, musicInfoList.size()); i++) {
                subRankList.add(musicInfoList.get(i));
            }
        }
        return subRankList;
    }


    /**
     * 获取R15 (可能小于15)
     *
     * @param musicInfoList 源Recent列表
     * @param officialOnly  仅官谱模式
     * @return b15
     */
    public static List<RecentMusicInfo> getSubRecent15List(List<RecentMusicInfo> musicInfoList, boolean officialOnly) {
        List<RecentMusicInfo> subRencentList = new ArrayList<>();
        if(officialOnly) {
            int count = 0;
            for(int i = 0; i<musicInfoList.size() && count<15; i++) {
                RecentMusicInfo musicInfo = musicInfoList.get(i);
                if(isRatioValid(musicInfo)) {
                    subRencentList.add(musicInfo);
                    count++;
                }
            }
        } else {
            for(int i = 0; i<Math.min(15, musicInfoList.size()); i++) {
                subRencentList.add(musicInfoList.get(i));
            }
        }
        return subRencentList;
    }

}


