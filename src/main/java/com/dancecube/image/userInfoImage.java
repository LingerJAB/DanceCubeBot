package com.dancecube.image;


import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.LineAlign;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.mirai.event.AbstractHandler;

import java.awt.*;
import java.util.HashMap;

public class userInfoImage extends AbstractHandler {
    public static void main(String[] args) throws Exception {
        String filePath = windowsConfigPath + "UserToken.json";
        HashMap<Long, Token> map = TokenBuilder.tokensFromFile(filePath, false);
        UserInfo user = new UserInfo(map.get(2862125721L));
        System.out.println(user);
        String bgPath = "file:/C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\Background.png";
        String outputPath = "C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\test.png";

        dcImg(user, bgPath, outputPath);
//        breakLineSplitterTest(outputPath);

    }

    private static void dcImg(UserInfo user, String bgPath, String outputPath) {
        try {
            ImageCombiner combiner = new ImageCombiner(bgPath, OutputFormat.PNG);

//            combiner.addImageElement(bgPath,0,0);
            combiner.addImageElement(user.getHeadimgURL(), 78, 92).setWidth(86).setHeight(86).setZoomMode(ZoomMode.WidthHeight);
            combiner.addImageElement(user.getHeadimgBoxPath(), 49, 66).setWidth(145).setHeight(145).setZoomMode(ZoomMode.WidthHeight);
            if(!user.getTitleUrl().equals(""))
                combiner.addImageElement(user.getTitleUrl(), 61, 190).setWidth(121).setHeight(52).setZoomMode(ZoomMode.WidthHeight);
            combiner.addTextElement("%s\n\n战队：%s\n积分：%d\n金币：%d"
                                    .formatted(user.getUserName(), user.getTeamName(), user.getMusicScore(), user.getGold()),
                            "得意黑", 23, 203, 93).setBreakLineSplitter("\n")
                    .setAutoBreakLine(168, 10, 25, LineAlign.Left);
            combiner.addTextElement("战力：%s\n连击率：%.2f%%\n全国排名：%d"
                                    .formatted(user.getLvRatio(), (float) user.getComboPercent() / 100, user.getRankNation()),
                            "得意黑", 23, 74, 331).setBreakLineSplitter("\n")
                    .setAutoBreakLine(168, 10, 25, LineAlign.Left);

            combiner.combine(); //换行
            combiner.save2(outputPath);
            System.out.println("succeeded");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void breakLineSplitterTest(String outputPath) throws Exception {
        ImageCombiner combiner = new ImageCombiner("https://img.thebeastshop.com/combine_image/funny_topic/resource/bg_3x4.png", OutputFormat.JPG);
        Font font = new Font("阿里巴巴普惠体", Font.PLAIN, 62);

        //添加文字，并设置换行参数
        combiner.addTextElement("大江东去，浪淘尽，\r\n千古风流人物。\r\n故垒西边，\r\n人道是：三国周郎赤壁。\r\n乱石穿空，惊涛拍岸，卷起千堆雪。\r\n江山如画，\r\n一时多少豪杰。", font, 100, 0)
                .setColor(Color.red)
                .setCenter(true).setBreakLineSplitter("\r\n");                                                              //手动指定换行符setBreakLineSplitter（支持正则，会忽略自动宽度计算，方便多行文本绘制）
        //合成图片
        combiner.combine();
        combiner.save2(outputPath);

    }
}

enum Style {
    DEFAULT,
}