package com.dancecube.token;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirai.HttpUtils;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class Token {
    private final int userId;
    private String accessToken;
    private String refreshToken;
    private long recTime;

    public int getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Token(int userId, String accessToken, String refreshToken, long recTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.recTime = recTime;
    }

    public boolean refresh() {

        //每refresh间隔为一个星期，防止出错改为6天
        if(System.currentTimeMillis() - recTime<518_400_000) return false;


        try {
            Response response = HttpUtils.httpApi("https://dancedemo.shenghuayule.com/Dance/token",
                    Map.of("content-type", "application/x-www-form-urlencoded"),
                    Map.of("client_type", "qrcode", "grant_type", "refresh_token", "refresh_token", refreshToken));
            JsonObject json;
            if(response!=null && response.body()!=null) {
                json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                response.close();
                if(response.code()!=200) {
                    throw new IOException(response.code() + " : ID:" + userId + " msg:" + response.message());
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
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\n    userId=\"%s\",\n    accessToken=\"%s\n,\n    refreshToken=\"%s\",\n    recTime=%d\n}"
                .formatted(userId, accessToken, refreshToken, recTime);
    }
}
