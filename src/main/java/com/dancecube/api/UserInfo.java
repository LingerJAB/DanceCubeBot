package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.Gson;
import com.mirai.HttpUtils;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

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
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=%s".formatted(token.getUserId()))
//                .get()
//                .addHeader("Authorization", "Bearer %s".formatted(token.getAccessToken()))
//                .build();

        try {
            Response response = HttpUtils.httpApi("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=" + token.getUserId(),
                    Map.of("Authorization", "Bearer " + token.getAccessToken()));
            if(response.code()!=200) {
                return null;
            }
            String string = response.body().string();
            response.close();
            return new Gson().fromJson(string, UserInfo.class);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}