package com.mirai;


import com.mirai.event.AbstractHandler;

import java.awt.*;
import java.io.IOException;

public class Main extends AbstractHandler {
    public static String url = "https://i.328888.xyz/2023/02/15/mNsWx.png";

    public static void main(String[] args) throws IOException, InterruptedException {
        String path = windowsConfigPath + "UserToken.json";
//        userTokensMap = TokenBuilder.tokensFromFile(path);
//        Token token1 = userTokensMap.get(2862125721L);
//        UserInfo userInfo = new UserInfo(token1);
//        System.out.println(userInfo);
//
        showFonts();

    }

    public static void showFonts() throws InterruptedException {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName = e.getAvailableFontFamilyNames();
        for(String s : fontName) {
            System.out.println(s);
        }
    }

}

