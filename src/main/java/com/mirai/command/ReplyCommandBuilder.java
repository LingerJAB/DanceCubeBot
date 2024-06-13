package com.mirai.command;

import org.jetbrains.annotations.NotNull;

public class ReplyCommandBuilder {
    private final ReplyCommand command = new ReplyCommand();

    public ReplyCommandBuilder replyMessages(String... strings) {
        command.setRecvMessages(strings);
        return this;
    }

    public ReplyCommandBuilder recvMessages(String... strings) {
        command.setRecvMessages(strings);
        return this;
    }

    public ReplyCommandBuilder onCall(Scope scope, @NotNull MsgHandleable onCall) {
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

    public ReplyCommand build() {
        return command;
    }
}
