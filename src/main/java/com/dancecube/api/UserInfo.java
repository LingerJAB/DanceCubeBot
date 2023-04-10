package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirai.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class UserInfo {
    private final int userID; //用户ID
    private final int gold; //金币
    private final int musicScore; //积分
    private final int lvRatio; //战力
    private final int rankNation; //全国排名
    private final int comboPercent; //连击率（518为5.18%）
    private final int sex; //性别（1男 2女）
    private final String userName; //用户名
    private final String headimgURL; //头像URL
    private final String phone; //手机号
    private final String cityName; //城市名
    private final String teamName; //战队名
    private final String titleUrl; //头衔
    private final String headimgBoxPath; //头像框

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

    //    public void setHeadimgURL(String headimgURL) {
//        this.headimgURL = headimgURL;
//    }
    public String getUserName() {
        return userName;
    }

    //    public void setUserName(String userName) {
//        this.userName = userName;
//    }
    public int getSex() {
        return sex;
    }

    //        public void setSex(int sex) {
//        this.sex = sex;
//    }
    public String getPhone() {
        return phone;
    }

    public String getCityName() {
        return cityName;
    }

    //        public void setCityName(String cityName) {
//        this.cityName = cityName;
//    }
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

    public UserInfo(Token token) {
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

        this.userID = jsonObject1.get("UserID").getAsInt();
        this.headimgURL = jsonObject1.get("HeadimgURL").getAsString();
        this.userName = jsonObject1.get("UserName").getAsString();
        this.sex = jsonObject1.get("Sex").getAsInt();
        this.phone = jsonObject1.get("Phone").getAsString();
        this.lvRatio = jsonObject1.get("LvRatio").getAsInt();
        this.cityName = jsonObject1.get("CityName").getAsString();
        this.musicScore = jsonObject1.get("MusicScore").getAsInt();
        this.rankNation = jsonObject1.get("RankNation").getAsInt();
        this.comboPercent = jsonObject1.get("ComboPercent").getAsInt();
        this.teamName = jsonObject1.get("TeamName").getAsString();
        JsonElement titleUrlJsonElement = jsonObject1.get("TitleUrl");
        this.titleUrl = titleUrlJsonElement.isJsonNull() ? "" : titleUrlJsonElement.getAsString();
        JsonElement headimgBoxPathJsonElement = jsonObject1.get("HeadimgBoxPath");
        this.headimgBoxPath = headimgBoxPathJsonElement.isJsonNull() ? "" : headimgBoxPathJsonElement.getAsString();
        this.gold = jsonObject2.get("Gold").getAsInt();
    }

    @Override
    public String toString() {
        return "UserInfo{" + "userID=" + userID + ", headimgURL='" + headimgURL + '\'' + ", userName='" + userName + '\'' + ", sex=" + sex + ", phone='" + phone + '\'' + ", cityName='" + cityName + '\'' + ", musicScore=" + musicScore + ", rankNation=" + rankNation + ", comboPercent=" + comboPercent + ", teamName='" + teamName + '\'' + ", titleUrl='" + titleUrl + '\'' + ", headimgBoxPath='" + headimgBoxPath + '\'' + ", gold='" + gold + '\'' + '}';
    }
}