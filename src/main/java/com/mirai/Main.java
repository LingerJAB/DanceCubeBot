package com.mirai;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        String s = "https://dancedemo.com/s?q=110";
        System.out.println(URLEncoder.encode(s, StandardCharsets.UTF_8));

    }

}
