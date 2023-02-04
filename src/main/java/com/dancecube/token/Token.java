package com.dancecube.token;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

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
        // 过期检测
        if(System.currentTimeMillis() - recTime<604_800_000) return false;

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create("client_type=qrcode&grant_type=refresh_token&refresh_token=%s"
                .formatted(getRefreshToken()), mediaType);
        Request request = new Request.Builder()
                .url("https://dancedemo.shenghuayule.com/Dance/token")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
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
        return "Token{\nuserId=\"%s\",\naccessToken=\"%s\n,\nrefreshToken=\"%s\", recTime=%d\n}".formatted(userId, accessToken, refreshToken, recTime);
    }
}
