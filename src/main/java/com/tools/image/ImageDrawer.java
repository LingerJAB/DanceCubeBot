package com.tools.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

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
        if(effect.spaceHeight!=null) {
            int lineHeight = graphics.getFontMetrics().getHeight();    // 获取文本行高
            for(String line : text.split("\n")) {
                if(effect.maxWidth!=null) {
                    line = addDots(line, effect.maxWidth);
                }
                graphics.drawString(line, x, y);
                y += lineHeight + effect.spaceHeight;
            }
        } else if(effect.maxWidth!=null) {
            text = addDots(text, effect.maxWidth);
            graphics.drawString(text, x, y);
        } else {
            graphics.drawString(text, x, y);
        }
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y) {
        drawImage(bufferedImage, x, y, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, ImageEffect effect) {
        if(effect!=null) {
            bufferedImage = makeRoundCorner(bufferedImage, effect.arcW, effect.arcH);
        }
        graphics.drawImage(bufferedImage, x, y, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        drawImage(bufferedImage, x, y, width, height, null);
        return this;
    }

    public ImageDrawer drawImage(BufferedImage bufferedImage, int x, int y, int width, int height, ImageEffect effect) {
        if(effect!=null) {
            bufferedImage = makeRoundCorner(bufferedImage, effect.arcW, effect.arcH);
        }
        graphics.drawImage(bufferedImage, x, y, width, height, null);
        return this;
    }

    public ImageDrawer drawImage(String url, int x, int y, int width, int height) {
        BufferedImage image = null;
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

    public ImageDrawer antiAliasing() {
        //消除文字锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return this;
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

            while(textWidth + ellipsisWidth>maxWidth && text.length()>0) {
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


}

