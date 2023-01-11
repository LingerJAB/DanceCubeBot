package com.dancecube.image;


import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;

import java.util.HashMap;

public class userInfoImage {
    public static void main(String[] args) {
        String rootPath="C:Users/周洁/IdeaProjects/DanceCubeBot/DcConfig/";
        HashMap<Long, Token> map = TokenBuilder.tokensFromFile(rootPath+"UserToken.json",true);
        UserInfo user = UserInfo.get(map.get(2862125721L));

        String path = rootPath+"Images/test.png";
        String bgPath = rootPath+"Images/bg.png";
//        String bgPath = rootPath+"Images/bg.png";

        try {
            ImageCombiner combiner = new ImageCombiner(bgPath, OutputFormat.PNG);

            combiner.addTextElement("??", 10, 0, 0);
//            combiner.addImageElement(bgPath,0,0);
            combiner.addImageElement(user.HeadimgURL, 0, 0);
            combiner.combine();
            combiner.save(path);
            System.out.println("succeeded");
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}


enum Style {
    DEFAULT,

}