package com.dancecube.image;


import com.dancecube.info.AccountInfo;
import com.dancecube.info.ReplyItem;
import com.dancecube.info.UserInfo;
import com.dancecube.ratio.image.ImageDrawer;
import com.dancecube.ratio.image.TextEffect;
import com.dancecube.token.Token;
import com.mirai.MiraiBot;
import com.mirai.config.AbstractConfig;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;
import org.junit.Test;

import java.awt.*;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class UserInfoImage extends AbstractConfig {
    public static InputStream generate(Token token) {
        String bgPath = "file:" + configPath + "Images/Background.png";

        UserInfo userInfo = UserInfo.get(token);
        AccountInfo accountInfo = AccountInfo.get(token);
        ReplyItem replyItem = ReplyItem.get(token);

        JavaPluginScheduler scheduler = MiraiBot.INSTANCE.getScheduler();
        try {
            Future<UserInfo> userInfoFuture = scheduler.async(() -> UserInfo.get(token));
            Future<AccountInfo> accountInfoFuture = scheduler.async(() -> AccountInfo.get(token));
            userInfo = userInfoFuture.get();
            accountInfo = accountInfoFuture.get();
        } catch(ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        ImageDrawer drawer = new ImageDrawer(bgPath);
        drawer.antiAliasing();

        drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgURL()), 120, 150, 137, 137);

        if(!userInfo.getHeadimgBoxPath().equals("")) // 头像框校验
            drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgBoxPath()), 74, 104, 230, 230);
        if(!userInfo.getTitleUrl().equals("")) // 头衔校验
            drawer.drawImage(ImageDrawer.read(userInfo.getTitleUrl()), 108, 300, 161, 68);

        Font font = new Font("得意黑", Font.PLAIN, 36);
        Font font2 = new Font("得意黑", Font.PLAIN, 20);
        TextEffect effect = new TextEffect(null, 0);
        drawer.font(font)
                .drawText("%s\n\n战队：%s\n积分：%d\n金币：%d"
                        .formatted(userInfo.getUserName(),
                                userInfo.getTeamName(),
                                userInfo.getMusicScore(),
                                accountInfo.getGold()), 293, 137, effect)
                .drawText("战力：%s\n全连率：%.2f%%\n全国排名：%d\n游玩次数：%d"
                        .formatted(userInfo.getLvRatio(),
                                (float) userInfo.getComboPercent() / 100,
                                userInfo.getRankNation(),
                                replyItem.getPlayedTimes()), 106, 472, effect)
                .font(font2)
                .drawText("ID：" + userInfo.getUserID(), 293, 173)
                .dispose();
//        drawer.save("PNG", new File("C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png"));

        return drawer.getImageStream("PNG");
    }

    @Test
    public void test() throws IOException {
        Token token = new Token(939088, "O2fxf-_RBrEukJTwdN4UrbMWg8ECGN-JYUF46TWTSj47uxc2QICsiHvDIqBl1F79DwLUN2os3ZE-itHE70ukkNQG2AV6gbE_DI8pEhD1hbYQPOQqEgunIN4mOrFvGTcGJqptpdnJE876GfrCjWPoHxocfVr7ukEyS7CO5kSKA0G38e6TWfhjiMfKVLHJZOjGefE3rh8zPA6bqHLHoHNFq9Zybu4wwUc63CLBEgxOjnztN9BFgUZCxaZn260iVcur3sIvYXCwnBal4rbeTnTS15rL3JHIUszLT-JKzJJU7FxPlmLMeWCAvhAwizgkOj9CZtVaj8gX34riQmjKVP1RDcQmcL0YiNzdHnfyke5RiUwhjaKOIKpGgMVWMD9xKLeP");
        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png";
        saveImg(generate(token), path);
    }

    public static void saveImg(InputStream stream, String path) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while((len = stream.read(buffer))!=-1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        stream.close();
        //把outStream里的数据写入内存

        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = outStream.toByteArray();
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File(path);
        //创建输出流
        FileOutputStream fileOutStream = new FileOutputStream(imageFile);
        //写入数据
        fileOutStream.write(data);
    }

    public static void getAvailableFonts() {
        // 获取系统所有可用字体名称
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName = e.getAvailableFontFamilyNames();
        for(String s : fontName) {
            System.out.println(s);
        }
    }
}

