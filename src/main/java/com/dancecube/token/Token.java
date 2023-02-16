package com.dancecube.token;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirai.HttpUtils;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class Token {
    private final String userId;
    private String accessToken;
    private String refreshToken;
    private long recTime;

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Token(String userId, String accessToken, String refreshToken, long recTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.recTime = recTime;
    }

    public boolean refresh() {
        //TODO 必须过期检测 强制刷新会很耗时间（Http IO）
        if(System.currentTimeMillis() - recTime<604_800_000) return false;


        try {
            Response response = HttpUtils.httpApi("https://dancedemo.shenghuayule.com/Dance/token",
                    Map.of("content-type", "application/x-www-form-urlencoded"),
                    Map.of("client_type", "qrcode", "grant_type", "refresh_token", "refresh_token", refreshToken));
            JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
            response.close();
            if(response.code()!=200) {
                throw new IOException(response.code() + ":" + response.message());
            } else {
                accessToken = json.get("access_token").getAsString();
                refreshToken = json.get("refresh_token").getAsString();
                recTime = System.currentTimeMillis();
                return true;
            }
        } catch(IOException e) {
            System.out.println("# refreshTokenHttp执行bug辣！");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Token {\n\tuserId=\"%s\",\n\taccessToken=\"%s\n,\n\trefreshToken=\"%s\",\n\trecTime=%d\n}".formatted(userId, accessToken, refreshToken, recTime);
    }
}
