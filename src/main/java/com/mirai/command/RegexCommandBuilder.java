package com.mirai.command;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

// 构造器
public class RegexCommandBuilder {
    private final RegexCommand command = new RegexCommand();

    public RegexCommandBuilder regex(@NotNull String regex) {
        return regex(regex, true);
    }

    public RegexCommandBuilder regex(String regex, boolean lineOnly) {
        if(regex.isBlank()) throw new RuntimeException("regex不能为空值");
        command.setRegex(Pattern.compile(lineOnly ? "^" + regex + "$" : regex));
        return this;
    }

    // TODO lineOnly -> ^(...|...)$
    public RegexCommandBuilder multiStrings(String... strings) {
        if(strings.length<1) throw new RuntimeException("regex不能为空值");
        StringBuilder builder = new StringBuilder();
        for(String string : strings) {
            builder.append('|').append('^').append(string).append('$');
        }
        builder.deleteCharAt(0);
        command.setRegex(Pattern.compile(builder.toString()));
        return this;
    }

    public RegexCommandBuilder onCall(Scope scope, @NotNull MsgHandleable onCall) {
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

    public RegexCommand build() {
        return command;
    }
}
