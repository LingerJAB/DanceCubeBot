package com.mirai;

import com.dancecube.token.TokenBuilder;
import com.mirai.event.MainHandler;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Timer;
import java.util.TimerTask;

import static com.mirai.config.AbstractConfig.configPath;
import static com.mirai.config.AbstractConfig.userTokensMap;

public final class MiraiBot extends JavaPlugin {
    public static final MiraiBot INSTANCE = new MiraiBot();

    private MiraiBot() {
        super(new JvmPluginDescriptionBuilder("com.mirai.lin", "0.1.0")
                .name("MiraiBot")
                .author("Lin")
                .build());

    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        EventChannel<Event> channel = GlobalEventChannel.INSTANCE
                .parentScope(MiraiBot.INSTANCE)
                .context(this.getCoroutineContext());

        //定时器
        refreshTokensTimer();

        //加载 DcConfig
//        MainHandler.loadTokens();
        channel.subscribeAlways(MessageEvent.class, MainHandler::eventCenter);

    }


    public void refreshTokensTimer() {
        long period = 86400 * 500; //半天
//        long period = 5000; //5s

        JavaPluginScheduler scheduler = MiraiBot.INSTANCE.getScheduler();
        String path = configPath + "UserTokens.json";
        userTokensMap = TokenBuilder.tokensFromFile(path);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                userTokensMap.forEach((qq, token) -> {
                    scheduler.async(() -> {
                        if(token.isAvailable()) token.refresh(true);
                    });
                });
                TokenBuilder.tokensToFile(userTokensMap, path);
                System.out.println(System.currentTimeMillis() + ":今日已刷新token");
            }
        };

        new Timer().schedule(task, 5000, period);
    }
}
