package com.mirai.event;

import com.dancecube.api.Machine;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.config.AbstractConfig;
import com.mirai.config.UserConfigUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;

import java.util.*;

// 不过滤通道
public class MainHandler extends AbstractConfig {


    @EventHandler
    public static void eventCenter(MessageEvent event) {
        MessageChain messageChain = event.getMessage();
        if(messageChain.size()==2) { //单一消息（仅图片，文本，表情）
            if(messageChain.contains(PlainText.Key)) { //单一文本
                PlainTextHandler.accept(event);
            } else if(messageChain.contains(Image.Key)) { //单一图片
                return;
            } else { //其它单一的消息
                return;
            }
        } else { //其它多元的消息
            return;
        }


        String message = messageChain.contentToString();
        long qq = event.getSender().getId(); // qq不为contact.getId()
        Contact contact = event.getSubject();

        // 文本消息检测
        switch(message) {
//            case "菜单" -> msgMenu(contact);
//            case "个人信息" -> msgUserInfo(contact, qq);
//            case "个人信息 -l" -> msgUserInfoLegacy(contact, qq);
//            case "登录" -> dcLogin(contact, qq);
//            case "扫码", "机台登录" -> machineLogin(contact, qq, messageChain);
            case "#save" -> saveTokens(contact);
            case "#load" -> loadTokens(contact);
            case "#token" -> showToken(contact, qq);
            case "#refresh" -> refreshToken(contact, qq);
            case "#about" -> showAbout(contact);
            default -> {
                message = message.strip();
                // 自定义指令 TODO 封装

//                if(userInfoCommands.containsKey(qq) && userInfoCommands.get(qq).contains(message)) {
//                    msgUserInfo(contact, qq);
//                }
//                if(userTokensMap.)

                // 带参指令
                ArrayList<String> params = new ArrayList<>(Arrays.stream(message.split(" ")).filter(str -> !str.isBlank()).toList());
                String prefix = params.remove(0);

                if(params.size()>0) {
                    String firstParam = params.get(0);

                    switch(prefix) {  //TODO 多匹配指令前缀 (List)
                        case "查找舞立方", "查找机台" -> msgMachineList(contact, firstParam);
                        case "添加指令" -> addCmd(contact, qq, firstParam);
                        case "删除指令" -> delCmd(contact, qq, firstParam);
                    }
                }
            }
        }
    }

    // 添加指令 全局
    public static void addCmd(Contact contact, long qq, String newPrefix) {
        if(!userInfoCommands.containsKey(qq)) userInfoCommands.put(qq, new HashSet<>());
        userInfoCommands.get(qq).add(newPrefix);
        contact.sendMessage("已添加 \"" + newPrefix + "\" !");
    }

    // 删除指令 全局
    public static void delCmd(Contact contact, long qq, String newPrefix) {
        if(!userInfoCommands.containsKey(qq)) userInfoCommands.put(qq, new HashSet<>());
        if(!userInfoCommands.get(qq).contains(newPrefix)) {
            contact.sendMessage("未找到 \"" + newPrefix + "\" !");
            return;
        }
        userInfoCommands.get(qq).remove(newPrefix);
        contact.sendMessage("已删除 \"" + newPrefix + "\" !");
        UserConfigUtils.configsToFile(userInfoCommands, configPath + "UserCommands.json");
    }


    // 查找舞立方 全局
    public static void msgMachineList(Contact contact, String region) {
        StringBuilder machineListText = new StringBuilder("\"%s\"的舞立方机台列表：".formatted(region));
        List<Machine> list = Machine.getMachineList(region);
        if(list==null) return;
        int limit = Math.min(list.size(), contact instanceof Friend ? 99 : 5);
        for(int i = 0; i<limit; i++) {
            Machine machine = list.get(i);
            String online = machine.Online ? "🔵在线" : "🔴离线";
            String singleInfo = "店名：%s %s\n地址：%s\n".formatted(machine.PlaceName, online, machine.Address);
            machineListText.append("\n").append(singleInfo);
        }
        if(contact instanceof Group) {
            contact.sendMessage(machineListText + "⭐刷屏哒咩！群聊显示" + limit + "条就够啦，更多列表请私聊喽~");
        } else {
            contact.sendMessage(machineListText.toString());
        }
    }


    // 机台登录 全局

    // #save 高级
    public static void saveTokens(Contact contact) {
        String path = configPath + "UserTokens.json";
        TokenBuilder.tokensToFile(userTokensMap, path);
        contact.sendMessage("保存成功！共%d条".formatted(userTokensMap.size()));
    }

    // #load 高级
    public static void loadTokens(Contact contact) {
        String path = configPath + "UserTokens.json";
        userTokensMap = TokenBuilder.tokensFromFile(path, true);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Token> entry : userTokensMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("\nqq: %d , id: %s;".formatted(qq, token.getUserId()));
        }
        contact.sendMessage("加载成功！共%d条".formatted(userTokensMap.size()) + sb);
    }

    // #token 高级
    public static void showToken(Contact contact, long qq) {
        Token token = loginDetect(contact, qq);
        if(token==null) return;
        if(contact instanceof Group) {
            contact.sendMessage("私聊才能看的辣！");
        } else {
            contact.sendMessage(token.toString());
        }
    }

    public static void refreshToken(Contact contact, long qq) {
        Token token = loginDetect(contact, qq);
        if(token==null) return;
        if(contact instanceof Group) {
            contact.sendMessage("私聊才能用的辣！");
        } else {
            if(token.refresh(true))
                contact.sendMessage("#Token已强制刷新#\n\n" + token);
            else
                contact.sendMessage("刷新失败，请重新登录！");
        }
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

    //TODO 删除
    public static Token loginDetect(Contact contact, Long qq) {
        Token token = userTokensMap.get(qq);
        if(token==null) {
            // 登录检测
            contact.sendMessage("好像还没有登录欸(´。＿。｀)\n私信发送\"登录\"一起来玩吧！");
            userInfoCommands.put(qq, new HashSet<>());
            return null;
        }
        return token;
    }
}