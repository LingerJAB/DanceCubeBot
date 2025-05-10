package com.dancecube.api;

import com.dancecube.token.Token;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tools.HttpUtil;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

public class PhoneLoginBuilder {
    private final String phoneNumber;

    public PhoneLoginBuilder(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 获取图形验证码
     *
     * @return 图片输入流
     */
    public InputStream getGraphCode() {
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetGraphCode?phone=" + phoneNumber)) {
            if(response != null && response.code() == 200) {
                String base64 = "";
                if(response.body() != null) {
                    base64 = response.body().string();
                    base64 = base64.substring(1, base64.length() - 1);
                }
                byte[] bytes = Base64.getDecoder().decode(base64);
                return new ByteArrayInputStream(bytes);
            }
        } catch(IOException e) {
            return null;
        }
        return null;
    }


    /**
     * 获取短信验证码
     *
     * @param graphCode 图形验证码
     * @return 是否成功发送
     */
    public boolean getSMSCode(String graphCode) {
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetSMSCode?phone=" + phoneNumber + "&graphCode=" + graphCode)) {
            return (response != null && response.code() == 200);
        }
    }

    /**
     * 手机号登录
     *
     * @param smsCode 短信验证码
     * @return Token令牌
     */
    public Token login(String smsCode) {
        try(Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/token",
                Map.of("content-type", "application/x-www-form-urlencoded"),
                Map.of("client_type", "phone", "grant_type", "client_credentials", "client_id", phoneNumber, "client_secret", smsCode)
        )) {
            if(response.body() != null && response.code() == 200) {
                JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                return new Token(json.get("userId").getAsInt(),
                        json.get("access_token").getAsString(),
                        json.get("refresh_token").getAsString(),
                        System.currentTimeMillis());
            }
        } catch(IOException e) {
            return null;
        }
        return null;
    }
}