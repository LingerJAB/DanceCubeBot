package com.mirai.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// 仅注解了的 Command 会被放入 Handler
public @interface DeclaredCommand {
    //指令名，无调用意义
    String value();
}
