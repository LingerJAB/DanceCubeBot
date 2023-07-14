package com.dancecube.image;


import com.dancecube.info.AccountInfo;
import com.dancecube.info.InfoStatus;
import com.dancecube.info.ReplyItem;
import com.dancecube.info.UserInfo;
import com.dancecube.token.Token;
import com.mirai.config.AbstractConfig;
import com.tools.image.ImageDrawer;
import com.tools.image.TextEffect;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class UserInfoImage extends AbstractConfig {
    //UserInfo生成
    public static InputStream generate(Token token, int id) {
        //todo 随机背景
        String bgPath = "file:" + configPath + "Images/UserInfoImage/Background2.png";

        UserInfo userInfo = UserInfo.get(token, id);
        // 不存在查询的id
        if(userInfo.getStatus()==InfoStatus.NONEXISTENT) return null;

        //不存在用户
        if(userInfo.getHeadimgURL()==null) return null;

        ImageDrawer drawer = new ImageDrawer(bgPath);
        drawer.antiAliasing();
        drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgURL()), 120, 150, 137, 137);

        if(!userInfo.getHeadimgBoxPath().equals("")) // 头像框校验
            drawer.drawImage(ImageDrawer.read(userInfo.getHeadimgBoxPath()), 74, 104, 230, 230);
        if(!userInfo.getTitleUrl().equals("")) // 头衔校验
            drawer.drawImage(ImageDrawer.read(userInfo.getTitleUrl()), 108, 300, 161, 68);

        Font font = new Font("得意黑", Font.PLAIN, 36);
        Font font2 = new Font("得意黑", Font.PLAIN, 20);
        TextEffect effect = new TextEffect(235, 0);
        drawer.font(font);
        //信息开放
        if(userInfo.getStatus()!=InfoStatus.PRIVATE) {
            String gold = "未登录";
            String playedTimes = "未登录";
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
    public void test() throws IOException {
        Token token = new Token(939800, "PViJl0yNANEslG5QPYZjiI7ZcAg8F5L7o0mXH2TJpkEsoZQcAh3DrUN3CPgvx-KFNoMFMQDMtQbZs9opoXnL6_VaOSDIf2VvlJBpBHY7XF-YSAnIdkRaLbyiUF6J6WqvKFiP6WcSEcdXde-ifSp2GlifVvGE4NiGxTPmY2NsV61T9LCd6CzhQyz97408jGYUsTq9I-d8ewZ65qE3TayXn18SGseZG924fOr-tOYWiEFESXnLNzMbrsRIiSSMtuc4ell1_4479J7WFGvZkvmgGSa3qis4WmvFUJkuTcAH5OuqjCeroiLwz1ksCGriCt6b7CGVFAHTkoEI0XMBdwiw-t_LczRuLZeS9_JCAH-DZ3bdCcL_i26a9jYyAqqpggQchgKMUYyy7j_jR7QhcEoCLodAgAtU4PN4WoZRHp7DhAhxQMY-9ua66ZJBhu2b6tEdUocjN4FUMRv3Qv_Fg53WBKB8f36fqFxZ5HTdpaiPF1ig5ipI0hM3rRYEWWvxg4j_IjzzMJDGQHN5KhSXEvjk7TSUvCaOuM9DR8fdbaiUTz2JC0QCw9SG4l_mlVdkf7zmj3ZfhiteGZ1-n3VXl9y_KyKKEuuL-_0YGn6qDvS9ng5fUdwki3WUlZ34TJrYaNmImGQmnEjQTpvFGxKgdpMOR-P4vAm0W8HVS9r15Kht2wAM5GHWoXmlvjva-oLt6LbJICd_u6svAFn92VwHlx6179LSMr6iZppK4GEC-XS9kJTwCDMZ_XLXoqczOBRngz9M69NMRVpmRJ8c0Bn55lbiA6n8sAcbHAYtGCh3P5gnPO_1PKDn0UQ1FovmA7uS1Xvfc6fk0Ugi29yCC_rhv0R4MZbxodOyCha4rMgDd8XlsKfQJNpCMNWogAD7vhWYIpACOXjRjCG4Q5lweR3XxbSRPA");
        String path = "C:\\Users\\Lin\\IdeaProjects\\DanceCubeBot\\DcConfig\\Images\\result.png";
        ImageDrawer.write(generate(token, 2550784), path);
    }
}

