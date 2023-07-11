package com.dancecube.ratio.image;

import com.dancecube.info.UserInfo;
import com.dancecube.music.AnyMusic;
import com.dancecube.music.Officials;
import com.dancecube.ratio.rankingMusic.LvRatioCalculator;
import com.dancecube.ratio.rankingMusic.RankMusicInfo;
import com.dancecube.ratio.rankingMusic.RecentMusicInfo;
import com.dancecube.ratio.rankingMusic.SingleRank;
import com.dancecube.token.Token;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserRatioImage {
    public static String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\src\\Materials\\";
    public static String officialImgPath = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\src\\all\\officialImg\\";
    public static File defaultImg = new File(officialImgPath + "default.png");

    public static InputStream generate(Token token, int id) {
        UserInfo info = UserInfo.get(token, id);
        System.out.println(new File(path + "Card1.png").exists());

        ImageDrawer drawer;
        BufferedImage card1;
        BufferedImage card2;
        BufferedImage card3;
        BufferedImage lvSSS;
        BufferedImage lvSS;
        BufferedImage lvS;
        BufferedImage lvA;
        BufferedImage lvB;
        BufferedImage lvC;
        BufferedImage lvD;
        try {
            card1 = ImageIO.read(new File(path + "Card1.png"));
            card2 = ImageIO.read(new File(path + "Card2.png"));
            card3 = ImageIO.read(new File(path + "Card3.png"));
            lvSSS = ImageIO.read(new File(path + "SSS.png"));
            lvSS = ImageIO.read(new File(path + "SS.png"));
            lvS = ImageIO.read(new File(path + "S.png"));
            lvA = ImageIO.read(new File(path + "A.png"));
            lvB = ImageIO.read(new File(path + "B.png"));
            lvC = ImageIO.read(new File(path + "C.png"));
            lvD = ImageIO.read(new File(path + "D.png"));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage avatar;
        BufferedImage box;
        BufferedImage title;
        BufferedImage backgroundImg;

        try {
            backgroundImg = ImageIO.read(new File(path + "Main.png"));
            drawer = new ImageDrawer(backgroundImg);
            drawer.antiAliasing(); // 抗锯齿

            // 个人信息 头像/头衔
            avatar = ImageIO.read(new URL(info.getHeadimgURL()));
            drawer.drawImage(avatar, 34, 180, 174, 174);

            if(!info.getHeadimgBoxPath().equals("")) {
                box = ImageIO.read(new URL(info.getHeadimgBoxPath()));
                drawer.drawImage(box, -24, 122, 290, 290);
            }
            if(!info.getTitleUrl().equals("")) {
                title = ImageIO.read(new URL(info.getTitleUrl()));
                drawer.drawImage(title, 28, 373, 186, 79);
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        // TODO 段位
        String text = """
                %s

                战队：%s
                排名：%d
                战力：%d""".formatted(info.getUserName(), info.getTeamName(), info.getRankNation(), info.getLvRatio());
        Font font = new Font("得意黑", Font.PLAIN, 45);
        drawer.color(Color.BLACK)
                .font(font)
                .drawText(text, 245, 167, new TextEffect(null, 0));

        // B15

        ArrayList<RankMusicInfo> allRankList = LvRatioCalculator.getAllRankList(token.getBearerToken(), true);
        List<RankMusicInfo> rank15List = LvRatioCalculator.getSubRank15List(allRankList);
        int index = 0;
        int dx = 395, dy = 180; //x y延伸长度
        Font titleFont = new Font("Microsoft YaHei UI", Font.BOLD, 32);
        Font scoreFont = new Font("庞门正道标题体", Font.PLAIN, 52);
        Font infoFont = new Font("庞门正道标题体", Font.PLAIN, 15);
        Font levelFont = new Font("庞门正道标题体", Font.PLAIN, 23);
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
                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect)
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect(220, null))
                        .font(scoreFont).drawText(String.valueOf(bestInfo.getScore()), 160 + dx2, 658 + dy2)
                        .font(infoFont).drawText("%d\n%d\n%.2f%%".formatted(bestInfo.getCombo(), bestInfo.getMiss(), bestInfo.getAcc()), 230 + dx2, 725 + dy2, new TextEffect(null, 3))
                        .font(levelFont, Color.WHITE).drawText(String.valueOf(bestInfo.getLevel()), 17 + dx2, 749 + dy2);
            }
        }

        ArrayList<RecentMusicInfo> allRecentList = LvRatioCalculator.getAllRecentList(token.getBearerToken(), true);
        List<RecentMusicInfo> recent15List = LvRatioCalculator.getSubRecent15List(allRecentList);
        index = 0;
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
                    //Todo Unexpected value: -1
                    case -1 -> card3;
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
                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect) //y+1065
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect(220, null))
                        .font(scoreFont).drawText(String.valueOf(musicInfo.getScore()), 160 + dx2, 658 + dy2)
                        .font(infoFont).drawText("%d\n%d\n%.2f%%".formatted(musicInfo.getCombo(), musicInfo.getMiss(), musicInfo.getAcc()), 230 + dx2, 725 + dy2, new TextEffect(null, 3))
                        .font(levelFont, Color.WHITE).drawText(String.valueOf(musicInfo.getLevel()), 17 + dx2, 749 + dy2);
            }
        }

        drawer.dispose();
//        drawer.save("PNG", new File(path + "result.png"));

        return drawer.getImageStream("PNG");
    }

    @Test
    public void test() {
        Token token = new Token(660997, "89ZHSafR_BM199Q5ox3jqUeQv4YPeV1_A8aeSl-D_GqJ0V0uqHE0AxFhqvD46nyOLoCVHrPbVwZkB7mz814DJkvUIsgZrTb-BZrfEuBLd_iz2tDtSr_i71La1U6MKF-U7Ccv4d3ocjwg-Pr07R4lOAKJlT7pqMDTcTaFnYLe6xnmHyQN6kTNzNhlKL3w6SSZub2XHxY8GgxTiXRtDaGXSrk31-ZOBAgPSW3VZ4fEs6NdsMfLo3OwQEIH1cynDBRGUnm1QashCBCSq6glewdiB05Axt3vXqNzsy2TBoqDZIpZzlMCasNM963v6I8wpwBv9aMBp_ic_YdgjWwefuF57pHgPRC0dfC6EJ-NbRifAFCNPVVAFiN32Hx5e5AWKWDR");
        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png";
        try {
            ImageIO.write(ImageIO.read(generate(token, 660997)), "PNG", new FileOutputStream(path));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
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