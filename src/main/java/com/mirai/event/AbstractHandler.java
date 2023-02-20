package com.mirai.event;

import com.dancecube.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractHandler {
    public static HashMap<Long, Token> userTokensMap = new HashMap<>();
    public static HashMap<Long, String> userInfoCommand = new HashMap<>();
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String rootPath;
    public static String windowsConfigPath;
    public static String configPath;

    static {
        try {
            rootPath = new File("..").getCanonicalPath();
            windowsConfigPath = rootPath + "/DanceCubeBot/DcConfig/";
            configPath = rootPath + "/DcConfig/";
        } catch(IOException e) {
            System.out.println("#DcCofig 读取出Bug辣！");
            e.printStackTrace();
        }
    }
}
