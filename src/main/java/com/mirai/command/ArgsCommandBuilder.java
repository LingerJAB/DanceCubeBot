package com.mirai.command;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ArgsCommandBuilder {
    ArgsCommand command = new ArgsCommand();

    public ArgsCommandBuilder prefix(String... prefix) {
        command.setPrefix(prefix);
        return this;
    }

    public ArgsCommandBuilder form(Pattern... patterns) {
        command.setForm(patterns);
        return this;
    }


    public ArgsCommandBuilder onCall(Scope scope, @NotNull MsgHandleable onCall) {
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

    public ArgsCommand build() {
        return command;
    }
}
