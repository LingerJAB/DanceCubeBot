package com.mirai.channel;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

// 群通道
public class GroupChannel extends AbstractChannel {

    public static void eventCenter(GroupMessageEvent event) {
        String content = event.getMessage().contentToString();
        Group group = event.getGroup();
//        long groupQq=group.getId();
//        long senderQq = event.getSender().getId();

        switch(content) {
            case "舞立方登录" -> group.sendMessage("私信才可以登录哦( •̀ ω •́ )/");
        }
    }
}