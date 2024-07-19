package com.dancecube.music;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.mirai.config.AbstractConfig.configPath;

public class CoverUtil {
    public static String path = configPath + "Images/Material/";
    public static String officialImgPath = configPath + "Images/Cover/OfficialImage/";
    public static String customImgPath = configPath + "Images/Cover/CustomImage/";

    static {
        new File(officialImgPath).mkdirs();
        new File(customImgPath).mkdirs();
    }

    ///不保证存在
    private static String getImgPath(int id) {
        return (MusicUtil.isOfficial(id) ? officialImgPath : customImgPath) + id + ".jpg";
    }

    /**
     * Absent adj.缺席的，没有的
     */
    public static boolean isCoverAbsent(int id) {
        return !new File(getImgPath(id)).exists();
    }

    @Nullable
    private static BufferedImage getCoverOrNull(int id) {
        // 先从官谱音乐取封面
        try {
            return ImageIO.read(new File(getImgPath(id)));
        } catch(IOException e) {
            return null;
        }
    }

    public static void downloadCover(int id) {
        Music music = MusicUtil.getMusic(id);
        BufferedImage image;
        File imgFile;
        try {
            image = ImageIO.read(new URL(music.getCoverUrl()));
            imgFile = new File(getImgPath(id));
            ImageIO.write(image, "JPG", imgFile);
        } catch(IOException e) {
            throw new RuntimeException(id + "的id 封面url 无效");
        }
    }

    public static BufferedImage getCover(int id) {
        BufferedImage cover = getCoverOrNull(id);
        if(cover!=null) {
            return cover;
        }
        downloadCover(id);
        return getCoverOrNull(id);
    }
}
