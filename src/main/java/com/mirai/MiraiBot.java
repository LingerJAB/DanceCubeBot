package com.mirai;

import com.mirai.channel.FriendChannel;
import com.mirai.channel.GlobalChannel;
import com.mirai.channel.GroupChannel;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

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

        EventChannel<Event> channel = GlobalEventChannel.INSTANCE.context(this.getCoroutineContext());


        //全局通道
        channel.subscribeAlways(MessageEvent.class, GlobalChannel::eventCenter);

        //群组通道
        channel.subscribeAlways(GroupMessageEvent.class, GroupChannel::eventCenter);

        //好友通道
        channel.subscribeAlways(FriendMessageEvent.class, FriendChannel::eventCenter);

    }
}