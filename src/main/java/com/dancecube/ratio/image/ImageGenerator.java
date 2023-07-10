package com.dancecube.ratio.image;

import com.dancecube.info.UserInfo;
import com.dancecube.music.AnyMusic;
import com.dancecube.music.Officials;
import com.dancecube.ratio.rankingMusic.LvRatioCalculator;
import com.dancecube.ratio.rankingMusic.RankMusicInfo;
import com.dancecube.ratio.rankingMusic.RecentMusicInfo;
import com.dancecube.ratio.rankingMusic.SingleRank;
import com.dancecube.token.Token;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.dancecube.ratio.image.ImageDrawer.printAvailableFonts;

public class ImageGenerator {
    public static Token token = new Token(939088,
            "IQN0ollrA8xdLUBDgrM3iG0MrDhR5IZVq0fxI6hN-aEiSQibwTSttky-fT_ruwv97gKANnSuPJrUoXDFnEWPwjuOLgSbX5rsw2ZnD1PRnzVla68u_oG0CVAkUmsS6Kp62nHVVet6F9wRnWRbsPfO7wnn5YmkMx1oz_wa0RVZqH-q2J4eUEjunPmjqLxTk_SU5DmoraDOq3XLq2rWM045Uci712ictoPKBG6v4kbWqiL5Kxe8TNAbgAoDpz8NYG_p8h6MfFyAAPpL_3twplsY2-40U38kEWJdDbfpvHYgdITLAXXMmByeaF8jsgEH6EzS1w_TFbJPtcs7Euz6mkb93eVsUZE3_vHYTd6HxXuXsVUrUMDaQs9-3m5DsaYrF8Je",
            "",
            9999);
    public static UserInfo info = UserInfo.get(token);
    public static String path = "C:\\Users\\Lin\\IdeaProjects\\LvRatioCalculator\\src\\Materials\\";
    public static String officialImgPath = "C:\\Users\\Lin\\IdeaProjects\\LvRatioCalculator\\src\\all\\officialImg\\";
    public static File defaultImg = new File(officialImgPath + "default.png");

