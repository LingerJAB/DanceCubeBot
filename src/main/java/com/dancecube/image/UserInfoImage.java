package com.dancecube.image;


import com.dancecube.info.AccountInfo;
import com.dancecube.info.InfoStatus;
import com.dancecube.info.ReplyItem;
import com.dancecube.info.UserInfo;
import com.dancecube.token.Token;
import com.tools.image.ImageDrawer;
import com.tools.image.TextEffect;
import org.junit.Test;

import java.awt.*;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.mirai.command.AllCommands.scheduler;
import static com.mirai.config.AbstractConfig.configPath;
import static com.mirai.config.AbstractConfig.itIsAReeeeaaaalWindowsMark;


public class UserInfoImage {
    /**
     * 生成个人信息图，可能是用户查询别人账号
     *
     * @param token 发起查看信息的用户
     * @param id    需要查看的信息目标
     */
    public static InputStream generate(Token token, int id) {
        //todo 随机背景
        String bgPath = "file:" + configPath + "Images/UserInfoImage/Background2.png";

        UserInfo userInfo = UserInfo.get(token, id);
        // 不存在查询的id
        if(userInfo.getStatus()==InfoStatus.NONEXISTENT) return null;

        //不存在用户
        if(userInfo.getHeadimgURL()==null) return null;

        ImageDrawer drawer = new ImageDrawer(bgPath);
        drawer.setAntiAliasing();
        drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgURL()), 120, 150, 137, 137);

        if(!userInfo.getHeadimgBoxPath().equals("")) // 头像框校验
            drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgBoxPath()), 74, 104, 230, 230);
        if(!userInfo.getTitleUrl().equals("")) // 头衔校验
            drawer.drawImage(ImageDrawer.read(userInfo.getTitleUrl()), 108, 300, 161, 68);

        Font font = new Font("得意黑", Font.PLAIN, 36);
        Font font2 = new Font("得意黑", Font.PLAIN, 20);
        TextEffect effect = new TextEffect().setMaxWidth(235).setSpaceHeight(0);
        drawer.font(font);
        //信息开放
        if(userInfo.getStatus()!=InfoStatus.PRIVATE) {
            String gold = "不可见";
            String playedTimes = "不可见";
            if(token.getUserId()==id) {
                ReplyItem replyItem;
                AccountInfo accountInfo;

                // 异步获取个人信息
                if(itIsAReeeeaaaalWindowsMark()) {
                    accountInfo = AccountInfo.get(token);
                    replyItem = ReplyItem.get(token);
                } else {
                    try {
                        Future<ReplyItem> replyItemFuture = scheduler.async(() -> ReplyItem.get(token));
                        Future<AccountInfo> accountInfoFuture = scheduler.async(() -> AccountInfo.get(token));
                        replyItem = replyItemFuture.get();
                        accountInfo = accountInfoFuture.get();
                    } catch(ExecutionException | InterruptedException e) {
                        accountInfo = AccountInfo.get(token);
                        replyItem = ReplyItem.get(token);
                    }
                }

                gold = String.valueOf(accountInfo.getGold());
                playedTimes = String.valueOf(replyItem.getPlayedTimes());


            }
            drawer.drawText("%s\n\n战队：%s\n战力：%d\n金币：%s"
                            .formatted(userInfo.getUserName(),
                                    userInfo.getTeamName().equals("") ? "无" : userInfo.getTeamName(),
                                    userInfo.getLvRatio(),
                                    gold), 293, 137, effect)
                    .drawText("积分：%s\n全连率：%.2f%%\n全国排名：%d\n游玩次数：%s"
                            .formatted(userInfo.getMusicScore(),
                                    (float) userInfo.getComboPercent() / 100,
                                    userInfo.getRankNation(),
                                    playedTimes), 106, 472, effect)
                    .font(font2)
                    .drawText("ID：" + userInfo.getUserID(), 293, 170);
        } else { //屏蔽
            drawer.drawText("%s\n\n地区：%s\n战力：%d"
                            .formatted(userInfo.getUserName(),
                                    userInfo.getCityName().equals("") ? "无" : userInfo.getCityName(),
                                    userInfo.getLvRatio()), 293, 137, effect)
                    .drawText("该账号已设置隐私", 106, 472)
                    .font(font2)
                    .drawText("ID：" + userInfo.getUserID(), 293, 170);

        }
        drawer.dispose();
        return drawer.getImageStream("PNG");
    }

    @Test
    public void test() {
        Token token = new Token(939800, "Tbp4C0QAJeIsqNcJx7psKoYAxFNsD0qH68qutYCZod8ybPiRoEJ05RZhHzy4LPQDtw3tJvKYqSkCpnEd-qrg-c7MMY7DwQecXF3-uuU-6qDd7zIQ7IpfTHbcVHvN_st9XnVCyt9op0b6CYFY3nTvNH1F4aidP5M-P-MXes3-TIH80YHN8zHgua_XjgFWfi0loubYS0KW9APsB0POsoaBmeJz-85ZxnqlOdzUkW7cb9vGPzgQvP7adZPa6igEfynpx1YXTthssnhGyjKdMSQnKkR2Zhmx4zdbwo9N1eTDoAv0ZuNZ9-29gSirqGHbwRS-GXPnXG4mGLvdMuRWY1OKuLk1HWvV-AsceOuMvZX9vin0BcxGDKmK4axbU8kRQkx-");
        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png";
        ImageDrawer.write(generate(token, 939088), path);
    }
}

