package com.mirai.event;

import com.mirai.command.*;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class PlainTextHandler {
    private static final int MAX_LENGTH = 1024;  //单次指令字符最大长度（用于过滤）

    public static HashSet<RegexCommand> regexCommands = AllCommands.regexCommands;  //所有正则指令
    public static HashSet<ArgsCommand> argsCommands = AllCommands.argsCommands;  //所有参数指令
    public static HashSet<Long> adminsSet = new HashSet<>();

    static {
        // 初始化AllCommands所有指令
        AllCommands.init();
        adminsSet.add(2862125721L);
    }


    //事件处理
    public static void accept(MessageEvent messageEvent) {
        MessageChain messageChain = messageEvent.getMessage();

        String message;

        if(messageChain.stream().anyMatch(m -> m instanceof At)) {  //At 转 QQ
            StringBuilder builder = new StringBuilder();
            messageChain.forEach(msg -> builder.append(" ").append(msg instanceof At ? (((At) msg).getTarget()) : msg.contentToString()));
            message = builder.toString();
        } else {
            message = messageChain.contentToString();
        }
        if(message.length()>MAX_LENGTH) return;

        ArrayList<String> prefixAndArgs = new ArrayList<>(Arrays.asList(message.strip().split("\\s+")));

        // 执行正则指令
        regexCommands.forEach(command -> {
            // 匹配作用域
            boolean find = command.getRegex().matcher(message).find();
            if(find) {
                runCommand(messageEvent, command);
            }
        });

        //执行参数指令
        argsCommands.forEach(command -> {
            if(prefixAndArgs.size()<2) return;
            String msgPre = prefixAndArgs.remove(0);
            String[] commandPrefixes = command.getPrefix();
            if(Arrays.asList(commandPrefixes).contains(msgPre)) {
                String[] args = prefixAndArgs.toArray(new String[0]);
                if(ArgsCommand.checkError(command, args)==-1) {
                    runCommand(messageEvent, command, args);
                }
            }

        });


    }


    //含参指令
    private static void runCommand(MessageEvent messageEvent, AbstractCommand command, String[] args) {
        HashSet<Scope> scopes = command.getScopes(); //作用域
        long qq = messageEvent.getSender().getId(); // qq不为contact.getId()
        Contact contact = messageEvent.getSubject(); //发送对象

        if(scopes.contains(Scope.GLOBAL))
            command.onCall(Scope.GLOBAL, messageEvent, contact, qq, args);
        else if((scopes.contains(Scope.USER) & contact instanceof User))
            command.onCall(Scope.USER, messageEvent, contact, qq, args);
        else if((scopes.contains(Scope.GROUP) & contact instanceof Group))
            command.onCall(Scope.GROUP, messageEvent, contact, qq, args);
        else if(scopes.contains(Scope.ADMIN) & adminsSet.contains(qq))
            command.onCall(Scope.ADMIN, messageEvent, contact, qq, args);
    }


    // 无参指令
    private static void runCommand(MessageEvent messageEvent, AbstractCommand command) {
        runCommand(messageEvent, command, null);
    }
}

