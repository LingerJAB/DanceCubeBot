package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mirai.tools.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class UserInfo {
    private int userID; //用户ID
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

    @Nullable
    public String getTitleUrl() {
        return titleUrl;
    }

    @Nullable
    public String getHeadimgBoxPath() {
        return headimgBoxPath;
    }

    public static UserInfo get(Token token) {
        String userInfoJson = "";
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=" + token.getUserId(), Map.of("Authorization", token.getBearerToken()));
        try(Response response = call.execute()) {

            if(response.body()!=null) {
                userInfoJson = response.body().string();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        return gson.fromJson(userInfoJson, UserInfo.class);
    }

    @Override
    public String toString() {
        return "UserInfo{userID=%d, musicScore=%d, lvRatio=%d, rankNation=%d, comboPercent=%d, sex=%d, userName='%s', headimgURL='%s', phone='%s', cityName='%s', teamName='%s', titleUrl='%s', headimgBoxPath='%s'}".formatted(userID, musicScore, lvRatio, rankNation, comboPercent, sex, userName, headimgURL, phone, cityName, teamName, titleUrl, headimgBoxPath);
    }

}