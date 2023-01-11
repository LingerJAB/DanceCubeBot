package com.mirai.channel;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.HttpUtils;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// 好友通道
public class FriendChannel extends AbstractChannel {

    public static void eventCenter(FriendMessageEvent event) {
        String content = event.getMessage().contentToString();
        long qq = event.getSender().getId();
        Friend friend = event.getFriend();

        // 发送消息检测
        switch(content) {
            case "舞立方登录" -> dcLogin(friend);

            case "#save" -> saveTokens(friend);
            case "#load" -> loadTokens(friend);
            default -> {
                if(userMap.get(qq)==null) friend.sendMessage("还没有登录呢，发送“舞立方登录”试试吧~");
            }
        }
    }

    public static void dcLogin(Friend friend) {
        long qq = friend.getId();
        // 正在登录检测
        if(logStatus.contains(qq)) {
            friend.sendMessage("(´。＿。｀)不要重复登录啊喂！");
            return;
        }
        logStatus.add(qq);

        try {

            TokenBuilder builder = new TokenBuilder();
            ExternalResource ex = HttpUtils.getExResByURL(new URL(builder.getQrcodeUrl()));
            Image image = ExternalResource.uploadAsImage(ex, friend);

            friend.sendMessage(new PlainText("快快用微信扫码，在五分钟内登录上吧~").plus(image));
            ex.close();
            Token token = builder.getToken();

            if(token==null) {
                friend.sendMessage("超时啦~ 请重试一下吧！");
            } else {
                friend.sendMessage("登录成功啦~(●'◡'●)\n你的ID是：%s".formatted(token.getUserId()));
                userMap.put(qq, builder.getToken());  // 重复登录只会覆盖新的token
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        logStatus.remove(qq);
    }

    public static void saveTokens(Friend friend) {
        String path = rootPath + "/DcConfig/UserToken.json";
        TokenBuilder.tokensToFile(userMap, path);
        friend.sendMessage("保存成功！共%d条".formatted(userMap.size()));
    }

    public static void loadTokens(Friend friend) {
        String path = rootPath + "/DcConfig/UserToken.json";
        userMap = TokenBuilder.tokensFromFile(path,true);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Token> entry : userMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("qq: %d , id: %s;\n".formatted(qq, token.getUserId()));
        }
        friend.sendMessage("加载成功！共%d条".formatted(userMap.size()));
        friend.sendMessage(sb.toString());
    }
}
