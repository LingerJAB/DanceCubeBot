package com.dancecube.token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mirai.HttpUtils;
import okhttp3.Call;
import okhttp3.Response;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TokenBuilder {
    private static int index = 0;
    private static final String[] ids = {"yyQ6VxqMeIKJDzVmQuHtNAUGxHAgSxmR", "yyQ6VxqMeIL2hceWzZtdqsJxNf/hHSzH", "yyQ6VxqMeIL2hceWzZtdqtGq81Ru8pIE", "yyQ6VxqMeILLsdiEbWkbSnddhlyVGcNa", "yyQ6VxqMeILneEzfVyXPFVCZoOuXSoH3"};
    private final String id;

    public TokenBuilder() {
        this.id = getId();
    }

    private static String getId() {
        //ID会一段时间释放，需要换用ID
        if(index>ids.length - 1) index = 0;
        return ids[index++];
    }

    public String getQrcodeUrl() {
        return getQrcodeUrl(id);
    }

    private String getQrcodeUrl(String id) {
        try {
            Response response = HttpUtils.httpApi("https://dancedemo.shenghuayule.com/Dance/api/Common/GetQrCode?id=" + id);
            String string = response.body().string();
            response.close(); // 释放
            return JsonParser.parseString(string).getAsJsonObject().get("QrcodeUrl").getAsString();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Token getToken() {
        long curTime = System.currentTimeMillis();
        Call call = HttpUtils.httpApiCall("https://dancedemo.shenghuayule.com/Dance/token",
                Map.of("client_type", "qrcode", "grant_type", "client_credentials", "client_id", id),
                Map.of("content-type", "application/x-www-form-urlencoded"));
        Response response;
        //五分钟计时
        while(System.currentTimeMillis() - curTime<300_000) {
            try {
                //call不能重复请求
                response = call.clone().execute();
                //未登录为 400 登录为 200
                if(response.code()==200) {
                    JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                    return new Token(json.get("userId").getAsString(), json.get("access_token").getAsString(), json.get("refresh_token").getAsString(), System.currentTimeMillis());
                }
                response.close();  // 关闭释放
            } catch(IOException e) {
                System.out.println("# TokenHttp执行bug辣！");
                e.printStackTrace();
            }
        }
        return null;
    }


    // HashMap写入json文件（一般T为Long）
    public static void tokensToFile(HashMap<Long, Token> tokenMap, String filePath) {
        Type type = new TypeToken<HashMap<Long, Token>>() {
        }.getType();
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(tokenMap, type);

        try {
            FileOutputStream stream = new FileOutputStream(filePath);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch(IOException e) {
            System.out.println("# TokenToFile执行bug辣！");
            throw new RuntimeException(e);
        }
    }

    // HashMap写出json文件（一般为Long）
    public static HashMap<Long, Token> tokensFromFile(String filePath, boolean refreshed) {
        Type type = new TypeToken<HashMap<Long, Token>>() {
        }.getType();
        StringBuilder json = new StringBuilder();

        try(FileInputStream stream = new FileInputStream(filePath)) {
            // read读取字符到json
            int i = stream.read();
            while(i!=-1) {
                json.append((char) i);
                i = stream.read();
            }
        } catch(IOException e) {
            System.out.println("# TokenFromFile执行bug辣！");
            throw new RuntimeException(e);
        }
        HashMap<Long, Token> userMap = new Gson().fromJson(json.toString(), type);

        // 读取并refresh()
        if(refreshed) userMap.forEach((key, token) -> token.refresh());
        return userMap;
    }

    public static HashMap<Long, Token> tokensFromFile(String filePath) {
        return tokensFromFile(filePath, false);
    }
}
