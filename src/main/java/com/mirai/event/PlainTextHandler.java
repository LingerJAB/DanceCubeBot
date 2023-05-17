package com.mirai.event;

import com.mirai.command.Commands;
import com.mirai.command.DeclaredCommand;
import com.mirai.command.RegexCommand;
import com.mirai.command.Scope;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.lang.reflect.Field;
import java.util.HashSet;

public class PlainTextHandler {
    private static final int MAX_LENGTH = 1024;  //单次指令字符最大长度（用于过滤）
    public static HashSet<RegexCommand> regexCommands = new HashSet<>();  //所有指令


    static {
        //初始化commands
        for(Field field : Commands.class.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.getType()==RegexCommand.class & field.isAnnotationPresent(DeclaredCommand.class)) {
                try {
                    regexCommands.add((RegexCommand) field.get(null)); // 获取并保存所有指令
                } catch(IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //事件处理
    public static void accept(MessageEvent messageEvent) {
        MessageChain messageChain = messageEvent.getMessage();

        String message;
        //At to QQ
        if(messageChain.contains(At.Key)) {
            StringBuilder builder = new StringBuilder();
            messageChain.forEach(msg -> builder.append(" ").append(msg instanceof At ? (((At) msg).getTarget()) : msg.contentToString()));
            message = builder.toString();
        } else {
            message = messageChain.contentToString();
        }

        long qq = messageEvent.getSender().getId(); // qq不为contact.getId()
        Contact contact = messageEvent.getSubject();

        if(message.length()>MAX_LENGTH) return;

        // 执行指令
        regexCommands.forEach(regexCommand -> {
            // 匹配作用域
            HashSet<Scope> scopes = regexCommand.getScopes();
            boolean find = regexCommand.getRegex().matcher(message).find();

            // 如果匹配上 regexCommand.regex
            if(scopes.contains(Scope.GLOBAL)) {
                if(find) regexCommand.onCall(Scope.GLOBAL, messageEvent, contact, qq);
            } else {
                if((scopes.contains(Scope.USER) & contact instanceof User)) {
                    if(find) regexCommand.onCall(Scope.USER, messageEvent, contact, qq);
                } else if((scopes.contains(Scope.GROUP) & contact instanceof Group))
                    if(find) regexCommand.onCall(Scope.GROUP, messageEvent, contact, qq);
            }
        });

    }
}