    public static void main(String... args) throws Exception {

        System.out.println(new File(path + "Card1.png").exists());

        BufferedImage card1 = ImageIO.read(new File(path + "Card1.png"));
        BufferedImage card2 = ImageIO.read(new File(path + "Card2.png"));
        BufferedImage card3 = ImageIO.read(new File(path + "Card3.png"));
        BufferedImage lvSSS = ImageIO.read(new File(path + "SSS.png"));
        BufferedImage lvSS = ImageIO.read(new File(path + "SS.png"));
        BufferedImage lvS = ImageIO.read(new File(path + "S.png"));
        BufferedImage lvA = ImageIO.read(new File(path + "A.png"));
        BufferedImage lvB = ImageIO.read(new File(path + "B.png"));
        BufferedImage lvC = ImageIO.read(new File(path + "C.png"));
        BufferedImage lvD = ImageIO.read(new File(path + "D.png"));
//        BufferedImage cover = ImageIO.read(new File("C:\\Users\\Lin\\IdeaProjects\\LvRatioCalculator\\src\\all\\officialImg\\484.jpg"));

        BufferedImage avatar = ImageIO.read(new URL(info.getHeadimgURL()));
        BufferedImage box = ImageIO.read(new URL(info.getHeadimgBoxPath()));
        BufferedImage title = ImageIO.read(new URL(info.getTitleUrl()));

        BufferedImage backgroundImg = ImageIO.read(new File(path + "Main.png"));
        ImageDrawer drawer = new ImageDrawer(backgroundImg);
//        BufferedImage resultImg = ImageIO.read(new File(path + "result.png"));

        // 个人信息 头像/头衔
        drawer.drawImage(avatar, 34, 180, 174, 174)
                .drawImage(box, -24, 122, 290, 290)
                .drawImage(title, 28, 373, 186, 79);

        // 个人信息 文字
        // TODO 段位
        String text = """
                是铃酱呐~

                战队：%s
                排名：%d
                战力：%d""".formatted(info.getTeamName(), info.getRankNation(), info.getLvRatio());
        Font font = new Font("得意黑", Font.PLAIN, 45);
        drawer.color(Color.BLACK)
                .font(font)
                .drawText(text, 245, 189, new textEffect(null, 0));

        // B15

        ArrayList<RankMusicInfo> allRankList = LvRatioCalculator.getAllRankList(token.getBearerToken(), true);
        List<RankMusicInfo> rank15List = LvRatioCalculator.getSubRank15List(allRankList);
        int index = 0;
        int dx = 395, dy = 180; //x y延伸长度
        Font titleFont = new Font("Microsoft YaHei UI", Font.BOLD, 32);
        Font scoreFont = new Font("庞门正道标题体", Font.PLAIN, 52);
        Font infoFont = new Font("庞门正道标题体", Font.PLAIN, 15);
        Font levelFont = new Font("庞门正道标题体", Font.PLAIN, 23);
        drawer.antiAliasing(); // 抗锯齿
        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                int dx2 = col * dx;
                int dy2 = row * dy;
                RankMusicInfo musicInfo = rank15List.get(index);
                BufferedImage cover = getCover(musicInfo.getId());
                SingleRank bestInfo = musicInfo.getBestInfo();
                BufferedImage card = switch(bestInfo.getDifficulty()) {
                    case 0 -> card1;
                    case 1 -> card2;
                    case 2 -> card3;
                    default -> throw new IllegalStateException("Unexpected value: " + bestInfo.getDifficulty());
                };
                BufferedImage grade = switch(bestInfo.getGrade()) {
                    case SSS -> lvSSS;
                    case SS -> lvSS;
                    case S -> lvS;
                    case A -> lvA;
                    case B -> lvB;
                    case C -> lvC;
                    default -> lvD;
                };
                imageEffect effect = new imageEffect(35, 35);
                drawer.drawImage(cover, 15 + dx2, 620 + dy2, 130, 158, effect)
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 650 + dy2, new textEffect(220, null))
                        .font(scoreFont).drawText(String.valueOf(bestInfo.getScore()), 160 + dx2, 658 + dy2)
                        .font(infoFont).drawText("%d\n%d\n%.2f%%".formatted(bestInfo.getCombo(), bestInfo.getMiss(), bestInfo.getAcc()), 230 + dx2, 740 + dy2, new textEffect(null, 1))
                        .font(levelFont, Color.WHITE).drawText(String.valueOf(bestInfo.getLevel()), 17 + dx2, 749 + dy2);
            }
        }

        ArrayList<RecentMusicInfo> allRecentList = LvRatioCalculator.getAllRecentList(token.getBearerToken(), true);
        List<RecentMusicInfo> recent15List = LvRatioCalculator.getSubRecent15List(allRecentList);
        index = 0;
        drawer.antiAliasing(); // 抗锯齿
        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                int dx2 = col * dx;
                int dy2 = row * dy + 1065;
                RecentMusicInfo musicInfo = recent15List.get(index);
                BufferedImage cover = getCover(musicInfo.getId());
                BufferedImage card = switch(musicInfo.getDifficulty()) {
                    case 0 -> card1;
                    case 1 -> card2;
                    case 2 -> card3;
                    default -> throw new IllegalStateException("Unexpected value: " + musicInfo.getDifficulty());
                };
                BufferedImage grade = switch(musicInfo.getGrade()) {
                    case SSS -> lvSSS;
                    case SS -> lvSS;
                    case S -> lvS;
                    case A -> lvA;
                    case B -> lvB;
                    case C -> lvC;
                    default -> lvD;
                };
                imageEffect effect = new imageEffect(35, 35);
                drawer.drawImage(cover, 15 + dx2, 620 + dy2, 130, 158, effect) //y+1065
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 650 + dy2, new textEffect(220, null))
                        .font(scoreFont).drawText(String.valueOf(musicInfo.getScore()), 160 + dx2, 658 + dy2)
                        .font(infoFont).drawText("%d\n%d\n%.2f%%".formatted(musicInfo.getCombo(), musicInfo.getMiss(), musicInfo.getAcc()), 230 + dx2, 740 + dy2, new textEffect(null, 1))
                        .font(levelFont, Color.WHITE).drawText(String.valueOf(musicInfo.getLevel()), 17 + dx2, 749 + dy2);
            }
        }

        drawer.dispose();


        Graphics2D graphics = drawer.getGraphics();

        drawer.save("PNG", new File(path + "result.png"));

    }


    public void test() throws Exception {
        printAvailableFonts();
    }

    public static ArrayList<BufferedImage> getRankCovers(Token token) {
        ArrayList<RankMusicInfo> allRankList = LvRatioCalculator.getAllRankList(token.getBearerToken(), true);
        List<RankMusicInfo> rank15List = LvRatioCalculator.getSubRank15List(allRankList);
        ArrayList<BufferedImage> covers = new ArrayList<>();
        for(RankMusicInfo musicInfo : rank15List) {
            BufferedImage file = getCover(musicInfo.getId());
            covers.add((file));
        }
        return covers;
    }

    public static BufferedImage getCover(int id) {
        if(Officials.OFFICIAL_ID.contains(id)) {
            File file = new File(officialImgPath + id + ".jpg");
            try {
                if(file.exists()) {
                    return ImageIO.read(file);
                } else {
                    File defaultImg = new File(officialImgPath + "default.png");
                    if(!defaultImg.exists()) {
                        throw new RuntimeException(defaultImg.getAbsolutePath() + "\n默认文件不存在！");
                    }
                    return ImageIO.read(defaultImg);
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            AnyMusic music = new AnyMusic(id);
            try {
                return ImageIO.read(new URL(music.getCoverUrl()));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}