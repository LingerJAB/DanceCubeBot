package com.mirai;


import com.dancecube.token.TokenBuilder;
import com.mirai.event.AbstractHandler;

import java.io.IOException;

public class Main extends AbstractHandler {
    public static String url = "https://i.328888.xyz/2023/02/15/mNsWx.png";

    public static void main(String[] args) throws IOException {
        userTokensMap = TokenBuilder.tokensFromFile(windowsConfigPath + "UserToken.json");
//        Token token1 = userTokensMap.get(2862125721L);
//        UserInfo userInfo = UserInfo.get(token1);
//        System.out.println(userInfo);
    }


}

