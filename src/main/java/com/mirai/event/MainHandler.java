package com.mirai.event;

import com.dancecube.api.MachineList;
import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.HttpUtils;
import com.mirai.MiraiBot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// 不过滤通道
public class MainHandler extends AbstractHandler {
    @EventHandler
    public static void eventCenter(MessageEvent event) {
        MessageChain messageChain = event.getMessage();
        String message = messageChain.contentToString();
        long qq = event.getSender().getId(); // qq不为contact.getId()
        Contact contact = event.getSubject();

        // 文本消息检测
        switch(message) {
            case "个人信息" -> msgUserInfo(contact, qq);
            case "登录" -> dcLogin(contact, qq);
            case "机台登录" -> machineLogin(contact, qq, messageChain);
            case "#save" -> saveTokens(contact);
            case "#load" -> loadTokens(contact);
            default -> {
                message = message.strip();
                // 自定义指令 TODO 封装

                if(message.equals(userInfoCommand.get(qq))) {
                    msgUserInfo(contact, qq);
                }

                // 带参指令
                ArrayList<String> params = new ArrayList<>(Arrays.stream(message.split(" ")).filter(str -> !str.isBlank()).toList());
                String prefix = params.remove(0);

                if(params.size()>0) {
                    String firstParam = params.get(0);

                    switch(prefix) {  //TODO 多匹配指令前缀 (List)
                        case "查找舞立方" -> msgMachineList(contact, firstParam);
                        case "添加指令" -> addCmd(contact, qq, firstParam);
                        case "删除指令" -> delCmd(contact, qq, firstParam);
                    }
                }

            }
        }

    }

    // 添加指令 全局
    public static void addCmd(Contact contact, long qq, String newPrefix) {
        userInfoCommand.put(qq, newPrefix);
        contact.sendMessage("已添加 " + newPrefix + " !");
    }

    // 删除指令 全局
    public static void delCmd(Contact contact, long qq, String newPrefix) {
        if(!newPrefix.equals(userInfoCommand.get(qq))) {
            contact.sendMessage("未找到 " + newPrefix + " !");
            return;
        }
        contact.sendMessage("已删除 " + userInfoCommand.remove(qq, newPrefix) + " !");
    }

    // 个人信息 全局
    public static void msgUserInfo(Contact contact, long qq) {
        Token token = loginDetect(contact, qq);
        if(token==null) return;

        UserInfo user = UserInfo.get(token);
        Image image = HttpUtils.getImageFromURL(user.HeadimgURL, contact);

        String info = "昵称：%s\n战队：%s\n积分：%d\n全国排名：%d".formatted(user.UserName, user.TeamName, user.MusicScore, user.RankNation);

        contact.sendMessage(image.plus(info));
    }

    // 查找舞立方 全局
    public static void msgMachineList(Contact contact, String region) {
        StringBuilder list = new StringBuilder("舞立方机台列表：");
        List<MachineList> lists = MachineList.get(region);
        if(lists==null) return;
        for(MachineList machine : lists) {
            String online = machine.Online ? "在线" : "离线";
            String singleInfo = "店名：%s *%s\n地址：%s".formatted(machine.PlaceName, online, machine.Address);
            list.append("\n\n").append(singleInfo);
        }

        contact.sendMessage(list.toString());
    }

    // 舞立方登录 好友
    public static void dcLogin(Contact contact, long qq) {
        // 限私聊
        if(contact instanceof Group) {
            contact.sendMessage("私信才可以登录哦( •̀ ω •́ )/");
            return;
        }
        // 正在登录检测
        if(logStatus.contains(qq)) {
            contact.sendMessage("(´。＿。｀)不要重复登录啊喂！");
            return;
        }
        logStatus.add(qq);
        TokenBuilder builder = new TokenBuilder();
        Image image = HttpUtils.getImageFromURL(builder.getQrcodeUrl(), contact);

        contact.sendMessage(new PlainText("快快用微信扫码，在五分钟内登录上吧~").plus(image));
        Token token = builder.getToken();

        if(token==null) {
            contact.sendMessage("超时啦~ 请重试一下吧！");
        } else {
            contact.sendMessage("登录成功啦~(●'◡'●)\n你的ID是：%s".formatted(token.getUserId()));
            userMap.put(qq, builder.getToken());  // 重复登录只会覆盖新的token
        }
        logStatus.remove(qq);
    }

    // 机台登录 全局
    public static void machineLogin(Contact contact, Long qq, MessageChain messageChain) {
        Token token = loginDetect(contact, qq);
        if(token==null) return;

        QuoteReply quoteReply = new QuoteReply(messageChain);
        EventChannel<Event> channel = GlobalEventChannel.INSTANCE.parentScope(MiraiBot.INSTANCE);
        CompletableFuture<MessageEvent> future = new CompletableFuture<>();
        channel.subscribeOnce(MessageEvent.class, future::complete);

        contact.sendMessage(new PlainText("请在3分钟之内发送机台二维码图片哦！\n一定要清楚才好！").plus(quoteReply));
        SingleMessage message = null;
        try {
            List<SingleMessage> messageList = future.get(3, TimeUnit.MINUTES).getMessage().stream().filter(m -> m instanceof Image).toList();
            if(messageList.size()==1) {
                message = messageList.get(0);
            } else {
                contact.sendMessage("这个不是图片吧...重新发送“机台登录”吧");
            }
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch(TimeoutException e) {
            e.printStackTrace();
            contact.sendMessage(quoteReply.plus("超时啦，请重新发送吧~"));
        }
        if(message instanceof Image) {
            String imageUrl = Image.queryUrl((Image) message);
            String qrUrl = HttpUtils.QrDecode(imageUrl);
            String url = "https://dancedemo.shenghuayule.com/Dance/api/Machine/AppLogin?qrCode=" + URLEncoder.encode(qrUrl, StandardCharsets.UTF_8);
            Response response = HttpUtils.httpApi(url, Map.of("Authorization", "Bearer " + token.getAccessToken()));
            if(response.code()==200) {
                contact.sendMessage("登录成功辣，快来出勤吧！");
            } else {
                try {
                    contact.sendMessage("呜呜呜，登录错误了，再试一次吧\n" + response.body().string());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // #save 高级
    public static void saveTokens(Contact contact) {
        String path = rootPath + "/DcConfig/UserToken.json";
        TokenBuilder.tokensToFile(userMap, path);
        contact.sendMessage("保存成功！共%d条".formatted(userMap.size()));
    }

    // #load 高级
    public static void loadTokens(Contact contact) {
        String path = rootPath + "/DcConfig/UserToken.json";
        userMap = TokenBuilder.tokensFromFile(path, true);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Long, Token> entry : userMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("qq: %d , id: %s;\n".formatted(qq, token.getUserId()));
        }
        contact.sendMessage("加载成功！共%d条".formatted(userMap.size()));
        contact.sendMessage(sb.toString());
    }

    // 登录检测 内部
    public static Token loginDetect(Contact contact, Long qq) {
        Token token = userMap.get(qq);
        if(token==null) {
            // 登录检测
            contact.sendMessage("好像还没有登录欸(´。＿。｀)\n私信发送\"登录\"一起来玩吧！");
            return null;
        }
        return token.refresh() ? token : null;
    }
}
