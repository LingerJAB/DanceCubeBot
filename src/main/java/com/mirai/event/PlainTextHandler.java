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
import java.util.Calendar;
import java.util.HashSet;

public class PlainTextHandler {

    public static HashSet<Long> adminsSet = new HashSet<>();

    static {
        // 初始化AllCommands所有指令
        AllCommands.init();
        adminsSet.add(2862125721L);
    }

    public static HashSet<RegexCommand> regexCommands = AllCommands.regexCommands;  //所有正则指令
    public static HashSet<ArgsCommand> argsCommands = AllCommands.argsCommands;  //所有参数指令

    private static final int MAX_LENGTH = 0x7fff;  //单次指令字符最大长度（用于过滤）


    /**
     * 事件处理
     *
     * @param messageEvent 消息事件
     */
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


        // 执行正则指令
        for(RegexCommand regexCommand : regexCommands) {// 匹配作用域
            boolean find = regexCommand.getRegex().matcher(message).find();
            if(find) {
                runCommand(messageEvent, regexCommand);
            }
        }

        ArrayList<String> prefixAndArgs = new ArrayList<>(Arrays.asList(message.strip().split("\\s+")));
        String msgPre = prefixAndArgs.remove(0); //前缀
        String[] args = prefixAndArgs.isEmpty() ? null : prefixAndArgs.toArray(new String[0]); //参数 奇奇怪怪的特性，这不是空数组！


        //执行参数指令
        for(ArgsCommand command : argsCommands) {
            String[] commandPrefixes = command.getPrefix();
            if(Arrays.asList(commandPrefixes).contains(msgPre)) {
                if(ArgsCommand.checkError(command, args) < 0) { //args可能不存在，需要判空
                    runCommand(messageEvent, command, args);
                }
            }
        }


    }

    private static final MsgHandleable MUTE = (event, contact, qq, args) -> contact.sendMessage("小铃困啦，白天再来玩吧，先晚安安啦~");

    /**
     * 宵禁
     *
     * @return 是否在宵禁
     */
    public static boolean isMutedNow() {
        // 获取当前时间
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        // 如果当前时间是 3 点到 4 点，则将 `autoMuted` 设置为 true
        return hour>=3 && hour<=4;
    }

    //含参指令
    private static void runCommand(MessageEvent messageEvent, AbstractCommand command, String[] args) {

        HashSet<Scope> scopes = command.getScopes(); //作用域
        long qq = messageEvent.getSender().getId(); // qq不为contact.getId()
        Contact contact = messageEvent.getSubject(); //发送对象

        if(isMutedNow()) {
            MUTE.handle(messageEvent, contact, qq, args);
            if(!adminsSet.contains(qq)) return;
        }

        if(scopes.contains(Scope.GLOBAL))
            command.onCall(Scope.GLOBAL, messageEvent, contact, qq, args);
        else if((scopes.contains(Scope.USER) & contact instanceof User))
            command.onCall(Scope.USER, messageEvent, contact, qq, args);
        else if((scopes.contains(Scope.GROUP) & contact instanceof Group))
            command.onCall(Scope.GROUP, messageEvent, contact, qq, args);
        else if(scopes.contains(Scope.ADMIN) & adminsSet.contains(qq))
            command.onCall(Scope.ADMIN, messageEvent, contact, qq, args);
    }

    // 无参指令（其实就是给上面的runCommand传了个args=null）
    private static void runCommand(MessageEvent messageEvent, AbstractCommand command) {
        runCommand(messageEvent, command, null);
    }
}

