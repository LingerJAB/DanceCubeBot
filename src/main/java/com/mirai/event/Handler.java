package com.mirai.event;

import com.dancecube.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Handler {
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
