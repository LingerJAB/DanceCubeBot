package com.dancecube.image;

import com.dancecube.api.LvRatioHistory;
import com.dancecube.info.UserInfo;
import com.dancecube.music.AnyMusic;
import com.dancecube.music.Officials;
import com.dancecube.ratio.LvRatioCalculator;
import com.dancecube.ratio.RankMusicInfo;
import com.dancecube.ratio.RecentMusicInfo;
import com.dancecube.ratio.SingleRank;
import com.dancecube.token.Token;
import com.tools.image.AccGrade;
import com.tools.image.ImageDrawer;
import com.tools.image.ImageEffect;
import com.tools.image.TextEffect;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.mirai.config.AbstractConfig.*;

public class UserRatioImage {
    public static String path = configPath + "Images/Material/";
    public static String officialImgPath = configPath + "Images/Cover/OfficialImage/";


    public static final BufferedImage CARD_1; //低级
    public static final BufferedImage CARD_2; //中级
    public static final BufferedImage CARD_3; //高级

    public static final BufferedImage LV_SSS;
    public static final BufferedImage LV_SS;
    public static final BufferedImage LV_S;
    public static final BufferedImage LV_A;
    public static final BufferedImage LV_B;
    public static final BufferedImage LV_C;
    public static final BufferedImage LV_D;


