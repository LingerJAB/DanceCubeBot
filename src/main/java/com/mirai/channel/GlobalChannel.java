package com.mirai.channel;

import com.dancecube.api.MachineList;
import com.dancecube.api.UserInfo;
import com.dancecube.token.Token;
import com.mirai.HttpUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.IOException;
import java.net.URL;

// 不过滤通道
public class GlobalChannel extends AbstractChannel {
    public static void eventCenter(MessageEvent event) {
        String content = event.getMessage().contentToString();
        long qq = event.getSender().getId();
        Contact contact = event.getSubject();

        // 发送消息检测
        switch(content) {
            case "个人信息" -> msgUserInfo(contact, qq);
            case "机台检测" -> msgMachineList(contact);
        }

    }

    // 个人信息
    public static void msgUserInfo(Contact contact, long qq) {
        Token token = userMap.get(qq);
        if(token==null) {
            // 登录检测
            contact.sendMessage("好像还没有登录欸(´。＿。｀)\n私信发送\"舞立方登录\"一起来玩吧！");
            return;
        }

        token.refresh();
        UserInfo user = UserInfo.get(token);
        ExternalResource ex;

        try {
            ex = HttpUtils.getExResByURL(new URL(user.HeadimgURL));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        Image image = ExternalResource.uploadAsImage(ex, contact);

        String info = "昵称：%s\n战队：%s\n积分：%d\n全国排名：%d"
                .formatted(user.UserName, user.TeamName, user.MusicScore, user.RankNation);

        contact.sendMessage(image.plus(info));
    }

    // 机台检测
    public static void msgMachineList(Contact contact) {
        StringBuilder list = new StringBuilder("舞立方机台列表：");
        for(MachineList machine : MachineList.get()) {
            String online = machine.Online ? "在线" : "离线";
            String singleInfo = "店名：%s *%s\n地址：%s"
                    .formatted(machine.PlaceName, online, machine.Address);
            list.append("\n\n").append(singleInfo);
        }
        contact.sendMessage(list.toString());
    }

}
