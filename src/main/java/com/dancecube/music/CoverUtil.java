package com.dancecube.music;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mirai.config.AbstractConfig.configPath;

public class CoverUtil {
    public static String officialImgPath = configPath + "Images/Cover/OfficialImage/";
    public static String customImgPath = configPath + "Images/Cover/CustomImage/";
    public static String coverImgPath = configPath + "Images/Cover/";

    static {
        new File(officialImgPath).mkdirs();
        new File(customImgPath).mkdirs();
    }

    ///不保证存在
    private static String getImgPath(int id) {
        if(id == 0) return coverImgPath + "default.jpg";
        return (MusicUtil.isOfficial(id) ? officialImgPath : customImgPath) + id + ".jpg";
    }

    /**
     * Absent adj.缺席的，没有的
     */
    public static boolean isCoverAbsent(int id) {
        return !new File(getImgPath(id)).exists();
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

    @Nullable
    public static BufferedImage getCoverOrDefault(int id) {
        if(isCoverAbsent(id)) id = 0;
        try {
            return ImageIO.read(new File(getImgPath(id)));
        } catch(IOException e) {
            return null;
        }
    }

    public static byte[] getCoverBytesOrDefault(int id) {
        if(isCoverAbsent(id)) id = 0;
        try {
            return Files.readAllBytes(Path.of(getImgPath(id)));
        } catch(IOException e) {
            return null;
        }
    }
}
