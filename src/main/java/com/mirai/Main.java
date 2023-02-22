package com.mirai;


import com.dancecube.api.Machine;
import com.mirai.event.AbstractHandler;

import java.io.IOException;

public class Main extends AbstractHandler {
    public static String url = "https://i.328888.xyz/2023/02/15/mNsWx.png";

    public static void main(String[] args) throws IOException {
        String path = windowsConfigPath + "UserToken.json";
//        userTokensMap = TokenBuilder.tokensFromFile(path);
//        Token token1 = userTokensMap.get(2862125721L);
//        UserInfo userInfo = new UserInfo(token1);
//        System.out.println(userInfo);

        try {
            System.out.println(Machine.getMachineList("安徽大学"));
        } catch(Exception e) {
            e.printStackTrace();
        }

    }


}

