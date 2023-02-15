package com.mirai;


import com.mirai.event.AbstractHandler;

public class Main extends AbstractHandler {
    public static void main(String[] args) {
        String path = "https://i.328888.xyz/2023/02/11/Rabtq.jpeg";
        path = "https://i.328888.xyz/2023/02/15/mNvpk.png";

        System.out.println(path);
        String qrDecode = HttpUtils.QrDecodeTencent(path);
        System.out.println(qrDecode);

    }
}