    static {
        try {
            // 素材缓存
            CARD_1 = ImageIO.read(new File(path + "Card1.png"));
            CARD_2 = ImageIO.read(new File(path + "Card2.png"));
            CARD_3 = ImageIO.read(new File(path + "Card3.png"));
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
//        System.out.println("running...");

        UserInfo info;
        ArrayList<LvRatioHistory> ratioList;
//        ReplyItem replyItem;
        if(itIsAReeeeaaaalWindowsMark()) {
            info = UserInfo.get(token);
            ratioList = LvRatioHistory.get(token);
//            replyItem = ReplyItem.get(token);
        } else {
            Future<UserInfo> userInfoFuture = scheduler.async(() -> UserInfo.get(token));
            Future<ArrayList<LvRatioHistory>> ratioFuture = scheduler.async(() -> LvRatioHistory.get(token));
//            Future<ReplyItem> replyItemFuture = scheduler.async(() -> ReplyItem.get(token));
            try {
                info = userInfoFuture.get();
                ratioList = ratioFuture.get();
//                replyItem = replyItemFuture.get();
            } catch(ExecutionException | InterruptedException e) {
                info = UserInfo.get(token);
                ratioList = LvRatioHistory.get(token);
//                replyItem = ReplyItem.get(token);
            }
        }

        ImageDrawer drawer;
        BufferedImage avatar;
        BufferedImage box;
        BufferedImage title;
        BufferedImage backgroundImg;

        try {
            backgroundImg = ImageIO.read(new File(path + "Background1.png"));
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
        int lvRatio = info.getLvRatio();
        String userInfoText = """
                %s

                战队：%s
                排名：%d
                战力：%d""".formatted(info.getUserName(), info.getTeamName(), info.getRankNation(), lvRatio);
        Font infoFont = new Font("得意黑", Font.PLAIN, 45);
        Font idFont = new Font("得意黑", Font.PLAIN, 30);
        drawer.color(Color.BLACK).font(idFont).drawText("ID: " + token.getUserId(), 245, 200)
                .font(infoFont).drawText(userInfoText, 245, 160, new TextEffect(230, 0));


        // 异步获取两个列表
        ArrayList<RankMusicInfo> allRankList;
        ArrayList<RecentMusicInfo> allRecentList;
        if(itIsAReeeeaaaalWindowsMark()) {
            allRankList = LvRatioCalculator.getAllRankList(token.getBearerToken(), true);
            allRecentList = LvRatioCalculator.getAllRecentList(token.getBearerToken(), true);
        } else {
            Future<ArrayList<RankMusicInfo>> rankMusicFuture = scheduler.async(() -> LvRatioCalculator.getAllRankList(token.getBearerToken(), true));
            Future<ArrayList<RecentMusicInfo>> recentMusicFuture = scheduler.async(() -> LvRatioCalculator.getAllRecentList(token.getBearerToken(), true));
            try {
                allRankList = rankMusicFuture.get();
                allRecentList = recentMusicFuture.get();
            } catch(ExecutionException | InterruptedException e) {
                allRankList = LvRatioCalculator.getAllRankList(token.getBearerToken(), true);
                allRecentList = LvRatioCalculator.getAllRecentList(token.getBearerToken(), true);
            }

        }
        List<RankMusicInfo> rank15List = LvRatioCalculator.getSubRank15List(allRankList);
        List<RecentMusicInfo> recent15List = LvRatioCalculator.getSubRecent15List(allRecentList);

        // B15
        int index = 0;
        int dx = 395, dy = 180; //x y延伸长度
        Font titleFont = new Font("Microsoft YaHei UI", Font.BOLD, 32);
        Font scoreFont = new Font("庞门正道标题体", Font.PLAIN, 52);
        Font comboMissAccFont = new Font("庞门正道标题体", Font.PLAIN, 15);
        Font levelFont = new Font("庞门正道标题体", Font.PLAIN, 23);

        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                int dx2 = col * dx;
                int dy2 = row * dy;
                RankMusicInfo musicInfo = rank15List.get(index);
                BufferedImage cover = getCover(musicInfo.getId());
                SingleRank bestInfo = musicInfo.getBestInfo();
                BufferedImage card = getCardImage(bestInfo.getDifficulty());
                BufferedImage grade = getGradeImage(bestInfo.getGrade());
                int fx = switch(bestInfo.getGrade()) {
                    case SSS, C -> 0;
                    case SS -> -17;
                    case S -> -6;
                    default -> 5;// case A B D
                };
                ImageEffect effect = new ImageEffect(35, 35);

                String diff = musicInfo.getBestRatioInt()>lvRatio ? "+" + (musicInfo.getBestRatioInt() - lvRatio) : String.valueOf(musicInfo.getBestRatioInt() - lvRatio);
                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect)
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + fx + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK)
                        .drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect(220, null))
                        .font(scoreFont).drawText(String.valueOf(bestInfo.getScore()), 160 + dx2, 648 + dy2)
                        .font(comboMissAccFont)
                        .drawText("%d\n%d\n%.2f%%".formatted(bestInfo.getCombo(), bestInfo.getMiss(), bestInfo.getAcc()), 230 + dx2, 725 + dy2, new TextEffect(null, 2))
                        .drawText("> %d (%s)".formatted(musicInfo.getBestRatioInt(), diff), 163 + dx2, 703 + dy2)
                        .font(levelFont, Color.WHITE)
                        .drawText(String.valueOf(bestInfo.getLevel()), 17 + dx2, 747 + dy2);
            }
        }

        index = 0;
        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                int dx2 = col * dx;
                int dy2 = row * dy + 1065;
                RecentMusicInfo musicInfo = recent15List.get(index);
                BufferedImage cover = getCover(musicInfo.getId());
                BufferedImage card = getCardImage(musicInfo.getDifficulty());
                BufferedImage grade = getGradeImage(musicInfo.getGrade());
                int fx = switch(musicInfo.getGrade()) {
                    case SSS, C -> 0;
                    case SS -> -17;
                    case S -> -6;
                    default -> 3;// case A B D
                };
                ImageEffect effect = new ImageEffect(35, 35);
                String diff = musicInfo.getBestRatioInt()>lvRatio ? "+" + (musicInfo.getBestRatioInt() - lvRatio) : String.valueOf(musicInfo.getBestRatioInt() - lvRatio);

                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect) //y+1065
                        .drawImage(card, 15 + dx2, 620 + dy2).drawImage(grade, 285 + fx + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect(220, null)).font(scoreFont)
                        .drawText(String.valueOf(musicInfo.getScore()), 160 + dx2, 648 + dy2)
                        .font(comboMissAccFont)
                        .drawText("%d\n%d\n%.2f%%"
                                .formatted(musicInfo.getCombo(), musicInfo.getMiss(), musicInfo.getAcc()), 230 + dx2, 725 + dy2, new TextEffect(null, 2))
                        .drawText("> %d (%s)".formatted(musicInfo.getBestRatioInt(), diff), 163 + dx2, 703 + dy2)
                        .font(levelFont, Color.WHITE)
                        .drawText(String.valueOf(musicInfo.getLevel()), 17 + dx2, 747 + dy2);
            }
        }

        LvRatioHistory lvRatioHistory = ratioList.get(ratioList.size() - (ratioList.size()>1 ? 2 : 1));
        Calendar calendar = lvRatioHistory.getCalendar();
        float avg1 = LvRatioCalculator.average(rank15List);
        float avg2 = LvRatioCalculator.average(recent15List);
        float allAvg = (avg1 + avg2) / 2;
        int randomIndex = new Random().nextInt(5); //0 ~ 4
        String extraInfoText = """
                上次战力：%d   (%d月%d日)
                B-15 战力：%.4f
                R-15 战力：%.4f
                平均战力：%.5f
                """.formatted(lvRatioHistory.getRatio(),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                avg1, avg2, allAvg) + getRatioComment(lvRatio);
        // 向上取整到百位 ((info.getLvRatio() / 100) + 1) * 100)
        drawer.font(infoFont).color(Color.BLACK).drawText(extraInfoText, 720, 160, new TextEffect(null, -6));
        drawer.dispose();
        return drawer.getImageStream("PNG");
    }

    private static BufferedImage getGradeImage(AccGrade grade) {
        return switch(grade) {
            case SSS -> LV_SSS;
            case SS -> LV_SS;
            case S -> LV_S;
            case A -> LV_A;
            case B -> LV_B;
            case C -> LV_C;
            default -> LV_D;
        };
    }

    private static BufferedImage getCardImage(int difficulty) {
        return switch(difficulty) {
            case 0, -1 -> CARD_1; //-1为秀谱
            case 1 -> CARD_2;
            case 2 -> CARD_3;
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };
    }

    @Test
    public void test() {
//        Token token = new Token(939088, "PViJl0yNANEslG5QPYZjiI7ZcAg8F5L7o0mXH2TJpkEsoZQcAh3DrUN3CPgvx-KFNoMFMQDMtQbZs9opoXnL6_VaOSDIf2VvlJBpBHY7XF-YSAnIdkRaLbyiUF6J6WqvKFiP6WcSEcdXde-ifSp2GlifVvGE4NiGxTPmY2NsV61T9LCd6CzhQyz97408jGYUsTq9I-d8ewZ65qE3TayXn18SGseZG924fOr-tOYWiEFESXnLNzMbrsRIiSSMtuc4ell1_4479J7WFGvZkvmgGSa3qis4WmvFUJkuTcAH5OuqjCeroiLwz1ksCGriCt6b7CGVFAHTkoEI0XMBdwiw-t_LczRuLZeS9_JCAH-DZ3bdCcL_i26a9jYyAqqpggQchgKMUYyy7j_jR7QhcEoCLodAgAtU4PN4WoZRHp7DhAhxQMY-9ua66ZJBhu2b6tEdUocjN4FUMRv3Qv_Fg53WBKB8f36fqFxZ5HTdpaiPF1ig5ipI0hM3rRYEWWvxg4j_IjzzMJDGQHN5KhSXEvjk7TSUvCaOuM9DR8fdbaiUTz2JC0QCw9SG4l_mlVdkf7zmj3ZfhiteGZ1-n3VXl9y_KyKKEuuL-_0YGn6qDvS9ng5fUdwki3WUlZ34TJrYaNmImGQmnEjQTpvFGxKgdpMOR-P4vAm0W8HVS9r15Kht2wAM5GHWoXmlvjva-oLt6LbJICd_u6svAFn92VwHlx6179LSMr6iZppK4GEC-XS9kJTwCDMZ_XLXoqczOBRngz9M69NMRVpmRJ8c0Bn55lbiA6n8sAcbHAYtGCh3P5gnPO_1PKDn0UQ1FovmA7uS1Xvfc6fk0Ugi29yCC_rhv0R4MZbxodOyCha4rMgDd8XlsKfQJNpCMNWogAD7vhWYIpACOXjRjCG4Q5lweR3XxbSRPA");
//        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png";
//        ImageDrawer.write(generate(token), path);

        int ratio = 1808;
        System.out.println("---------");
        String comment = getRatioComment(ratio);
        String str = """
                上次战力：%d   (%d月%d日)
                B-15 战力：%.4f
                R-15 战力：%.4f
                平均战力：%.5f
                """ + getRatioComment(ratio);
        System.out.println(str);
        System.out.println("---------");
    }

    //todo 遍历+异步
    public static BufferedImage getCover(int id) {
        File file = new File(officialImgPath + id + ".jpg");
        // 先从官谱音乐取封面
        if(Officials.OFFICIAL_ID.contains(id)) {
            try {
                if(file.exists()) {
                    return ImageIO.read(file);
                }
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        //非官谱或未缓存则从DC服务器获取
        AnyMusic music = new AnyMusic(id);
        String coverUrl = music.getCoverUrl();
        try {
            if(coverUrl==null | "".equals(coverUrl)) {
                File defaultImg = new File(officialImgPath + "default.png");
                if(!defaultImg.exists()) {
                    throw new RuntimeException(defaultImg.getAbsolutePath() + "\n默认文件不存在！");
                }
                return ImageIO.read(defaultImg);
            } else {
                return ImageIO.read(new URL(coverUrl));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    //常量放在这里我有病（
    public static final String[] RATIO_COMMENTS = {
            //   连1145的战力都没有
            "  \"你已初步了解这款游戏了\n  继续练习吧~\"", //..1000
            "  \"你已经适应10级的歌曲了\n  继续练习吧~\"", //1000..1300
            "  \"你正在对线14级的歌了\n  继续加油~\"", //1300..1500
            "  \"你即将迈入大佬的行列\n  加油加油！\"",//1500..1800
            "  \"恭喜突破1800守门员\n  正式成为大佬啦！\"",//1800..1900
            "  \"你即将成神\n  请继续和1819对线\"",//1900..2000
            "  \"你已步入神的行列\n  快快杀19吧~\"",//2000..2080
            //卧槽，外星人？！
            "  \"你已经成为外星人\n  正在薄纱一切歌曲\""//2080..2100
    };

    public static String getRatioComment(int ratio) {
        String comment;
        if(ratio<1000) comment = RATIO_COMMENTS[0];
        else if(ratio<1300) comment = RATIO_COMMENTS[1];
        else if(ratio<1500) comment = RATIO_COMMENTS[2];
        else if(ratio<1800) comment = RATIO_COMMENTS[3];
        else if(ratio<1900) comment = RATIO_COMMENTS[4];
        else if(ratio<2000) comment = RATIO_COMMENTS[5];
        else if(ratio<2080) comment = RATIO_COMMENTS[6];
        else if(ratio<2100) comment = RATIO_COMMENTS[7];
        else comment = "";
        return comment;
    }
}

