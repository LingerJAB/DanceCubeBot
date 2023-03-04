package com.mirai.event;

import com.dancecube.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractHandler {
    public static HashMap<Long, Token> userTokensMap = new HashMap<>();
    public static HashMap<Long, HashSet<String>> userInfoCommands = new HashMap<>();
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String linuxRootPath;
    public static String windowsConfigPath;
    public static String configPath;

    static {
        try {
            linuxRootPath = new File("..").getCanonicalPath();
            windowsConfigPath = new File(".").getCanonicalPath() + "/DcConfig/";
            configPath = linuxRootPath + "/DcConfig/";
        } catch(IOException e) {
            System.out.println("#DcCofig 读取出Bug辣！");
            e.printStackTrace();
        }
    }
}
