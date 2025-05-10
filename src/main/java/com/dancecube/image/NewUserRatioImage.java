package com.dancecube.image;

import com.dancecube.token.Token;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.mirai.config.AbstractConfig.configPath;

public class NewUserRatioImage {
    public static final BufferedImage CARD_1;
    public static final BufferedImage CARD_2;
    public static final BufferedImage CARD_3;
    public static final BufferedImage CARD_4;
    public static final BufferedImage CARD_5;

    public static final BufferedImage LV_SSS_AP;
    public static final BufferedImage LV_SSS;
    public static final BufferedImage LV_SS;
    public static final BufferedImage LV_S;
    public static final BufferedImage LV_A;
    public static final BufferedImage LV_B;
    public static final BufferedImage LV_C;
    public static final BufferedImage LV_D;

    public static String path = configPath + "Images/NewUserRatioImage/";

    static {
        try {
            // 素材缓存到内存
            CARD_1 = ImageIO.read(new File(path + "Card1.png"));
            CARD_2 = ImageIO.read(new File(path + "Card2.png"));
            CARD_3 = ImageIO.read(new File(path + "Card3.png"));
            CARD_4 = ImageIO.read(new File(path + "Card4.png"));
            CARD_5 = ImageIO.read(new File(path + "Card5.png"));
            LV_SSS_AP = ImageIO.read(new File(path + "SSS_AP.png"));
            LV_SSS = ImageIO.read(new File(path + "SSS.png"));
            LV_SS = ImageIO.read(new File(path + "SS.png"));
            LV_S = ImageIO.read(new File(path + "S.png"));
            LV_A = ImageIO.read(new File(path + "A.png"));
            LV_B = ImageIO.read(new File(path + "B.png"));
            LV_C = ImageIO.read(new File(path + "C.png"));
            LV_D = ImageIO.read(new File(path + "D.png"));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static InputStream generate(Token token) {
        return null;
    }
}
