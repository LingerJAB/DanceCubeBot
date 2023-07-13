package com.mirai.command;

import com.dancecube.api.Machine;
import com.dancecube.api.PlayerMusic;
import com.dancecube.image.UserInfoImage;
import com.dancecube.info.AccountInfo;
import com.dancecube.info.InfoStatus;
import com.dancecube.info.UserInfo;
import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.MiraiBot;
import com.mirai.config.UserConfigUtils;
import com.tools.HttpUtil;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mirai.config.AbstractConfig.*;

@SuppressWarnings("unused")
public class AllCommands {

    public static HashSet<RegexCommand> regexCommands = new HashSet<>();  //所有正则指令
    public static HashSet<ArgsCommand> argsCommands = new HashSet<>();  //所有参数指令
    public static Token defaultToken = userTokensMap.get(0L);

    // 初始化所有指令
    public static void init() {
        for(Field field : AllCommands.class.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(DeclaredCommand.class)) {
                try {
                    if(field.getType()==RegexCommand.class)
                        regexCommands.add((RegexCommand) field.get(null)); // 获取并保存所有指令
                    else if(field.getType()==ArgsCommand.class) {
                        argsCommands.add(((ArgsCommand) field.get(null)));
                    }
                } catch(IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @DeclaredCommand("清空登录")
    public static final ArgsCommand clearLogin = new ArgsCommandBuilder()
            .prefix("#clearLogin")
            .form(ArgsCommand.WORD)
            .onCall(Scope.USER, (event, contact, qq, args) -> {
                if(args==null) {
                    return;
                }
                // 正在登录检测
                switch(args[0]) {
                    case "all" -> logStatus.clear();
                    case "me" -> contact.sendMessage(logStatus.remove(qq) ? "已清空！" : "未找到登录！");
                }
            }).build();

    @DeclaredCommand("菜单")
    public static final RegexCommand msgMenu = new RegexCommandBuilder()
            .regex("菜单")
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
                String menu = """
                        舞小铃有以下功能哦！
                        1. 登录
                        -登录才能和舞小铃玩！
                        2. 个人信息
                        -查询舞立方资料
                        3. 机台登录 | 扫码
                        -拍照即可扫码舞立方机台！
                        4. 添加指令 [名称]
                        -换个方式查看信息！
                        5. 查找舞立方 [地名]
                        越详细地名越精确！
                        6. [自制谱兑换码]
                        私聊批量兑换好多兑换码！
                        ❤️其它问题请联系铃酱!！""";
                contact.sendMessage(menu);
            }).build();

    @DeclaredCommand("舞立方机器人登录")
    public static final RegexCommand dcLogin = new RegexCommandBuilder()
            .regex("登录|舞立方登录")
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
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
                TokenBuilder builder = new TokenBuilder();
                Image image = HttpUtil.getImageFromURL(builder.getQrcodeUrl(), contact);

                contact.sendMessage(new PlainText("快快用微信扫码，在五分钟内登录上吧~").plus(image));

                logStatus.add(qq);
                Token token = builder.getToken();

                if(token==null) {
                    contact.sendMessage("超时啦~ 请重试一下吧！");
                } else {
                    contact.sendMessage("登录成功啦~(●'◡'●)\n你的ID是：%s".formatted(token.getUserId()));
                    userTokensMap.put(qq, token);  // 重复登录只会覆盖新的token
                    TokenBuilder.tokensToFile(userTokensMap, configPath + "UserTokens.json");
                }
                logStatus.remove(qq);
            }).build();

    @DeclaredCommand("舞立方机台登录")
    public static final RegexCommand machineLogin = new RegexCommandBuilder()
            .regex("机台登录|扫码")
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
                Token token = getToken(contact, qq);
                if(token==null) return;
                MessageChain messageChain = event.getMessage();
                EventChannel<Event> channel = GlobalEventChannel.INSTANCE.parentScope(MiraiBot.INSTANCE);
                CompletableFuture<MessageEvent> future = new CompletableFuture<>();
                channel.subscribeOnce(MessageEvent.class, future::complete);

                contact.sendMessage(new QuoteReply(messageChain).plus(new PlainText("请在3分钟之内发送机台二维码图片哦！\n一定要清楚才好！")));
                SingleMessage message;
                try {
                    MessageChain nextMessage = future.get(3, TimeUnit.MINUTES).getMessage();
                    List<SingleMessage> messageList = nextMessage.stream().filter(m -> m instanceof Image).toList();
                    if(messageList.size()!=1) {
                        contact.sendMessage(new QuoteReply(nextMessage).plus(new PlainText("这个不是图片吧...重新发送“机台登录”吧")));
                    } else {  // 第一个信息
                        message = messageList.get(0);
                        String imageUrl = Image.queryUrl((Image) message);
                        String qrUrl = HttpUtil.qrDecodeTencent(imageUrl);
                        if(qrUrl==null) {  // 若扫码失败
                            contact.sendMessage(new QuoteReply((MessageChain) message).plus(new PlainText("没有扫出来！再试一次吧！")));
                            return;
                        }
                        try(Response response = Machine.qrLogin(token, qrUrl)) {
                            if(response!=null && response.code()==200) {
                                contact.sendMessage("登录成功辣，快来出勤吧！");
                            } else {
                                contact.sendMessage("二维码失效了，换一个试试看吧");
                            }
                        }//401 404
                    }
                } catch(InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch(TimeoutException e) {
                    e.printStackTrace();
                    contact.sendMessage(new QuoteReply(messageChain).plus("超时啦，请重新发送吧~"));
                }
            }).build();

    @DeclaredCommand("个人信息")
    public static final RegexCommand msgUserInfo = new RegexCommandBuilder()
            .regex("个人信息|mydc|mywlf")
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
                Token token = getTokenOrDefault(contact, qq, (con, q) -> {
                    contact.sendMessage("由于不可抗因素，身份过期了💦\n重新私信登录即可恢复💦");
                });
                if(token==null) return;

                InputStream inputStream = UserInfoImage.generate(token, token.getUserId());
                if(inputStream!=null) {
                    Image image = HttpUtil.getImageFromStream(inputStream, contact);
                    contact.sendMessage(image);
                }
            }).build();

    //    @DeclaredCommand("舞立方自制谱兑换")
    public static final RegexCommand gainMusicByCode = new RegexCommandBuilder()
            .regex("[a-zA-Z0-9]{15}", false)
            .onCall(Scope.USER, (event, contact, qq, args) -> {
                Token token = getToken(contact, qq);
                if(token==null) return;

                String message = event.getMessage().contentToString();
                Matcher matcher = Pattern.compile("[a-zA-Z0-9]{15}").matcher(message);

                int i = 0;
                while(matcher.find() & ++i<25) {
                    String code = matcher.group();
                    contact.sendMessage("#%d 小铃在努力兑换 \"%s\" ...".formatted(i, code));
                    Response response = PlayerMusic.gainMusicByCode(token, code);
                    if(response==null) return;
                    if(response.code()==200) {
                        contact.sendMessage("\"" + code + "\"兑换成功啦！快去背包找找吧");
                        response.close();
                        return;
                    }
                    response.close();
                }
                contact.sendMessage("好像都失效了💦💦\n换几个试试吧！");
            })
            .onCall(Scope.GROUP, (event, contact, qq, args) -> {
                Token token = getToken(contact, qq);
                if(token==null) return;

                String message = event.getMessage().contentToString();
                Matcher matcher = Pattern.compile("[a-zA-Z0-9]{15}").matcher(message);

                if(matcher.find()) {
                    String code = matcher.group();
                    contact.sendMessage("检测到了兑换码！小铃在努力兑换 \"%s\" ...".formatted(code));
                    try(Response response = PlayerMusic.gainMusicByCode(token, code)) {
                        if(response==null) return;
                        if(response.code()==200) {
                            contact.sendMessage("\"" + code + "\"兑换成功啦！快去背包找找吧");
                            return;
                        }
                    }
                }
                contact.sendMessage("好像失效了💦💦\n换一个试试吧！");
            }).build();

    //    @DeclaredCommand("个人信息（旧版）")
    public static final RegexCommand msgUserInfoLegacy = new RegexCommandBuilder()
            .regex("个人信息-l|mydc-l")
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
                getToken(contact, qq);
                Token token = userTokensMap.get(qq);


                JavaPluginScheduler scheduler = MiraiBot.INSTANCE.getScheduler();
                UserInfo userInfo;
                AccountInfo accountInfo;
                try {
                    userInfo = scheduler.async(() -> UserInfo.get(token)).get();
                    accountInfo = scheduler.async(() -> AccountInfo.get(token)).get();
                } catch(ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Image image = HttpUtil.getImageFromURL(userInfo.getHeadimgURL(), contact);
                //TODO Gold
                String info = "昵称：%s\n战队：%s\n积分：%d\n金币：%d\n战力：%d\n全国排名：%d".formatted(userInfo.getUserName(), userInfo.getTeamName(), userInfo.getMusicScore(), accountInfo.getGold(), userInfo.getLvRatio(), userInfo.getRankNation());
                contact.sendMessage(image.plus(info));
            }).build();

    @DeclaredCommand("添加指令")
    public static final ArgsCommand addUserInfoCmd = new ArgsCommandBuilder()
            .prefix("添加指令")
            .form(ArgsCommand.CHAR)
            .onCall(Scope.USER, (event, contact, qq, args) -> {
                if(args==null) {
                    return;
                }
                String newPrefix = args[0];
                if(!userInfoCommands.containsKey(qq)) userInfoCommands.put(qq, new HashSet<>());
                userInfoCommands.get(qq).add(newPrefix);
                contact.sendMessage("已添加 \"" + newPrefix + "\" !");
            }).build();

    @DeclaredCommand("删除指令")
    public static final ArgsCommand delUserInfoCmd = new ArgsCommandBuilder()
            .prefix("删除指令")
            .form(ArgsCommand.CHAR)
            .onCall(Scope.USER, (event, contact, qq, args) -> {
                if(args==null) {
                    return;
                }

                String newPrefix = args[0];
                if(!userInfoCommands.containsKey(qq)) userInfoCommands.put(qq, new HashSet<>());
                if(!userInfoCommands.get(qq).contains(newPrefix)) {
                    contact.sendMessage("未找到 \"" + newPrefix + "\" !");
                    return;
                }
                userInfoCommands.get(qq).remove(newPrefix);
                contact.sendMessage("已删除 \"" + newPrefix + "\" !");
                UserConfigUtils.configsToFile(userInfoCommands, configPath + "UserCommands.json");
            }).build();


    @DeclaredCommand("查找舞立方机台")
    public static final ArgsCommand msgMachineList = new ArgsCommandBuilder()
            .prefix("查找舞立方", "查找机台", "舞立方")
            .form(ArgsCommand.CHAR)
            .onCall(Scope.GROUP, (event, contact, qq, args) -> {
                if(args==null) return;

                String region = args[0];
                StringBuilder machineListText = new StringBuilder("\"%s\"的舞立方机台列表：".formatted(region));
                List<Machine> list = Machine.getMachineList(region);
                if(list.size()==0) {
                    contact.sendMessage("在“" + region + "”似乎没有找到舞立方欸...");
                    return;
                }

                int maxCount = Math.min(list.size(), 5);
                for(int i = 0; i<maxCount; i++) {
                    Machine machine = list.get(i);
                    String show = machine.isShow() ? "[⭐秀]" : "";
                    String online = machine.isOnline() ? "🔵在线" : "🔴离线";
                    String singleInfo = "店名：%s%s %s\n地址：%s\n"
                            .formatted(show, machine.getPlaceName(), online, machine.getAddress());
                    machineListText.append("\n").append(singleInfo);
                }
                if(list.size()>5) {
                    contact.sendMessage(machineListText + "⭐刷屏哒咩！群聊只显示5条，更多列表请私聊~");
                } else {
                    contact.sendMessage(machineListText.toString());
                }
            })
            .onCall(Scope.USER, (event, contact, qq, args) -> {
                if(args==null) return;

                String region = args[0];
                StringBuilder machineListText = new StringBuilder("\"%s\"的舞立方机台列表：".formatted(region));
                List<Machine> list = Machine.getMachineList(region);
                if(list.size()==0) {
                    contact.sendMessage("似乎没有找到舞立方欸...");
                    return;
                }

                for(Machine machine : list) {
                    String show = machine.isShow() ? "[⭐秀]" : "";
                    String online = machine.isOnline() ? "🔵在线" : "🔴离线";
                    String singleInfo = "店名：%s%s %s\n地址：%s\n".formatted(show, machine.getPlaceName(), online, machine.getAddress());
                    machineListText.append("\n").append(singleInfo);
                }
                contact.sendMessage(machineListText.toString());
            }).build();

    @DeclaredCommand("查看其它个人信息")
    public static final ArgsCommand msgOthersInfo = new ArgsCommandBuilder()
            .prefix("看看你的", "康康你的", "看看")
            .form(ArgsCommand.NUMBER)
            .onCall(Scope.GLOBAL, (event, contact, qq, args) -> {
                if(args==null) return;
                long num = Long.parseLong(args[0]);
                Token token = getTokenOrDefault(contact, qq, (con, q) -> {
                    contact.sendMessage("由于不可抗因素，身份过期了💦\n重新私信登录即可恢复💦");
                });
                if(token==null) {
                    contact.sendMessage("默认Token异常，请联系铃！");
                    return;
                }

                //判断QQ/ID
                int id;
                UserInfo userInfo = UserInfo.getNull();
                if(num<99_999_999 && num>99_99) { //舞立方ID
                    id = (int) num;
//                    userInfo = UserInfo.get(token, (int) num);
                } else if(userTokensMap.containsKey(num) && num>999_999) { //QQ
                    id = userTokensMap.get(num).getUserId();
                } else {
                    contact.sendMessage("不存在！小铃没有保存！");
                    return;
                }

                userInfo = UserInfo.get(token, id);
                if(userInfo.getStatus()==InfoStatus.NONEXISTENT) {
                    contact.sendMessage("这个账号未保存或不存在！");
                    return;
                }

                //发送图片
                InputStream inputStream = UserInfoImage.generate(token, id);
                if(inputStream!=null) {
                    Image image = HttpUtil.getImageFromStream(inputStream, contact);
                    contact.sendMessage(image);
                }


            }).build();

    @DeclaredCommand("设置默认Token")
    public static final RegexCommand setDefaultToken = new RegexCommandBuilder()
            .regex("#setDefaultToken")
            .onCall(Scope.ADMIN, (event, contact, qq, args) -> {
                contact.sendMessage("请发送 Access Token 和 Refresh Token\n使用换行区分token！");
                EventChannel<Event> channel = GlobalEventChannel.INSTANCE.parentScope(MiraiBot.INSTANCE);
                CompletableFuture<MessageEvent> future = new CompletableFuture<>();
                channel.subscribeOnce(MessageEvent.class, future::complete);

                String accessToken = null;
                String refreshToken = null;
                try {
                    String[] token = future.get(1, TimeUnit.MINUTES).getMessage().contentToString().split("\n");
                    accessToken = token[0];
                    refreshToken = token[1];
                } catch(InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch(TimeoutException e) {
                    e.printStackTrace();
                    contact.sendMessage("超时了，请重新设置");
                }
                Token token = new Token(0, accessToken, refreshToken, 0);
                if(token.isAvailable()) {
                    defaultToken = token;
                    userTokensMap.put(0L, token);
                    TokenBuilder.tokensToFile(userTokensMap, configPath + "UserTokens.json");
                    contact.sendMessage("默认Token设置成功：\n\n" + defaultToken);
                } else {
                    contact.sendMessage("默认Token设置失败：已无效");
                }
            }).build();


    /////////////////////////////////////////////////////////////////////////////////
    public static Token getToken(Contact contact, Long qq) {
        Token token = userTokensMap.get(qq);
        if(token==null || token.isAvailable()) {
            // 登录检测
            contact.sendMessage("好像还没有登录欸(´。＿。｀)\n私信发送\"登录\"一起来玩吧！");
            userInfoCommands.put(qq, new HashSet<>());
            return null;
        }
        return token;
    }

    //TODO 先重写UserInfo & AccountInfo  (UserId==0)
    //有了onNull就不要return null了吧...如何处理呢？
    public static Token getTokenOrDefault(Contact contact, long qq, @Nullable BiConsumer<Contact, Long> onNull) {
        Token token = userTokensMap.get(qq);
        // 默认返回备份
        if(token!=null) {
            if(token.isAvailable()) return token; //默认token有效性
                //返回默认token 默认的都null那就登录吧 :(
            else if(defaultToken!=null && defaultToken.isAvailable()) return defaultToken;
            else throw new RuntimeException("未设置 defaultToken ！！");
        }
        if(onNull!=null) onNull.accept(contact, qq);

        //没有登录（本地保存记录）就 onNull.accept();
        return null;
    }

}
