package com.mirai.event;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;

import java.util.Map;

import static com.mirai.config.AbstractConfig.configPath;
import static com.mirai.config.AbstractConfig.userTokensMap;

// 不过滤通道
public class MainHandler {

    @EventHandler
    public static void eventCenter(MessageEvent event) {
        MessageChain messageChain = event.getMessage();
        if(messageChain.size() - 1==messageChain.stream()
                .filter(msg -> msg instanceof At | msg instanceof PlainText)
                .toList().size()) {
            PlainTextHandler.accept(event);
        } else return;

        String message = messageChain.contentToString();
        long qq = event.getSender().getId(); // qq不为contact.getId()
        Contact contact = event.getSubject();

        // 文本消息检测
        switch(message) {
            case "#save" -> saveTokens(contact);
            case "#load" -> loadTokens(contact);
        }
    }

    // #save 高级
    public static void saveTokens(Contact contact) {
        TokenBuilder.tokensToFile(userTokensMap, configPath + "UserTokens.json");
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


    @Deprecated
    public static void loadTokens() {
        StringBuilder sb = new StringBuilder();
        if(userTokensMap==null) return;

        for(Map.Entry<Long, Token> entry : userTokensMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("\nqq: %d , id: %s;".formatted(qq, token.getUserId()));
        }
        System.out.println("刷新加载成功！共%d条".formatted(userTokensMap.size()) + sb);
    }
}