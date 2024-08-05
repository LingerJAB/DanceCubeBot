package com.dancecube.token;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

/**
 * 舞立方登录鉴权 Token 类，用于账号操作时的身份识别和验证
 *
 * @author Lin
 */
public class Token {
    private final int userId;
    private String accessToken;
    private String refreshToken;
    // 是否过期，并且无法刷新Token
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

    /**
     * 通过查看账户未读消息检测是否Token可用
     * <p><b>*不严谨判定：</b>{@code available}表示是否可用访问API，但<b>不能判定{@code refresh_token}是否可再次刷新。</b>
     * 但事实上，{@code refresh_token}的{@code expire_in}并不准确，应该尽可能的刷新。
     * <p>
     * 而对于<b>能够即时刷新的定时程序</b>来说，可用认为{@code available}等价于{@code refreshable}</p>
     *
     * @return Token是否可用
     */
    public boolean checkAvailable() {
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Message/GetUnreadCount",
                Map.of("Authorization", getBearerToken()))) {
            available = response!=null && response.code()==200;
        }
        return !(!available | accessToken==null | refreshToken==null);
//        return available;
    }

    //如果是默认Token(公共token)
    @Deprecated
    public boolean isDefault() {
        return userId==0;
    }

    public boolean refresh() {
        if(!available) return false;

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
                    "desc"="token时长为%.3f天（大于7天可能需要重新登录）"
                }
                """)
                .formatted(userId, accessToken, refreshToken, recTime, (float) (System.currentTimeMillis() - recTime) / 86400_000);
    }

    public void forceAccessible() {
        this.available = true;
    }
}
