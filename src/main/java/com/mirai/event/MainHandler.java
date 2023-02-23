package com.mirai.event;

import com.dancecube.api.Machine;
import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.HttpUtils;
import com.mirai.MiraiBot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import okhttp3.Response;

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
            case "菜单" -> msgMenu(contact);
            case "个人信息" -> msgUserInfo(contact, qq);
            case "登录" -> dcLogin(contact, qq);
            case "机台登录" -> machineLogin(contact, qq, messageChain);
            case "#save" -> saveTokens(contact);
            case "#load" -> loadTokens(contact);
            case "#token" -> showToken(contact, qq);
            case "#about" -> showAbout(contact);
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

    // 菜单 全局
    public static void msgMenu(Contact contact) {
        String menu = """
                舞小铃有以下功能哦！
                1. 登录
                -登录才能和舞小铃玩！
                2. 个人信息
                -开发中，只能显示一部分
                3. 机台登录
                -可以拍照扫码舞立方机台！
                4. 添加指令 [名称]
                -换个方式查看信息！
                5. 查找舞立方 [地名]
                越详细地名越精确！
                6. chatgpt
                再键入stop才可停止
                ❤️其它问题请联系开发者 [铃] 酱！""";
        contact.sendMessage(menu);
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

        UserInfo user = new UserInfo(token);
        Image image = HttpUtils.getImageFromURL(user.getHeadimgURL(), contact);

        String info = "昵称：%s\n战队：%s\n积分：%d\n金币：%d\n全国排名：%d".formatted(user.getUserName(), user.getTeamName(), user.getMusicScore(), user.getGold(), user.getRankNation());

        contact.sendMessage(image.plus(info));
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

    // 登录 好友
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
            userTokensMap.put(qq, builder.getToken());  // 重复登录只会覆盖新的token
        }
        logStatus.remove(qq);
    }

    // 机台登录 全局
    public static void machineLogin(Contact contact, Long qq, MessageChain messageChain) {
        Token token = loginDetect(contact, qq);
        if(token==null) return;

//        QuoteReply quoteReply = new QuoteReply(messageChain);
        EventChannel<Event> channel = GlobalEventChannel.INSTANCE.parentScope(MiraiBot.INSTANCE);
        CompletableFuture<MessageEvent> future = new CompletableFuture<>();
        channel.subscribeOnce(MessageEvent.class, future::complete);

        contact.sendMessage(new PlainText("请在3分钟之内发送机台二维码图片哦！\n一定要清楚才好！").plus(new QuoteReply(messageChain)));
        SingleMessage message;
        try {
            MessageChain nextMessage = future.get(3, TimeUnit.MINUTES).getMessage();
            List<SingleMessage> messageList = nextMessage.stream().filter(m -> m instanceof Image).toList();
            if(messageList.size()!=1) {
                contact.sendMessage(new PlainText("这个不是图片吧...重新发送“机台登录”吧").plus(new QuoteReply(nextMessage)));
            } else {  // 第一个信息
                message = messageList.get(0);
                String imageUrl = Image.queryUrl((Image) message);
                String qrUrl = HttpUtils.qrDecodeTencent(imageUrl);
                if(qrUrl==null) {  // 若扫码失败
                    contact.sendMessage(new PlainText("没有扫出来！再试一次吧！").plus(new QuoteReply((MessageChain) message)));
                    return;
                }
                String url = "https://dancedemo.shenghuayule.com/Dance/api/Machine/AppLogin?qrCode=" + URLEncoder.encode(qrUrl, StandardCharsets.UTF_8);
                try(Response response = HttpUtils.httpApi(url, Map.of("Authorization", "Bearer " + token.getAccessToken()))) {
                    //401 404
                    if(response!=null && response.code()==200) {
                        contact.sendMessage("登录成功辣，快来出勤吧！");
                    }
                }
            }
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch(TimeoutException e) {
            e.printStackTrace();
            contact.sendMessage(new QuoteReply(messageChain).plus("超时啦，请重新发送吧~"));
        }
    }

    // #save 高级
    public static void saveTokens(Contact contact) {
        String path = rootPath + "/DcConfig/UserToken.json";
        TokenBuilder.tokensToFile(userTokensMap, path);
        contact.sendMessage("保存成功！共%d条".formatted(userTokensMap.size()));
    }

    // #load 高级
    public static void loadTokens(Contact contact) {
        String path = configPath + "UserToken.json";
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
        if(loginDetect(contact, qq)!=null) {
            if(contact instanceof Group) {
                contact.sendMessage("私聊才能看的辣！");
            } else {
                contact.sendMessage(userTokensMap.get(qq).toString());
            }
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

    // 登录检测 内部
    public static Token loginDetect(Contact contact, Long qq) {
        Token token = userTokensMap.get(qq);
        if(token==null) {
            // 登录检测
            contact.sendMessage("好像还没有登录欸(´。＿。｀)\n私信发送\"登录\"一起来玩吧！");
            return null;
        }
        return token;
    }
}