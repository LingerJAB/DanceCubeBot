package com.mirai.event;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.config.AbstractConfig;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;

import java.util.Map;

// 不过滤通道
public class MainHandler extends AbstractConfig {


    @EventHandler
    public static void eventCenter(MessageEvent event) {
        MessageChain messageChain = event.getMessage();
        if(messageChain.size() - 1==messageChain.stream()
                .filter(msg -> msg instanceof At | msg instanceof PlainText)
                .toList().size()) {
            PlainTextHandler.accept(event);
        } else { //其它多元的消息
            return;
        }


        String message = messageChain.contentToString();
        long qq = event.getSender().getId(); // qq不为contact.getId()
        Contact contact = event.getSubject();

        // 文本消息检测
        switch(message) {
            case "#save" -> saveTokens(contact);
            case "#load" -> loadTokens(contact);
            case "#about" -> showAbout(contact);
        }
    }


    // #save 高级
    public static void saveTokens(Contact contact) {
        String path = configPath + "UserTokens.json";
        TokenBuilder.tokensToFile(userTokensMap, path);
        contact.sendMessage("保存成功！共%d条".formatted(userTokensMap.size()));
    }

    // #load 高级
    public static void loadTokens(Contact contact) {
        String path = configPath + "UserTokens.json";
        userTokensMap = TokenBuilder.tokensFromFile(path, false);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Token> entry : userTokensMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("\nqq: %d , id: %s;".formatted(qq, token.getUserId()));
        }
        contact.sendMessage("不刷新加载成功！共%d条".formatted(userTokensMap.size()) + sb);
    }

    // #about 全局
    public static void showAbout(Contact contact) {
        if(contact instanceof Group) return;
        String content = """
                你的id是%d,发送#token查看详情
                舞小铃已保存%d个账户辣！
                目前运行在Ubuntu Linux服务器上
                欢迎提出建议！
                开发者QQ:2862125721""".formatted(userTokensMap.get(contact.getId()).getUserId(), userTokensMap.size());
        contact.sendMessage(content);
    }

    public static void loadTokens() {
        String path = configPath + "UserTokens.json";
        userTokensMap = TokenBuilder.tokensFromFile(path, true);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Token> entry : userTokensMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("\nqq: %d , id: %s;".formatted(qq, token.getUserId()));
        }
        System.out.println("刷新加载成功！共%d条".formatted(userTokensMap.size()) + sb);
    }
}