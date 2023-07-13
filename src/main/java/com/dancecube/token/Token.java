package com.dancecube.token;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class Token {
    private final int userId;
    private String accessToken;
    private String refreshToken;
    private boolean available = true;
    private long recTime;

    public int getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getBearerToken() {
        return "bearer " + accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getRecTime() {
        return recTime;
    }

    public Token(int userId, String accessToken, String refreshToken, long recTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.recTime = recTime;
    }

    /**
     * 仅临时创建
     */
    public Token(int id, @Nullable String accessToken) {
        this.userId = id;
        this.accessToken = accessToken;
    }

    /**
     * 仅临时创建
     */
    public Token(@Nullable String accessToken) {
        this.userId = 0;
        this.accessToken = accessToken;
    }

    public boolean isAvailable() {
        if(!available | accessToken==null | refreshToken==null) return false;
        Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Message/GetUnreadCount",
                Map.of("Authorization", getBearerToken()));
        boolean available = response!=null && response.code()==200;
        this.available = available;
        return available;
    }

    //如果是默认Token(公共token)
    public boolean isDefault() {
        return userId==0;
    }

    public boolean refresh() {
        return refresh(false);
    }

    /**
     * @param ignoreWaiting 忽略默认等待时间
     */
    public boolean refresh(boolean ignoreWaiting) {
        if(!available) return false;
        //每refresh间隔为一个星期，防止出错改为4天
        if(!ignoreWaiting && System.currentTimeMillis() - recTime<345_600_000) return false;
        try {
            Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/token",
                    Map.of("content-type", "application/x-www-form-urlencoded"),
                    Map.of("client_type", "qrcode", "grant_type", "refresh_token", "refresh_token", refreshToken));
            JsonObject json;
            if(response!=null && response.body()!=null) {
                json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                response.close();
                if(response.code()!=200) {
                    available = false;
                    throw new IOException("code:" + response.code() + " id:" + userId + " msg:" + response.message());
                } else {
                    accessToken = json.get("access_token").getAsString();
                    refreshToken = json.get("refresh_token").getAsString();
                    recTime = System.currentTimeMillis();
                    return true;
                }
            }
        } catch(IOException e) {
            System.out.println("# refreshTokenHttp执行bug辣！");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return ("""
                {
                    "userId"="%s",
                    "accessToken"="%s",
                    "refreshToken"="%s",
                    "recTime"=%d
                }
                token时长：%.3f天（大于7天需要重新登录）
                """)
                .formatted(userId, accessToken, refreshToken, recTime, (float) (System.currentTimeMillis() - recTime) / 86400_000);
    }

    public void forceValidity() {
        this.available = true;
    }
}
