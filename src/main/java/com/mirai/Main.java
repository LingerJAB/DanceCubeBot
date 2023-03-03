package com.mirai;


import com.mirai.event.AbstractHandler;

public class Main extends AbstractHandler {
    public static void main(String[] args) throws Exception {
        String info = HttpUtils.getLocationInfo("六安");
        System.out.println(info);
    }
}

