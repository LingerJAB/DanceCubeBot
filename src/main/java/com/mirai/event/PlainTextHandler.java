package com.mirai.event;

import com.mirai.command.BotCommand;
import com.mirai.command.Command;
import com.mirai.command.DefinedCommands;
import com.mirai.command.Scope;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.Field;
import java.util.HashSet;

public class PlainTextHandler {
    public static final int MAX_LENGTH = 1024;  //单次指令字符最大长度（用于过滤）
    public static HashSet<Command> commands = new HashSet<>();  //所有指令


    static {
        //初始化commands
        for(Field field : DefinedCommands.class.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.getType()==Command.class & field.isAnnotationPresent(BotCommand.class)) {
                try {
                    commands.add((Command) field.get(null)); // 获取并保存所有指令
                } catch(IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //事件处理
    public static void accept(MessageEvent messageEvent) {
        String message = messageEvent.getMessage().contentToString();
        long qq = messageEvent.getSender().getId(); // qq不为contact.getId()
        Contact contact = messageEvent.getSubject();

        if(message.length()>MAX_LENGTH) return;

        // 执行指令
        commands.forEach(command -> {
            // 匹配作用域
            HashSet<Scope> scopes = command.getScopes();
            boolean find = command.getRegex().matcher(message).find();

            // 如果匹配上 command.regex
            if(scopes.contains(Scope.GLOBAL)) {
                if(find) command.onCall(Scope.GROUP, messageEvent, contact, qq);
                command.onCall(Scope.GLOBAL, messageEvent, contact, qq);
            } else {
                if((scopes.contains(Scope.USER) & contact instanceof User)) {
                    if(find) command.onCall(Scope.GROUP, messageEvent, contact, qq);
                    command.onCall(Scope.USER, messageEvent, contact, qq);
                } else if((scopes.contains(Scope.GROUP) & contact instanceof Group))
                    if(find) command.onCall(Scope.GROUP, messageEvent, contact, qq);
            }
        });

    }
}

