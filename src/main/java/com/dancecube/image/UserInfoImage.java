package com.dancecube.image;


import com.dancecube.api.AccountInfo;
import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.mirai.config.AbstractConfig;

import java.awt.*;
import java.io.*;
import java.util.concurrent.CompletableFuture;

public class UserInfoImage extends AbstractConfig {
    public static InputStream generate(Token token) {
        String linuxBackgroundPathUrl = "file:" + configPath + "Images/Background.png";
//        String linuxBackgroundPathUrl = "https://i.imgloc.com/2023/04/11/ip37wc.png";

        AllInfo allInfo = new AllInfo();
        CompletableFuture<AllInfo> userInfoFuture = CompletableFuture.supplyAsync(() -> allInfo.setUserInfo(UserInfo.get(token)));
        CompletableFuture<AllInfo> accountInfoFuture = CompletableFuture.supplyAsync(() -> allInfo.setAccountInfo(AccountInfo.get(token)));
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(accountInfoFuture, userInfoFuture);

        allFutures.join();
        UserInfo userInfo = allInfo.getUserInfo();
        AccountInfo accountInfo = allInfo.getAccountInfo();

        try {
            ImageCombiner combiner = new ImageCombiner(linuxBackgroundPathUrl, OutputFormat.PNG);
            combiner.addImageElement(userInfo.getHeadimgURL(), 120, 150).setWidth(137).setHeight(137).setZoomMode(ZoomMode.WidthHeight);

            if(userInfo.getHeadimgBoxPath()!=null && !userInfo.getHeadimgBoxPath().equals("")) // 头像框校验
                combiner.addImageElement(userInfo.getHeadimgBoxPath(), 74, 104).setWidth(230).setHeight(230).setZoomMode(ZoomMode.WidthHeight);

            if(userInfo.getTitleUrl()!=null && !userInfo.getTitleUrl().equals("")) // 头衔校验
                combiner.addImageElement(userInfo.getTitleUrl(), 108, 300).setWidth(161).setHeight(68).setZoomMode(ZoomMode.WidthHeight);

            combiner.addTextElement("%s\n\n战队：%s\n积分：%d\n金币：%d".formatted(userInfo.getUserName(), userInfo.getTeamName(), userInfo.getMusicScore(), accountInfo.getGold()), "得意黑", 36, 293, 137).setAutoBreakLine("\n");//.setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 40, LineAlign.Left);
            combiner.addTextElement("战力：%s\n连击率：%.2f%%\n全国排名：%d".formatted(userInfo.getLvRatio(), (float) userInfo.getComboPercent() / 100, userInfo.getRankNation()), "得意黑", 36, 95, 472).setAutoBreakLine("\n");//.setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 40, LineAlign.Left);

            combiner.combine();
            return combiner.getCombinedImageStream();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

//    @Test
//    public void test() throws IOException {
//        int id = 939088;
//        String accKey = "81BJ6GM2FKqu-WS5Ii7C49ABsbeb7ThCHZ23QwgcaZCsvr5mnDgJJD62WWuEKHneq8IKRkUJGkPOixRoBCI_JeKqsdny8cqpSdOpDOlEnJiQLZNiqbx0B4wzOX5F3xxWrHQi5bGG3ogPEL_o-1KPdI903EoHsJJ3OPrXkNHfQ7q7dKo-aNZ-lu1GcQ_tIB-ZcXfjJIh_19t8t6YiJAiNb1-l4FIfcRsmBKGHDjDE7Je3o_JcUuljkz8-Xauy4QXMPLJJ_7hT5MsVF8SkKQmr8hEI-DMESGAleu5kGC6FWFiigclEw5Q3m4jZ1a7JR9M51pcnsPukIDbmNRm_SbKirDUTt_OAl3RnZZuMkTQg6O1cQRsi1g9fBA2TpsINOGfE-3kHpZvh2_db8bWk75ZeNl_gSf2q5_Aj6Ehr9-5HGSPipoUpZlDIA6W8TT0fq1gNFodrX4H3riiOuEZorNXrCyDJW4yZ4wymXtU4B2BUn9hCkpf4_g0PFGJ8Mgn30mNk7jVk_3RB5BeRzsETNrl5czk5YofYy5_9JV5alB77xAiCJPLiQZXtXKXhzxYaT3WIr1yNXSd_xb8wGjHSZYL-f68gJMwamiQBBOBxAJQ8g7R9sHYzwfCVlKI2fGMR6SMunm-1089Dmt-XLRRQClhkB3NKYlhhji6jfFfKkxd30-QIITXgq48RdWdvxE6ykDJvs6HbhNh0eXzUa4hWJuQXOW8MVNOzqYWOdJ7pYZQ0YySy39KQzYHjswdioRxKDdWdm4Ly2ItwxA-KYbWW2UXk_xsbxYAlCpV7H-D-Fen0_BuzdEHIphdF10Y6RyZst-_0kUwCwZ3LYaPoRHnj8s2I24EnlgrzHlwv5HLT0Gg0NojRug56SmyFN3oN4ie8Ypxc3sAMuZt3DondyTGAUmeXKIzTcj6Yw1obUjMZfD3YfsuX8TKlPh9GNIKOytNqbrTiQUxkBEsW_gIexOIE2ir5v373LmuB7CnJ2XVVkAgoOTA";
//        Token token = new Token(id, accKey, "", 0L);
//
//        UserInfo userInfo = UserInfo.get(token);
//        System.out.println(userInfo);
//
//        InputStream stream = generate(token);
//        saveImg(stream);
//        System.out.println(System.currentTimeMillis());
//    }

    public static void saveImg(InputStream stream) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while((len = stream.read(buffer))!=-1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        stream.close();
        //把outStream里的数据写入内存

        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = outStream.toByteArray();
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File("C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png");
        //创建输出流
        FileOutputStream fileOutStream = new FileOutputStream(imageFile);
        //写入数据
        fileOutStream.write(data);
    }

    public static void getAvailableFonts() {
        // 获取系统所有可用字体名称
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName = e.getAvailableFontFamilyNames();
        for(String s : fontName) {
            System.out.println(s);
        }
    }
}

class AllInfo {
    private UserInfo userInfo;
    private AccountInfo accountInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public AllInfo setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public AllInfo setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        return this;
    }
}
