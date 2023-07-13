package com.dancecube.info;

import com.dancecube.token.Token;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tools.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class AccountInfo {
    private int userID;

    public int getUserID() {
        return userID;
    }

    public int getGold() {
        return gold;
    }

    private int gold;

    public static AccountInfo get(Token token) {
        String accountInfoJson;
        Call call = HttpUtil.httpApiCall("https://dancedemo.shenghuayule.com/Dance/api/User/GetAccountInfo?userId=" + token.getUserId(), Map.of("Authorization", token.getBearerToken()));

        try(Response response = call.execute()) {
//            Response response = call.execute();
            accountInfoJson = response.body().string();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        return gson.fromJson(accountInfoJson, AccountInfo.class);
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "userID=" + userID +
                ", gold=" + gold +
                '}';
    }
}
