package com.dancecube.info;

import com.dancecube.token.Token;
import com.google.gson.*;
import com.tools.HttpUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class UserInfo {
    private int userID; //用户ID
    private int musicScore; //积分
    private int lvRatio; //战力
    private int rankNation; //全国排名
    private int comboPercent; //全连率（518为5.18%）
    private int sex; //性别（1男 2女）
    private String userName; //用户名
    private String headimgURL; //头像URL
    private String phone; //手机号
    private String cityName; //城市名
    private String teamName; //战队名
    private String titleUrl; //头衔
    private String headimgBoxPath; //头像框
    private InfoStatus status = InfoStatus.OPEN;


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
        return teamName==null ? "" : teamName;
    }

    public String getTitleUrl() {
        return titleUrl==null || titleUrl.length()<5 ? "" : titleUrl;
    }

    public String getHeadimgBoxPath() {
        return headimgBoxPath==null ? "" : headimgBoxPath;
    }

    public InfoStatus getStatus() {
        return status;
    }

    public void setStatus(InfoStatus status) {
        this.status = status;
    }


    public UserInfo() {
    }

    /**
     * 不推荐的构造函数，仅取代 null
     */
    public static UserInfo getNull() {
        UserInfo userInfo = new UserInfo();
        userInfo.userID = -1;
        userInfo.status = InfoStatus.NONEXISTENT;
        return userInfo;
    }

    public static UserInfo get(Token token) {
        String userInfoJson = "";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=" + token.getUserId())
                .addHeader("Authorization", token.getBearerToken())
                .get().build();
        Call call = client.newCall(request);
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

    public static UserInfo get(Token token, int id) {
        String userInfoJson = "";
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/User/GetInfo?userId=" + id,
                Map.of("Authorization", token.getBearerToken()))) {
            if(response!=null && response.body()!=null)
                userInfoJson = response.body().string();
        } catch(IOException e) {
            e.printStackTrace();
        }
        UserInfo userInfo = getNull();

        //状态判断
        switch(statusOf(userInfoJson)) {
            case PRIVATE -> {
                String url = "https://dancedemo.shenghuayule.com/Dance/api/Common/Search?keyword=" + id + "&type=0&page=1&pagesize=1";
                try(Response response = HttpUtil.httpApi(url,
                        Map.of("Authorization", token.getBearerToken()))) {
                    if(response!=null && response.body()!=null)
                        userInfoJson = response.body().string();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                JsonArray jsonArray = JsonParser.parseString(userInfoJson).getAsJsonObject().get("List").getAsJsonArray();
                if(!jsonArray.isEmpty()) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    userInfo.userID = id;
                    userInfo.userName = jsonObject.get("Name").getAsString();
                    userInfo.headimgURL = jsonObject.get("HeadimgURL").getAsString();
                    userInfo.lvRatio = jsonObject.get("LvRatio").getAsInt();
                    userInfo.cityName = jsonObject.get("Region").getAsString();
                }
                userInfo.setStatus(InfoStatus.PRIVATE);
                return userInfo;
            }
            case NONEXISTENT -> {
                userInfo = new UserInfo();
                userInfo.setStatus(InfoStatus.NONEXISTENT);
                return userInfo;
            }
        }

        //构造
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        userInfo = gson.fromJson(userInfoJson, UserInfo.class);
        return userInfo;
    }

    @Override
    public String toString() {
        return "UserInfo{userID=%d, musicScore=%d, lvRatio=%d, rankNation=%d, comboPercent=%d, sex=%d, userName='%s', headimgURL='%s', phone='%s', cityName='%s', teamName='%s', titleUrl='%s', headimgBoxPath='%s'}".formatted(userID, musicScore, lvRatio, rankNation, comboPercent, sex, userName, headimgURL, phone, cityName, teamName, titleUrl, headimgBoxPath);
    }

    private static InfoStatus statusOf(String message) {
        if(message.contains("账号不存在")) return InfoStatus.NONEXISTENT;
        if(message.contains("已设置保密")) return InfoStatus.PRIVATE;
        return InfoStatus.OPEN;
    }
}

