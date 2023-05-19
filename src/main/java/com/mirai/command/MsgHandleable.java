package com.mirai.command;


import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.Nullable;


@FunctionalInterface
public interface MsgHandleable {
    //类似于 Consumer<T> 但是定义好了方法参数，通过Lambda表达式传值
    void handle(MessageEvent event, Contact contact, long qq, @Nullable String[] args);

}
