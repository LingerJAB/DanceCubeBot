package com.dancecube.image;

import com.dancecube.api.LvRatioHistory;
import com.dancecube.info.UserInfo;
import com.dancecube.music.CoverUtil;
import com.dancecube.ratio.AccGrade;
import com.dancecube.ratio.RankMusicInfo;
import com.dancecube.ratio.RatioCalculator;
import com.dancecube.ratio.RecentMusicInfo;
import com.dancecube.token.Token;
import com.tools.image.ImageDrawer;
import com.tools.image.ImageEffect;
import com.tools.image.TextEffect;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

import static com.mirai.config.AbstractConfig.configPath;
import static com.mirai.config.AbstractConfig.itIsAReeeeaaaalWindowsMark;

public class UserRatioImage {
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
    public static String path = configPath + "Images/UserRatioImage/";


    static {
        try {
            // 素材缓存到内存
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
        // 个人信息
        UserInfo info;
        List<LvRatioHistory> ratioList;
//        if(!itIsAReeeeaaaalWindowsMark()) {
        info = UserInfo.get(token);
        ratioList = LvRatioHistory.get(token);
//        } else {
//            CompletableFuture<UserInfo> userInfoFuture = CompletableFuture.supplyAsync(() -> UserInfo.get(token));
//            CompletableFuture<ArrayList<LvRatioHistory>> ratioFuture = CompletableFuture.supplyAsync(() -> LvRatioHistory.get(token));
//            try {
//                info = userInfoFuture.get();
//                ratioList = ratioFuture.get();
//            } catch(ExecutionException | InterruptedException e) {
//                info = UserInfo.get(token);
//                ratioList = LvRatioHistory.get(token);
//            }
//        }

        ImageDrawer drawer;
        try {
            // 为什么这里放个finalInfo？
            final UserInfo finalInfo = info;
            if(info==null) {
                System.err.println("# 战力分析时个人信息获取失败");
            }

            // 获取背景图片
            CompletableFuture<BufferedImage> backgroundImgFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    // TODO 背景图片写到常量区（内存）
                    return ImageIO.read(new File(path + "Background1.png"));
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // 个人信息 头像/头像框/头衔  异步获取
            CompletableFuture<BufferedImage> avatarFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    if(finalInfo.getHeadimgURL().isEmpty()) return null;
                    return ImageIO.read(new URL(finalInfo.getHeadimgURL()));
                } catch(IOException e) {
                    return null;
                }
            });

