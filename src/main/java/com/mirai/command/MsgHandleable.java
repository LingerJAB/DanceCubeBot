package com.mirai.command;


import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 消息处理接口
 */
@FunctionalInterface
public interface MsgHandleable {
    /**
     * 类似于 Consumer<T> 但是定义好了方法参数，通过Lambda表达式传值
     *
     * @param event   消息事件
     * @param contact 消息发送者
     * @param qq      消息发送者的QQ号
     * @param args    消息参数，传入{@code RegexCommand}对象或者没有参数时为null
     */
    void handle(MessageEvent event, Contact contact, long qq, @Nullable String[] args);

}
