package com.mirai;


import com.mirai.event.AbstractHandler;

import java.util.HashMap;
import java.util.HashSet;

public class Main extends AbstractHandler {
    public static void main(String[] args) throws Exception {
        HashMap<Long, HashMap<String, HashSet<String>>> map = new HashMap<>();
    }

    public static void change(HashMap<Long, String> map, long key, String value) {
        map.put(key, value);
    }
}

