package com.dancecube.api;

import com.dancecube.token.Token;
import com.mirai.tools.HttpUtil;
import okhttp3.Response;

import java.util.Map;

public class PlayerMusic {

    public static Response gainMusicByCode(Token token, String code) {
        return HttpUtil.httpApi("https://dancedemo.shenghuayule.com/Dance/api/MusicData/GainMusicByCode?code=" + code,
                Map.of("Authorization", token.getBearerToken()),
                null);
    }

}
