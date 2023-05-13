package com.dancecube.api;

import com.dancecube.token.Token;
import com.mirai.tools.HttpUtil;
import okhttp3.Response;

import java.util.Map;

public class Music {

    public static int gainMusicByCode(Token token, String code) {
        String auth = token.getAccessToken();

        Response response = HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/MusicData/GainMusicByCode?code=" + code,
                Map.of("Authorization", auth),
                null);
        if(response==null) return -1;
        int respCode = response.code();
        response.close();
        return respCode;
    }
}
