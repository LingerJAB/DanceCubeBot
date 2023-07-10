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
            switch(scope) {
                case USER -> command.setUserOnCall(onCall);
                case GROUP -> command.setGroupOnCall(onCall);
                case ADMIN -> command.setAdminOnCall(onCall);
            }
        }
        return this;
    }

    public ArgsCommand build() {
        return command;
    }
}
