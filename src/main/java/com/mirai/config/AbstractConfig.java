package com.mirai.config;

import com.dancecube.token.Token;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractConfig {
    public static HashMap<Long, Token> userTokensMap;
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String linuxRootPath;
    public static String windowsRootPath;
    private static final boolean windowsMark;
    public static String configPath;

    // api key
    public static String gaodeApiKey = "";
    public static String tencentSecretId = "";
    public static String tencentSecretKey = "";

    static {

        windowsMark = new File("../WINDOWS_MARK").exists();
        try {
            linuxRootPath = new File("..").getCanonicalPath();
            windowsRootPath = new File("..").getCanonicalPath();

            //在项目下创建 “WINDOWS_MARK” 文件，存在即使用Windows路径的配置，而Linux则不需要
            if(itIsAReeeeaaaalWindowsMark()) {
                configPath = windowsRootPath + "/DcConfig/";
            } else {
                configPath = linuxRootPath + "/DcConfig/";
            }
            new File(configPath).mkdirs();
        } catch(IOException e) {
            e.printStackTrace();
        }


        // Authorization错误时查看控制台ip白名单
        Map<String, Map<String, String>> map;
        try {
            File apiKeyYml = new File(configPath + "ApiKeys.yml");
            if(!apiKeyYml.exists()) {
                apiKeyYml.getParentFile().mkdirs();
                apiKeyYml.createNewFile();
            }

            map = new Yaml().load(Files.readString(apiKeyYml.toPath()));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Map<String, String> tencentScannerKeys = map.get("tencentScannerKeys");
            Map<String, String> gaodeMapKeys = map.get("gaodeMapKeys");

            gaodeApiKey = gaodeMapKeys.get("apiKey");
            tencentSecretId = tencentScannerKeys.get("secretId");
            tencentSecretKey = tencentScannerKeys.get("secretKey");
        } catch(NullPointerException e) {
            System.out.println("# ApiKey配置不完整！");
            e.printStackTrace();
        }
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
