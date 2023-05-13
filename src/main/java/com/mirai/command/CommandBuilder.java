package com.mirai.command;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

// 构造器
public class CommandBuilder {
    private final Command command = new Command();

    public CommandBuilder regex(@NotNull String regex) {
        return regex(regex, true);
    }

    public CommandBuilder regex(String regex, boolean lineOnly) {
        if(regex.isBlank()) throw new RuntimeException("regex不能为空值");
        command.setRegex(Pattern.compile(lineOnly ? "^" + regex + "$" : regex));
        return this;
    }

    public CommandBuilder onCall(Scope scope, @NotNull MsgHandleable onCall) {
        command.addScope(scope);
        if(scope==Scope.GLOBAL) {
            command.setGlobalOnCall(onCall);
        } else {
            if(scope==Scope.USER) {
                command.setUserOnCall(onCall);
            } else if(scope==Scope.GROUP) {
                command.setGroupOnCall(onCall);
            }
        }
        return this;
    }

    public Command build() {
        return command;
    }
}
