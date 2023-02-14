package com.mirai;

import com.dancecube.token.TokenBuilder;
import com.mirai.event.MainHandler;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Timer;
import java.util.TimerTask;

import static com.mirai.event.AbstractHandler.rootPath;
import static com.mirai.event.AbstractHandler.userTokensMap;

public final class MiraiBot extends JavaPlugin {
    public static final MiraiBot INSTANCE = new MiraiBot();

    private MiraiBot() {
        super(new JvmPluginDescriptionBuilder("com.mirai.lin", "0.1.0").name("MiraiBot").author("Lin").build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        EventChannel<Event> channel = GlobalEventChannel.INSTANCE.context(this.getCoroutineContext());

        //定时器
        refreshTokensTimer();

        channel.subscribeAlways(MessageEvent.class, MainHandler::eventCenter);


    }

    public static void refreshTokensTimer() {
        long period = 86400 * 1000; //一天
        String path = rootPath + "/DcConfig/UserToken.json";
        userTokensMap = TokenBuilder.tokensFromFile(path);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                userTokensMap.forEach((qq, token) -> token.refresh());
                TokenBuilder.tokensToFile(userTokensMap, path);
            }
        };

        new Timer().schedule(task, 0, period);
    }
}

