package com.mirai;

import com.dancecube.token.Token;
import com.dancecube.token.TokenBuilder;
import com.mirai.event.MainHandler;
import com.mirai.task.SchedulerTask;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.mirai.config.AbstractConfig.configPath;
import static com.mirai.config.AbstractConfig.userTokensMap;

public final class MiraiBot extends JavaPlugin {
    public static final MiraiBot INSTANCE = new MiraiBot();
    String path = configPath + "UserTokens.json";

    private MiraiBot() {
        super(JvmPluginDescription.loadFromResource("plugin.yml", MiraiBot.class.getClassLoader()));
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        super.onLoad($this$onLoad);
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        EventChannel<Event> channel = GlobalEventChannel.INSTANCE
                .parentScope(MiraiBot.INSTANCE)
                .context(this.getCoroutineContext());

        // 输出加载Token
        onLoadToken();

        // Token刷新器
        SchedulerTask.autoRefreshToken();


        // 监听器
        channel.subscribeAlways(MessageEvent.class, MainHandler::eventCenter);

    }

    @Override
    public void onDisable() {
        // 保存Tokens
        TokenBuilder.tokensToFile(userTokensMap, configPath + "UserTokens.json");
        System.out.printf("保存成功！共%d条%n", userTokensMap.size());
    }

    @Deprecated
    public void refreshTokensTimer() {
        long period = 86400 * 500; //半天

        JavaPluginScheduler scheduler = MiraiBot.INSTANCE.getScheduler();
        userTokensMap = TokenBuilder.tokensFromFile(path);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Token defaultToken = userTokensMap.get(0L);
                if(defaultToken==null) {
                    userTokensMap.forEach((qq, token) ->
                            scheduler.async(() -> {
                                if(token.checkAvailable()) token.refresh();
                            }));
                } else {
                    userTokensMap.forEach((qq, token) ->
                            scheduler.async(() -> {
                                // 默认token不为用户token
                                if(token.checkAvailable() &
                                        !defaultToken.getAccessToken().equals(token.getAccessToken()))
                                    token.refresh();
                            }));

                }
                TokenBuilder.tokensToFile(userTokensMap, path);
                System.out.println(new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date()) + ": 今日已刷新token");
            }
        };

        new Timer().schedule(task, 0, 86400);
    }

    public void onLoadToken() {
        StringBuilder sb = new StringBuilder();
        // 导入Token
        userTokensMap = Objects.requireNonNullElse(
                TokenBuilder.tokensFromFile(configPath + "UserTokens.json"),
                new HashMap<>());

        for(Map.Entry<Long, Token> entry : userTokensMap.entrySet()) {
            Long qq = entry.getKey();
            Token token = entry.getValue();
            sb.append("\nqq: %d , id: %s;".formatted(qq, token.getUserId()));
        }
        Logger.getGlobal().info(("刷新加载成功！共%d条".formatted(userTokensMap.size()) + sb));
    }
}