            CompletableFuture<BufferedImage> boxFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    if(finalInfo.getHeadimgBoxPath().isEmpty()) return null;
                    return ImageIO.read(new URL(finalInfo.getHeadimgBoxPath()));
                } catch(IOException e) {
                    return null;
                }
            });

            CompletableFuture<BufferedImage> titleFuture = CompletableFuture.supplyAsync(() -> {
                if(finalInfo.getTitleUrl().isEmpty()) return null;
                try {
                    return ImageIO.read(new URL(finalInfo.getTitleUrl()));
                } catch(IOException e) {
                    return null;
                }
            });

            //异步阻塞完绘制战力图
            drawer = new ImageDrawer(backgroundImgFuture.get());
            drawer.setAntiAliasing(); // 抗锯齿

            CompletableFuture.allOf(avatarFuture, boxFuture, titleFuture).join();
            if(avatarFuture.get()!=null) drawer.drawImage(avatarFuture.get(), 34, 180, 174, 174);
            if(boxFuture.get()!=null) drawer.drawImage(boxFuture.get(), -24, 122, 290, 290);
            if(titleFuture.get()!=null) drawer.drawImage(titleFuture.get(), 28, 373, 186, 79);
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        int lvRatio = info.getLvRatio();
        String userInfoText = """
                %s
                
                战队：%s
                排名：%d
                战力：%d""".formatted(info.getUserName(), info.getTeamName(), info.getRankNation(), lvRatio);
        Font infoFont = new Font("得意黑", Font.PLAIN, 45);
        Font idFont = new Font("得意黑", Font.PLAIN, 30);
        drawer.color(Color.BLACK).font(idFont).drawText("ID: " + token.getUserId(), 245, 200)
                .font(infoFont).drawText(userInfoText, 245, 160, new TextEffect().setMaxWidth(230).setSpaceHeight(0));


        // 异步获取两个列表
        List<RankMusicInfo> allRankList;
        List<RecentMusicInfo> allRecentList;
        if(itIsAReeeeaaaalWindowsMark()) { // Windows下执行异步
            CompletableFuture<List<RankMusicInfo>> rankMusicFuture = CompletableFuture.supplyAsync(() -> RatioCalculator.getAllRankList(token.getBearerToken()));
            CompletableFuture<List<RecentMusicInfo>> recentMusicFuture = CompletableFuture.supplyAsync(() -> RatioCalculator.getAllRecentList(token.getBearerToken()));
            try {
                allRankList = rankMusicFuture.get();
                allRecentList = recentMusicFuture.get();
            } catch(ExecutionException | InterruptedException e) {
                allRankList = RatioCalculator.getAllRankList(token.getBearerToken());
                allRecentList = RatioCalculator.getAllRecentList(token.getBearerToken());
            }
        } else { //
            allRankList = RatioCalculator.getAllRankList(token.getBearerToken());
            allRecentList = RatioCalculator.getAllRecentList(token.getBearerToken());
        }
        List<RankMusicInfo> rank15List = RatioCalculator.getSubRank15List(allRankList, true);
        List<RecentMusicInfo> recent15List = RatioCalculator.getSubRecent15List(allRecentList, false);

        // TODO 这里要改
        boolean stopDownloading = false;
        if(stopDownloading) {
            //准备自制谱封面id下载列表
            HashSet<Integer> waitingCoversSet = new HashSet<>();
            for(RankMusicInfo value : rank15List) {
                if(CoverUtil.isCoverAbsent(value.getId())) waitingCoversSet.add(value.getId());
            }
            for(RecentMusicInfo value : recent15List) {
                if(CoverUtil.isCoverAbsent(value.getId())) waitingCoversSet.add(value.getId());
            }

            //多线程下载不存在的封面
            ExecutorService threadPool = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(waitingCoversSet.size());
            waitingCoversSet.forEach(id -> threadPool.submit(() -> {
                CoverUtil.downloadCover(id);
                latch.countDown();
            }));
            threadPool.shutdown();
            try {
                latch.await();
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // B15绘制
        int index = 0;
        int dx = 395, dy = 180; //x y延伸长度
        Font titleFont = new Font("Microsoft YaHei UI", Font.BOLD, 32);
        Font scoreFont = new Font("庞门正道标题体", Font.PLAIN, 52);
        Font comboMissAccFont = new Font("庞门正道标题体", Font.PLAIN, 15);
        Font levelFont = new Font("庞门正道标题体", Font.PLAIN, 23);

        out:
        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                if(index>=rank15List.size()) break out;

                int dx2 = col * dx;
                int dy2 = row * dy;
                RankMusicInfo musicInfo = rank15List.get(index);
                BufferedImage cover = CoverUtil.getCoverOrDefault(musicInfo.getId());
                BufferedImage card = getCardImage(musicInfo.getDifficulty());
                BufferedImage grade = getGradeImage(musicInfo.getAccGrade());
                int fix = switch(musicInfo.getAccGrade()) {
                    case SSS, C -> 0;
                    case SS -> -17;
                    case S -> -6;
                    default -> 5;// case A B D
                };
                ImageEffect effect = new ImageEffect().setArc(35);

                // 战力 >xxxx(+/- xx)
                String diff = musicInfo.getRatioInt() > lvRatio
                        ? "+" + (musicInfo.getRatioInt() - lvRatio)
                        : String.valueOf(musicInfo.getRatioInt() - lvRatio);
                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect)
                        .drawImage(card, 15 + dx2, 620 + dy2)
                        .drawImage(grade, 285 + fix + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK)
                        .drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect().setMaxWidth(220))
                        .font(scoreFont).drawText(String.valueOf(musicInfo.getScore()), 160 + dx2, 646 + dy2)
                        .font(comboMissAccFont)
                        .drawText("%d\n%d\n%.2f%%".formatted(musicInfo.getCombo(), musicInfo.getMiss(), musicInfo.getAccuracy()), 230 + dx2, 725 + dy2,
                                new TextEffect().setSpaceHeight(1))
                        .drawText("> %d (%s)".formatted(musicInfo.getRatioInt(), diff), 163 + dx2, 702 + dy2)
                        .font(levelFont, Color.WHITE)
                        .drawText(String.valueOf(musicInfo.getLevel()), 17 + dx2, 747 + dy2);
            }
        }

        // R15绘制
        index = 0;
        a:
        for(int row = 0; row<5; row++) { //列
            for(int col = 0; col<3; col++, index++) { //行
                int dx2 = col * dx;
                int dy2 = row * dy + 1065;
                RecentMusicInfo musicInfo;

                if(index>=recent15List.size()) break a;
                musicInfo = recent15List.get(index);
                BufferedImage cover = CoverUtil.getCoverOrDefault(musicInfo.getId());
                BufferedImage card = getCardImage(musicInfo.getDifficulty());
                BufferedImage grade = getGradeImage(musicInfo.getAccGrade());
                int fix = switch(musicInfo.getAccGrade()) {
                    case SSS, C -> 0;
                    case SS -> -17;
                    case S -> -6;
                    default -> 5;// case A B D
                };
                ImageEffect effect = new ImageEffect().setArc(35);
                String diff = musicInfo.getRatioInt()>lvRatio ?
                        "+" + (musicInfo.getRatioInt() - lvRatio) : String.valueOf(musicInfo.getRatioInt() - lvRatio);

                drawer.drawImage(cover, 16 + dx2, 621 + dy2, 130, 158, effect) //y+1065
                        .drawImage(card, 15 + dx2, 620 + dy2).drawImage(grade, 285 + fix + dx2, 715 + dy2)
                        .font(titleFont, Color.BLACK).drawText(musicInfo.getName(), 160 + dx2, 624 + dy2, new TextEffect().setMaxWidth(220)).font(scoreFont)
                        .drawText(String.valueOf(musicInfo.getScore()), 160 + dx2, 646 + dy2)
                        .font(comboMissAccFont)
                        .drawText("%d\n%d\n%.2f%%".formatted(musicInfo.getCombo(), musicInfo.getMiss(), musicInfo.getAccuracy()),
                                230 + dx2, 725 + dy2,
                                new TextEffect().setSpaceHeight(1))
                        .drawText("> %d (%s)".formatted(musicInfo.getRatioInt(), diff), 163 + dx2, 702 + dy2)
                        .font(levelFont, Color.WHITE)
                        .drawText(String.valueOf(musicInfo.getLevel()), 17 + dx2, 747 + dy2);
            }
        }

        // 战力概况
        LvRatioHistory lvRatioHistory;
        if(!ratioList.isEmpty()) {
            lvRatioHistory = ratioList.get(ratioList.size() - (ratioList.size()>1 ? 2 : 1));
        } else {
            lvRatioHistory = new LvRatioHistory(DateFormat.getDateInstance().getCalendar(), info.getLvRatio());
        }
        float avg1 = RatioCalculator.average(rank15List);
        float avg2 = RatioCalculator.average(recent15List);
        float allAvg = (avg1 + avg2) / 2;
        Calendar calendar = lvRatioHistory.getCalendar();
        String extraInfoText = """
                上次战力：%d   (%d月%d日)
                B-15 战力：%.4f
                R-15 战力：%.4f
                平均战力：%.5f
                """.formatted(lvRatioHistory.getRatio(),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                avg1, avg2, allAvg) + getRatioComment(lvRatio);
        drawer.font(infoFont).color(Color.BLACK).drawText(extraInfoText, 720, 160, new TextEffect().setSpaceHeight(-6));
        drawer.dispose();
        return drawer.getImageStream("png");
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
            default -> CARD_1;
        };
    }

    @Test
    public void test() throws IOException {
        System.out.println("Running...");
        Token token = new Token(939088,
                "Tbp4C0QAJeIsqNcJx7psKoYAxFNsD0qH68qutYCZod8ybPiRoEJ05RZhHzy4LPQDtw3tJvKYqSkCpnEd-qrg-c7MMY7DwQecXF3-uuU-6qDd7zIQ7IpfTHbcVHvN_st9XnVCyt9op0b6CYFY3nTvNH1F4aidP5M-P-MXes3-TIH80YHN8zHgua_XjgFWfi0loubYS0KW9APsB0POsoaBmeJz-85ZxnqlOdzUkW7cb9vGPzgQvP7adZPa6igEfynpx1YXTthssnhGyjKdMSQnKkR2Zhmx4zdbwo9N1eTDoAv0ZuNZ9-29gSirqGHbwRS-GXPnXG4mGLvdMuRWY1OKuLk1HWvV-AsceOuMvZX9vin0BcxGDKmK4axbU8kRQkx-");
        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.jpg";

        InputStream image = generate(token);
        Thumbnails.of(image)
                .scale(1)
                .outputFormat("jpg")
                .toFile(path);
//        ImageDrawer.write(ImageDrawer.convertPngToJpg(ImageDrawer.read(image),0.5f), path);
//        ImageDrawer.write(ImageDrawer.read(image), path);
        System.out.println("Done!");
    }

    public static float deltaSeconds(long mills) {
        return (float) (System.currentTimeMillis() - mills) / 1000;
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

