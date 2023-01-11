package com.mirai;

import static com.mirai.HttpUtils.QrDecode;

public class Main {
    public static void main(String[] args) throws Exception {
        String content = QrDecode("C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\qr1.jpg");
        System.out.println(content);
    }

}
