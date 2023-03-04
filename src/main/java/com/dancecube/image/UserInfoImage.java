package com.dancecube.image;


import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.LineAlign;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.mirai.event.AbstractHandler;

import java.io.InputStream;

public class UserInfoImage extends AbstractHandler {
    public static InputStream generate(Token token) {
        UserInfo user = new UserInfo(token);
        String linuxBackgroundPathUrl = "file:" + configPath + "Images/Background.png";
//        String linuxBackgroundPathUrl = "https://i.328888.xyz/2023/02/25/EfE1V.png";
//        Todo Linux 自定义本地目录

        try {
            ImageCombiner combiner = new ImageCombiner(linuxBackgroundPathUrl, OutputFormat.PNG);
            combiner.addImageElement(user.getHeadimgURL(), 120, 150).setWidth(137).setHeight(137).setZoomMode(ZoomMode.WidthHeight);
            combiner.addImageElement(user.getHeadimgBoxPath(), 74, 104).setWidth(230).setHeight(230).setZoomMode(ZoomMode.WidthHeight);
            if(!user.getTitleUrl().equals("")) // Todo 换行
                combiner.addImageElement(user.getTitleUrl(), 108, 300).setWidth(161).setHeight(68).setZoomMode(ZoomMode.WidthHeight);
            combiner.addTextElement("%s\n\n战队：%s\n积分：%d\n金币：%d".formatted(user.getUserName(), user.getTeamName(), user.getMusicScore(), user.getGold()), "得意黑", 36, 293, 137).setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 40, LineAlign.Left);
            combiner.addTextElement("战力：%s\n连击率：%.2f%%\n全国排名：%d".formatted(user.getLvRatio(), (float) user.getComboPercent() / 100, user.getRankNation()), "得意黑", 36, 95, 472).setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 40, LineAlign.Left);

            combiner.combine(); //换行
//            combiner.save2(windowsConfigPath+"Images/result.png");
            return combiner.getCombinedImageStream();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void generate(Token token, String savingPath) {
        UserInfo user = new UserInfo(token);
        String bgPath = "file:///" + windowsConfigPath + "Images/Background.png";
        String linuxBackgroundPath = "https://i.328888.xyz/2023/02/25/EfE1V.png";
        //Todo Linux 自定义本地目录

        try {
            ImageCombiner combiner = new ImageCombiner(linuxBackgroundPath, OutputFormat.PNG);
            combiner.addImageElement(user.getHeadimgURL(), 78, 92).setWidth(86).setHeight(86).setZoomMode(ZoomMode.WidthHeight);
            if(!user.getHeadimgBoxPath().isBlank())
                combiner.addImageElement(user.getHeadimgBoxPath(), 49, 66).setWidth(145).setHeight(145).setZoomMode(ZoomMode.WidthHeight);
            if(!user.getTitleUrl().isBlank())
                combiner.addImageElement(user.getTitleUrl(), 61, 190).setWidth(121).setHeight(52).setZoomMode(ZoomMode.WidthHeight);
            combiner.addTextElement("%s\n\n战队：%s\n积分：%d\n金币：%d".formatted(user.getUserName(), user.getTeamName(), user.getMusicScore(), user.getGold()), "得意黑", 23, 203, 93).setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 25, LineAlign.Left);
            combiner.addTextElement("战力：%s\n连击率：%.2f%%\n全国排名：%d".formatted(user.getLvRatio(), (float) user.getComboPercent() / 100, user.getRankNation()), "得意黑", 23, 74, 331).setBreakLineSplitter("\n").setAutoBreakLine(168, 10, 25, LineAlign.Left);

            combiner.combine(); //换行
            combiner.save2(savingPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
