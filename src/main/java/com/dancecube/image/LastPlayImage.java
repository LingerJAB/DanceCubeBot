package com.dancecube.image;

import com.dancecube.info.UserInfo;
import com.dancecube.music.CoverUtil;
import com.dancecube.ratio.AccGrade;
import com.dancecube.ratio.RatioCalculator;
import com.dancecube.ratio.RecentMusicInfo;
import com.dancecube.token.Token;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.mirai.config.AbstractConfig.configPath;

public class LastPlayImage {
    public static final String path = configPath + "Images/LastPlayImage/";
    public static final String savePath = configPath + "Images/result.png";

    public static final Image LV_A;
    public static final Image LV_B;
    public static final Image LV_C;
    public static final Image LV_D;
    public static final Image LV_S;
    public static final Image LV_S_AP;
    public static final Image LV_FC;
    public static final Image LV_AP;
    public static final Image NEW_RECORD;
    public static final Image BACKGROUND;
    public static final Image AVATAR_BOX;
    public static final Image COVER_BOX;
    public static final Image SONG_BOX;

    public static final Typeface scoreFace;
    public static final Typeface titleFace;

    static {
        try {
            LV_A = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "A.png")));
            LV_B = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "B.png")));
            LV_C = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "C.png")));
            LV_D = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "D.png")));
            LV_S = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "S.png")));
            LV_S_AP = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "S_AP.png")));
            LV_FC = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "FC.png")));
            LV_AP = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "AP.png")));

            NEW_RECORD = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "newRec.png")));
            BACKGROUND = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "background.png")));
            AVATAR_BOX = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "avatarBox.png")));
            COVER_BOX = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "coverBox.png")));
            SONG_BOX = Image.makeDeferredFromEncodedBytes(Files.readAllBytes(Path.of(path + "songBanner.png")));

            scoreFace = Typeface.makeFromFile(configPath + "Fonts/PangMenZhengDaoBiaoTiTi.ttf");
            titleFace = Typeface.makeFromFile(configPath + "Fonts/SourceHanSans-Bold.otf");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成游玩成绩图片
     *
     * @param token 用户Token
     * @param info  游玩记录
     * @return 成绩图片
     */
    public static InputStream generate(Token token, RecentMusicInfo info) {
        UserInfo user = UserInfo.get(token);

        Image backgroundImage = BACKGROUND;

        // 创建以图片为基础的 Surface
        Surface surface = Surface.makeRaster(ImageInfo.makeN32(
                backgroundImage.getWidth(),
                backgroundImage.getHeight(),
                ColorAlphaType.UNPREMUL
        ));
        Canvas canvas = surface.getCanvas();
        canvas.drawImage(backgroundImage, 0, 0);
        Font scoreFont = new Font(scoreFace, 95);
        Font titleFont = new Font(titleFace, 50);
        Paint plainPaint = new Paint().setAntiAlias(true); // 公共，节约内存（我讨厌new Paint()

        // 绘制封面，曲名，单曲战力
        Rect coverRect = Rect.makeXYWH(41, 202, 127, 127);
        Rect coverboxRect = Rect.makeXYWH(30, 191, 149, 149);
        byte[] coverBytes = CoverUtil.getCoverBytesOrDefault(info.getId());
        canvas.drawImageRect(Image.makeDeferredFromEncodedBytes(coverBytes), coverRect);
        canvas.drawImageRect(COVER_BOX, coverboxRect);

        canvas.drawString(info.getName(), 175, 247, titleFont, plainPaint.setColor(0xFFFFFFFF));
        canvas.drawString("Rating: " + info.getRatioInt(), 175 + 10, 300, titleFont.setSize(30), plainPaint.setColor(0xFFFFFFFF));

        // 绘制头像昵称，战队，积分，战力，日期
        Rect avatarRect = Rect.makeXYWH(612 + 3, 880 + 3, 120, 120);
        URL avatarUrl = null;
        try {
            avatarUrl = new URL(user.getHeadimgURL());
            try(InputStream is = avatarUrl.openStream()) {
                byte[] avatarBytes = is.readAllBytes();
                canvas.drawImageRect(Image.makeDeferredFromEncodedBytes(avatarBytes), avatarRect);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Rect boxRect = Rect.makeXYWH(612, 880, 128, 128);
        canvas.drawImageRect(AVATAR_BOX, boxRect);
        titleFont.setSize(25);
        canvas.drawString(user.getUserName(), 857, 903, titleFont, plainPaint.setColor(0xFFDBDC64));
        canvas.drawString(user.getTeamName(), 857, 903 + 40, titleFont, plainPaint.setColor(0xFF81C8DA));
        canvas.drawString(String.valueOf(user.getMusicScore()), 857 + 20, 903 + 80, titleFont, plainPaint.setColor(0xFFD5BC53));
        canvas.drawString(String.valueOf(user.getLvRatio()), 857 + 20, 903 + 120, titleFont, plainPaint.setColor(0xFFE93B63));
        scoreFont.setSize(26);
        drawTextGlow(canvas, reformatDate(info.getRecordTime()), 610, 1055, scoreFont, 0xFF8C57F1, 3);
        canvas.drawString(reformatDate(info.getRecordTime()), 610, 1055, scoreFont.setSize(26), plainPaint.setColor(0xFFFFFFFF).setAntiAlias(true));


        // 绘制难度等级
        drawLevel(canvas, info.getLevelType(), info.getLevel(), 445, 304, scoreFont);

        // 绘制精确度
        drawAccuracy(canvas, info.getAccuracy(), 160, 463, 0xFFFAE425, scoreFont);

        // 绘制评级
        drawAccGrade(canvas, info.getAccGrade(), 15, 477);

        // 绘制 FC/AP
        if(info.isFullCombo()) {
            canvas.drawImage(LV_FC, 700 + 5, 727);
            if(!info.isAllPerfect()) {
                canvas.drawImage(LV_AP, 840 + 9, 727);
            }
        }


        // Todo Font放到内存
        // 绘制判断结果：MaxCombo Perfect Great Good Miss Score
        scoreFont.setSkewX(-0.1f).setSize(40);
        drawJudgments(canvas, spacedIntFrom(info.getCombo()), 285, 773, 0xFFFAE425, scoreFont);
        drawJudgments(canvas, spacedIntFrom(info.getPerfect()), 285, 773 + 45, 0xFFEE58FB, scoreFont);
        drawJudgments(canvas, spacedIntFrom(info.getGreat()), 285, 773 + 45 * 2, 0xFF82FA2D, scoreFont);
        drawJudgments(canvas, spacedIntFrom(info.getGood()), 285, 773 + 45 * 3, 0xFF1DC9FA, scoreFont);
        drawJudgments(canvas, spacedIntFrom(info.getMiss()), 285, 773 + 45 * 4, 0xFFF0442D, scoreFont);
        scoreFont.setSkewX(0f).setSize(45);
        drawJudgments(canvas, String.valueOf(info.getScore()), 280, 998 + 18, 0xFF7DD6E6, scoreFont);

        Image resultImage = surface.makeImageSnapshot();
        surface.close();
        return new ByteArrayInputStream(EncoderPNG.encode(resultImage).getBytes());
    }

    public static void main(String[] args) throws IOException {
        Token token = new Token(939088, "xvp1mVzCtOYJ4dQ-l9ujOWDNwU4pFkoXsnrw3gIZwukJNo61wDNlhadDTBcLm5wYWezhVnm_zAGXabtplhGATWa2-pDdiSQ_-HLiozBnLX81drC3vkIEisTGfH2jbh2V7h2icD2hOjGC0pSEPYWA4Miv_l59_k3J8DUdV8n9PE5kI6A5_dUQ2rXXL7PWHdJPlYQGcFKUz9Q56ctai5u761p1gs6s4D3pVllbNJiG3_OhKxGB7M9GiDVcPBhCzd88ZnMddYVmgTtXW3vhrmME7mbtfM0lmP0WVQRmA0cdZwIWXd1JeyDH0d186syuuch2qIM3TTdz1FgBXbCpsU_ZpSUFdWapFyQNQXrgb5kUAEv3o2MHGQ2hzxAXZA9RdnNqb");

        Path path = new File(savePath).toPath();
        List<RecentMusicInfo> allRecentList = RatioCalculator.getAllRecentList(token.getBearerToken());

        byte[] bytes = generate(token, allRecentList.get(9)).readAllBytes();
        Files.write(path, bytes, StandardOpenOption.WRITE);
        System.out.println("Done!");
    }
    // Todo  绘制新记录

    private static void drawNewRec(Canvas canvas, RecentMusicInfo info, float x, float y) {

    }

    // 我讨厌封装
    private static String spacedIntFrom(int i) {
        if(0 <= i & i <= 9) return String.valueOf(i);
        return String.valueOf(i).replaceAll("", " ").trim();
    }

    private static String reformatDate(String ori) {
        return ori.replace('-', '.').substring(0, 16);
    }

    private static void drawLevel(Canvas canvas, int levelType, int level, float x, float y, Font font) {
        font.setSize(40);
        int textColor = switch(levelType) {
            case 101 -> 0xFF19BCFD;
            case 102 -> 0xFF5EC31E;
            case 103 -> 0xFFB988FA;
            case 104 -> 0xFFFD9EA8;
            case 105 -> 0xFFFDC067;
            default -> 0x00;
        };
        String text = switch(levelType) {
            case 101 -> "基础";
            case 102 -> "进阶";
            case 103 -> "专家";
            case 104 -> "大师";
            case 105 -> "传奇";
            default -> "";
        };
        drawTextStroke(canvas, text, x, y, font, 0xFFFFFFFF, textColor, 8);
        drawTextGlow(canvas, text, x, y, font, 0x77000000, 1f);

        font.setSize(20);
        drawTextStroke(canvas, "LV.", x + 86, y, font, 0xFFFFFFFF, 0xFF473581, 8);
        font.setSize(50);
        drawTextStroke(canvas, String.valueOf(level), x + 121, y, font, 0xFFFFFFFF, 0xFF473581, 8);
    }

    private static void drawJudgments(Canvas canvas, String score, float x, float y, int color, Font font) {
        drawTextGlow(canvas, score, x, y, font, color, 8f);
        drawTextGradient(canvas, score, x, y, font, color, 0xFFFFFFFF);
    }

    private static void drawAccuracy(Canvas canvas, float accuracy, float x, float y, int color, Font font) {
        font.setSize(95);
        if(accuracy == 100f) x -= 10;
        String text = "%.2f".formatted(accuracy);
        drawTextGlow(canvas, text, x, y, font, color, 5f);
        drawTextGradient(canvas, text, x, y, font, color, 0xFFFDF5AB);

        float width = font.measureTextWidth(text);
        font.setSize(72f);
        drawTextGlow(canvas, "%", x + width + 6, y, font, color, 5f);
        drawTextGradient(canvas, "%", x + width + 6, y, font, color, 0xFFFDF5AB);
    }

    private static void drawTextStroke(Canvas canvas, String text, float x, float y, Font font, int color, int strokeColor, int strokeWidth) {
        Paint strokePaint = new Paint().setColor(strokeColor);
        strokePaint.setStroke(true).setStrokeJoin(PaintStrokeJoin.ROUND); // 圆角描边
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setAntiAlias(true);

        canvas.drawString(text, x, y, font, strokePaint);
        Paint fillPaint = new Paint().setColor(color);
        fillPaint.setAntiAlias(true);

        canvas.drawString(text, x, y, font, fillPaint);
    }

    private static void drawTextGlow(Canvas canvas, String text, float x, float y, Font font, int glowColor, float blurRadius) {
        Paint glowPaint = new Paint()
                .setColor(glowColor)
                .setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.OUTER, blurRadius));
        canvas.drawString(text, x, y, font, glowPaint);
    }

    private static void drawTextGradient(Canvas canvas, String text, float x, float y, Font font, int topColor, int bottomColor) {
        // 获取字体真实高度（使用 metrics）
        FontMetrics metrics = font.getMetrics();
        float ascent = Math.abs(metrics.getAscent()); // ascent 通常为负数
        float descent = metrics.getDescent();
        float textHeight = ascent + descent;

        Paint gradientPaint = new Paint().setShader(
                Shader.makeLinearGradient(
                        x, y - ascent,  // 渐变从文字顶部（注意：y 是 baseline，所以顶部是 y - ascent）
                        x, y, // 到文字中心
                        new int[]{topColor, bottomColor},
                        null,
                        GradientStyle.DEFAULT
                )
        );
        canvas.drawString(text, x, y, font, gradientPaint);
    }

    private static void drawAccGrade(Canvas canvas, AccGrade grade, float x, float y) {
        if(grade.getMinAcc() <= AccGrade.S.getMinAcc()) {
            canvas.drawImage(getAccGradeImage(grade), x + 100, y);
            return;
        }
        // 反正至少要画一个
        canvas.drawImage(getAccGradeImage(grade), x, y);
        canvas.drawImage(getAccGradeImage(grade), x + 117, y);
        if(grade.getMinAcc() >= AccGrade.SSS.getMinAcc()) {
            canvas.drawImage(getAccGradeImage(grade), x + 234, y);
        }
    }

    private static Image getAccGradeImage(AccGrade grade) {
        return switch(grade) {
            case SSS_AP -> LV_S_AP;
            case S, SS, SSS -> LV_S;
            case A -> LV_A;
            case B -> LV_B;
            case C -> LV_C;
            default -> LV_D;
        };
    }
}
