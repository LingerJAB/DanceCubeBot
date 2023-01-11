package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class UserInfo {
    public int UserID; //用户ID
    public String HeadimgURL; //头像URL
    public String UserName; //用户名
    public int Sex; //性别（1男 2女）
    public String Phone; //手机号
    public String CityName; //
    public int MaxCursorSpeed;
    public int MusicSpeed;
    public String OtherCursorSpeed;
    public int MusicScore; //积分
    public int Point;
    public int RankNation;
    public int ComboPercent; //连击率（518为5.18%）
    public int LvRatio;
    public boolean IsVIP;
    public boolean IsInBlackList;
    public int TeamID;
    public String TeamName;
    public int StrictMode;
    public boolean JudgeTip;
    public boolean MirrorMode;
    public int MirrorModeVal;
    public boolean ResultPush;
    public int ResultPushPicLevel;
    public boolean ResultPushPic1;
    public boolean ResultPushPic2;
    public boolean ResultPushPic3;
    public int PrivacyLevel;
    public int TitleID;
    public String TitleUrl; //头衔
    public int Brightness;
    public String MuteExpireTime;
    public boolean IsMuted;
    public int TipsType;
    public String HeadimgBoxPath; //头像框
    public int WinTimes;
    public int Coins;
    public int GameLev;
    public String GameLevName;
    public int HideCursor;
    public int HpMode;
    public int DeleteStatus;

    public static UserInfo get(Token token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=%s".formatted(token.getUserId()))
                .get()
//                .addHeader("Connection", "Keep-Alive")
//                .addHeader("Accept-Encoding", "gzip")
//                .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 8.1.0; V1818T Build/OPM1.171019.026; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/96.0.4664.104 Mobile Safari/537.36 uni-app Html5Plus/1.0 (Immersed/28.0)")
//                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Authorization", "Bearer %s".formatted(token.getAccessToken()))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            response.close();
            return new Gson().fromJson(string, UserInfo.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}