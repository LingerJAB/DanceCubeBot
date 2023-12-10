package com.mirai.config;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.MiraiBot;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public abstract class AbstractConfig {
    public static JavaPluginScheduler scheduler;
    public static HashMap<Long, Token> userTokensMap;
    //    public static HashMap<Long, HashSet<String>> userInfoCommands = new HashMap<>();
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String linuxRootPath;
    private static final boolean windowsMark;

    //如果是在Windows IDEA里运行，请将 configPath 换成 windowsConfigPath
    public static String windowsRootPath;
    public static String configPath;

    static {

        windowsMark = new File("./WINDOWS_MARK").exists();
        try {
            linuxRootPath = new File("..").getCanonicalPath();
            windowsRootPath = new File(".").getCanonicalPath();

            //在项目下创建 “WINDOWS_MARK” 文件，存在即使用Windows路径的配置，而Linux则不需要
            if(itIsAReeeeaaaalWindowsMark()) {
                configPath = windowsRootPath + "/DcConfig/";
            } else {
                scheduler = MiraiBot.INSTANCE.getScheduler();
                configPath = linuxRootPath + "/DcConfig/";
            }
        } catch(IOException e) {
            System.out.println("#DcCofig 读取出Bug辣！");
            e.printStackTrace();
        }

        // 导入Token
        userTokensMap = Objects.requireNonNullElse(
                TokenBuilder.tokensFromFile(configPath + "UserTokens.json"),
                new HashMap<>());
//        userTokensMap.put(0L, new Token(0, null));
    }

    /**
     * 让我看看是不是Windows！
     *
     * @return 这是个Windows系统
     */
    public static boolean itIsAReeeeaaaalWindowsMark() {
        return windowsMark;
    }
}
