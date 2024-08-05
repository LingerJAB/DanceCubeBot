package com.dancecube.image;

import com.dancecube.token.Token;
import com.tools.image.ImageDrawer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.mirai.config.AbstractConfig.configPath;

public class LastPlayImage {
    public static final String PATH = configPath + "Images/LastPlayImage/";
    static String savePath = configPath + "Images/result.png";

    public static void main(String[] args) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(configPath + "Fonts\\SourceHanSans.otf"));
        File file = new File(savePath);
        Token token = new Token("62NfwwGhPbmF4mC9NfKD-_RvSaI6rxud2dpoFji638d07hXbRMsQ1g2C_wvLegMCD3X64eixqSv_r_1JtJHVuBFxZxKVMfhkWlJgdL7fR0wI2n-9HM-onbaTpOeEaZEc2efx402Bl8RB8_ZFkd-LwGUSo9ORCMZwFE4-RZHsXxNQK53YX5mHMpmZYkRZCxHtvwXlYvqja7QRAuMEXqUAB0DmcGnXc8cwDf6w0dHjxP-umpcPPtjOw4l_KUC-2X1BVRC4D3QhFKymZdhETbz0iVQWgfNrzD6RGfjTj7eN_-1RDJS8A1k1dwUvoSXKlnO4dhbnhJ3Vf8M4PwF4IhdRgeJ87Emou-n2wiITbz4s8s3x0D9QatXI4ntz3hMXiX9g");
//        generate(token);
        BufferedImage bufferedImage = ImageIO.read(generate2(token));
        ImageIO.write(bufferedImage, "PNG", file);
    }

    public static InputStream generate(Token token) throws IOException, FontFormatException {
        Font titleFont = Font.createFont(Font.PLAIN, new File(configPath + "Fonts/SourceHanSans.otf"));

//        titleFont = new Font("Arial", Font.PLAIN, 50);
        File file = new File(PATH + "Background1.png");


        ImageDrawer drawer = new ImageDrawer(file);
        drawer.setAntiAliasing();
        String text = "おやすみなさい";
        Graphics2D graphics = drawer.getGraphics();
        FontMetrics metrics = graphics.getFontMetrics();

        graphics.setFont(titleFont.deriveFont(50f));

        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.stringWidth(text);
        float[] portions = new float[]{0.2f, 0.3f}; // 每段的起始位置在总长的比例（必须要递增）
        Color[] colors = new Color[]{Color.yellow, Color.white}; // 每段对应的颜色
        LinearGradientPaint linearGradientPaint = new LinearGradientPaint(10, 100, 10, 100 - textHeight, portions, colors); // 起始点，终点，比例，颜色
        graphics.setPaint(linearGradientPaint); // 选择好渐变颜色
        graphics.drawString(text, 10, 100);


        drawer.save("PNG", new File(savePath));


        return drawer.getImageStream("PNG");
    }

    public static InputStream generate2(Token token) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.PLAIN, new File(configPath + "Fonts/SourceHanSans.otf"));
        font = font.deriveFont(Font.PLAIN, 50);
        File file = new File(PATH + "Background1.png");

        String text = "おやすみなさい啦啦啦";

        // 创建Graphics2D环境来测量文本大小
        Graphics2D g2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(text, frc);

        // 计算图片大小（向上取整）
        int width = (int) Math.ceil(bounds.getWidth());
        int height = (int) Math.ceil(bounds.getHeight());

        // 创建BufferedImage实例
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();

        // 设置抗锯齿渲染
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置背景透明
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.Src);

        // 设置字体并绘制文本
        g2d.setFont(font);
        g2d.drawString(text, 0, height); // 注意：drawString的y坐标是基线，可能需要调整


        return new ImageDrawer(image).getImageStream("PNG");
    }
}
