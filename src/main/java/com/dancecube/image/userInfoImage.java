package com.dancecube.image;


import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.mirai.event.AbstractHandler;

import java.util.HashMap;

public class userInfoImage extends AbstractHandler {
    public static void main(String[] args) {
        String filePath = windowsConfigPath + "UserToken.json";
        HashMap<Long, Token> map = TokenBuilder.tokensFromFile(filePath, false);
        UserInfo user = new UserInfo(map.get(2404576981L));
        System.out.println(user);
        String bgPath = "file:/C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\Background.png";
        String outputPath = "C:\\Users\\周洁\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\test.png";
        try {
            ImageCombiner combiner = new ImageCombiner(bgPath, OutputFormat.PNG);

//            combiner.addImageElement(bgPath,0,0);
            combiner.addImageElement(user.getHeadimgURL(), 78, 92).setWidth(86).setHeight(86).setZoomMode(ZoomMode.WidthHeight);
            combiner.addImageElement(user.getHeadimgBoxPath(), 49, 66).setWidth(145).setHeight(145).setZoomMode(ZoomMode.WidthHeight);
//            combiner.addImageElement(user.getTitleUrl(), 61, 190).setWidth(121).setHeight(52).setZoomMode(ZoomMode.WidthHeight);
//            combiner.addTextElement("?！！?", 100, 0, 0);
            combiner.combine();
            combiner.save2(outputPath);
            System.out.println("succeeded");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}


enum Style {
    DEFAULT,
}