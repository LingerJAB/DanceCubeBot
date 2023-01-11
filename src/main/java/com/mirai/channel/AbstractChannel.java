package com.mirai.channel;

import com.dancecube.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

// 抽象通道，用于过滤分区
public abstract class AbstractChannel {
    public static HashMap<Long, Token> userMap = new HashMap<>();
    public static HashSet<Long> logStatus = new HashSet<>();
    public static String rootPath;
    static{
        try {
            // C:\Users\周洁\IdeaProjects\DanceCubeBot
            rootPath = new File("..").getCanonicalPath();

        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
