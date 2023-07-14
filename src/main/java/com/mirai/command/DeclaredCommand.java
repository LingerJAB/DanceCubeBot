package com.mirai.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注册指令到监听器，
 * 仅注解了 @DeclaredCommand 的 Command 会被放入 Handler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DeclaredCommand {
    //指令名，无调用意义
    String value();
}
