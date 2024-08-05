package com.tools.image;

import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

public class ImageDrawer {
    private final BufferedImage originImage;
    private final Graphics2D graphics;

    public ImageDrawer(BufferedImage image) {
        originImage = image;
        graphics = originImage.createGraphics();
        graphics.setColor(Color.BLACK);
    }

    public ImageDrawer(File file) {
        try {
            originImage = ImageIO.read(new FileInputStream(file));
            graphics = originImage.createGraphics();
            graphics.setColor(Color.BLACK);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageDrawer(String url) {
        try {
            originImage = ImageIO.read(new URL(url));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        graphics = originImage.createGraphics();
        graphics.setColor(Color.BLACK);
    }

    public ImageDrawer color(Color color) {
        graphics.setColor(color);
        return this;
    }

    public ImageDrawer font(Font font) {
        graphics.setFont(font);
        return this;
    }

    public ImageDrawer paint(Paint paint) {
        graphics.setPaint(paint);
        return this;
    }

    public ImageDrawer clip(Shape clip) {
        graphics.setClip(clip);
        return this;
    }


    public ImageDrawer clip(int x, int y, int width, int height) {
        graphics.setClip(x, y, width, height);
        return this;
    }

    private BufferedImage makeBlur(BufferedImage srcImage, int radius) {

        if(radius<1) {
            return srcImage;
        }

        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        int[] pix = new int[w * h];
        srcImage.getRGB(0, 0, w, h, pix, 0, w);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for(i = 0; i<256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for(y = 0; y<h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for(i = -radius; i<=radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if(i>0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for(x = 0; x<w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if(y==0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for(x = 0; x<w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for(i = -radius; i<=radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if(i>0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if(i<hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for(y = 0; y<h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if(x==0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        srcImage.setRGB(0, 0, w, h, pix, 0, w);
        return srcImage;
    }

    public ImageDrawer font(Font font, Color color) {
        graphics.setFont(font);
        graphics.setColor(color);
        return this;
    }

    public ImageDrawer drawText(String text, int x, int y) {
        graphics.drawString(text, x, graphics.getFontMetrics().getHeight() + y);
        return this;
    }

    public ImageDrawer drawText(String text, int x, int y, TextEffect effect) {
        y = y - graphics.getFontMetrics().getDescent() + graphics.getFontMetrics().getHeight();
        if(effect.getSpaceHeight()!=null) {
            int lineHeight = graphics.getFontMetrics().getHeight();    // 获取文本行高
            for(String line : text.split("\n")) {
                if(effect.getMaxWidth()!=null) {
                    line = addDots(line, effect.getMaxWidth());
                }
                graphics.drawString(line, x, y);
                y += lineHeight + effect.getSpaceHeight();
            }
        } else if(effect.getMaxWidth()!=null) {
            text = addDots(text, effect.getMaxWidth());
            graphics.drawString(text, x, y);
        } else {
            graphics.drawString(text, x, y);
        }
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y) {
        graphics.drawImage(bufferedImage, x, y, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, @NotNull ImageEffect effect) {
        bufferedImage = makeRoundCorner(bufferedImage, effect.getArcW(), effect.getArcH());
        graphics.drawImage(bufferedImage, x, y, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        graphics.drawImage(bufferedImage, x, y, width, height, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, int width, int height, @NotNull ImageEffect effect) {
        bufferedImage = makeRoundCorner(bufferedImage, effect.getArcW(), effect.getArcH());
        graphics.drawImage(bufferedImage, x, y, width, height, null);
        return this;
    }

    public ImageDrawer drawImage(String url, int x, int y, int width, int height) {
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(url));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        graphics.drawImage(image, x, y, width, height, null);
        return this;
    }

    public ImageDrawer dispose() {
        graphics.dispose();
        return this;
    }

    /**
     * 设置抗锯齿
     */
    public void setAntiAliasing() {
        //消除文字锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void save(String formatName, OutputStream outputStream) {
        graphics.dispose();
        try {
            ImageIO.write(originImage, formatName, outputStream);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String formatName, File file) {
        graphics.dispose();
        try {
            ImageIO.write(originImage, formatName, new FileOutputStream(file));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getBufferedImage() {
        return originImage;
    }

    public InputStream getImageStream(String format) {
        graphics.dispose();
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(originImage, format, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch(IOException e) {
            throw new RuntimeException("执行图片合成失败，无法输出文件流");
        }
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public static BufferedImage read(File file) {
        try {
            return ImageIO.read(file);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage read(InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static BufferedImage read(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void write(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "PNG", new File(path));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(InputStream inputStream, String path) {
        try {
            write(ImageIO.read(inputStream), path);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage makeRoundCorner(BufferedImage srcImage, int radiusW, int radiusH) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, width, height, radiusW, radiusH);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(srcImage, 0, 0, width, height, null);
        g.dispose();
        return image;
    }

    private String addDots(String text, int maxWidth) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);

        if(textWidth>maxWidth) {
            String ellipsis = "...";
            int ellipsisWidth = metrics.stringWidth(ellipsis);

            while(textWidth + ellipsisWidth>maxWidth && !text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
                textWidth = metrics.stringWidth(text);
            }
            text += ellipsis;
        }
        return text;
    }

    public static void printAvailableFonts() {
        // 获取系统所有可用字体名称
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName = e.getAvailableFontFamilyNames();
        for(String s : fontName) {
            System.out.println(s);
        }
    }

    public static BufferedImage convertPngToJpg(BufferedImage pngImage, float quality) {
        BufferedImage rgbImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.drawImage(pngImage, 0, 0, null);
        g.dispose();
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if(!writers.hasNext()) {
                throw new IllegalStateException("No writers found for JPEG format.");
            }
            ImageWriter writer = writers.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();

            // 设置压缩质量
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(quality);

            writer.setOutput(ImageIO.createImageOutputStream(os));
            IIOImage iioImage = new IIOImage(rgbImage, null, null);
            writer.write(null, iioImage, iwp);
            writer.dispose();

            // 从字节数组中读取JPEG图像
            try(ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                return ImageIO.read(is);
            }
        } catch(Exception e) {
            throw new RuntimeException("Error compressing image to JPEG format and reading it back.", e);
        }
    }
}

