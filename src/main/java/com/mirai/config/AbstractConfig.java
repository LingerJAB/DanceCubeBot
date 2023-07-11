package com.mirai.config;

import com.dancecube.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractConfig {
    public static HashMap<Long, Token> userTokensMap = new HashMap<>();
    public static HashMap<Long, HashSet<String>> userInfoCommands = new HashMap<>();
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String linuxRootPath;

    //如果是在Windows IDEA里运行，请将 configPath 换成 windowsConfigPath
    public static String windowsRootPath;
    public static String configPath;

    static {
        try {
            linuxRootPath = new File("..").getCanonicalPath();
            windowsRootPath = new File(".").getCanonicalPath();

            //在项目下创建 “WINDOWS_MARK” 文件，存在即使用Windows配置，Linux则不需要
            configPath = (new File("./WINDOWS_MARK").exists() ? windowsRootPath : linuxRootPath) + "/DcConfig/";
//            if(new File("./WINDOWS_MARK").exists()) configPath = windowsConfigPath + "/DcConfig/";
//            configPath = linuxRootPath + "/DcConfig/";
        } catch(IOException e) {
            System.out.println("#DcCofig 读取出Bug辣！");
            e.printStackTrace();
        }

        //Todo 默认Token IO
        userTokensMap.put(0L, new Token(0, null));
    }
}
