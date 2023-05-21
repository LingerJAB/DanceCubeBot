package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.*;
import com.mirai.tools.HttpUtil;
import com.mirai.tools.JsonUtil;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class UserInfo {
    private int userID; //用户ID
    private int gold; //金币
    private int musicScore; //积分
    private int lvRatio; //战力
    private int rankNation; //全国排名
    private int comboPercent; //连击率（518为5.18%）
    private int sex; //性别（1男 2女）
    private String userName; //用户名
    private String headimgURL; //头像URL
    private String phone; //手机号
    private String cityName; //城市名
    private String teamName; //战队名
    private String titleUrl; //头衔
    private String headimgBoxPath; //头像框

    public int getGold() {
        return gold;
    }

    public int getUserID() {
        return userID;
    }

    public int getLvRatio() {
        return lvRatio;
    }


    public String getHeadimgURL() {
        return headimgURL;
    }

    public String getUserName() {
        return userName;
    }

    public int getSex() {
        return sex;
    }

    public String getPhone() {
        return phone;
    }

    public String getCityName() {
        return cityName;
    }

    public int getMusicScore() {
        return musicScore;
    }

    public int getRankNation() {
        return rankNation;
    }

    public int getComboPercent() {
        return comboPercent;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public String getHeadimgBoxPath() {
        return headimgBoxPath;
    }

    public static UserInfo get(Token token) {
        String userInfoJson = "";
        String userAccountInfoJson = "";
        Response response2 = null;
        Response response1 = null;
        Call call1 = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=" + token.getUserId(), Map.of("Authorization", "Bearer " + token.getAccessToken()));
        Call call2 = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/User/GetAccountInfo?userId=" + token.getUserId(), Map.of("Authorization", "Bearer " + token.getAccessToken()));
        try {
            response1 = call1.execute();
            response2 = call2.execute();
            if(response1.body()!=null && response2.body()!=null) {
                userInfoJson = response1.body().string();
                userAccountInfoJson = response2.body().string();
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(response1!=null && response2!=null) {
                response1.close();
                response2.close();
            }
        }
        JsonObject jsonObject1 = JsonParser.parseString(userInfoJson).getAsJsonObject();
        JsonObject jsonObject2 = JsonParser.parseString(userAccountInfoJson).getAsJsonObject();
        JsonObject jsonObject = JsonUtil.mergeJson(jsonObject1, jsonObject2);

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        UserInfo userInfo = gson.fromJson(jsonObject, UserInfo.class);

        return userInfo;
    }

    @Override
    public String toString() {
        return "UserInfo{" + "userID=" + userID + ", headimgURL='" + headimgURL + '\'' + ", userName='" + userName + '\'' + ", sex=" + sex + ", phone='" + phone + '\'' + ", cityName='" + cityName + '\'' + ", musicScore=" + musicScore + ", rankNation=" + rankNation + ", comboPercent=" + comboPercent + ", teamName='" + teamName + '\'' + ", titleUrl='" + titleUrl + '\'' + ", headimgBoxPath='" + headimgBoxPath + '\'' + ", gold='" + gold + '\'' + '}';
    }
}
