package com.mirai;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mirai.event.AbstractHandler;

public class Main extends AbstractHandler {
    public static void main(String[] args) {
//        String path = "file:///C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\" + "qr2.jpg";
        String path = "https://i.328888.xyz/2023/02/11/Rabtq.jpeg";

        System.out.println(path);
//        String qrDecode = HttpUtils.QrDecodeTencent(path);
//        System.out.println(qrDecode);


        String json = "{\"CodeResults\":[{\"TypeName\":\"QR_CODE\",\"Url\":\"http://weixin.qq.com/q/020Kl-sWLofb11OS-GNzck\",\"Position\":{\"LeftTop\":{\"X\":227,\"Y\":297},\"RightTop\":{\"X\":702,\"Y\":297},\"RightBottom\":{\"X\":702,\"Y\":768},\"LeftBottom\":{\"X\":227,\"Y\":768}}}],\"ImgSize\":{\"Wide\":960,\"High\":1280},\"RequestId\":\"24a87dff-1d2b-48b6-86bc-06de4ccdba72\"}";
        System.out.println(getUrl(json));

    }

    //TODO 二维码好了
    public static String getUrl(String json) {
        JsonElement codeResult = JsonParser.parseString(json).getAsJsonObject().get("CodeResults");
        return codeResult.getAsJsonArray().get(0).getAsJsonObject().get("Url").getAsString();
    }
}