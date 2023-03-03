package com.mirai;


import com.mirai.event.AbstractHandler;

public class Main extends AbstractHandler {
    public static void main(String[] args) throws Exception {
        System.out.println(System.currentTimeMillis());
        System.out.println(HttpUtils.tencentSecretId);
        System.out.println(HttpUtils.tencentSecretKey);
        System.out.println(HttpUtils.gaodeApiKey);
        System.out.println(System.currentTimeMillis());

    }
}

