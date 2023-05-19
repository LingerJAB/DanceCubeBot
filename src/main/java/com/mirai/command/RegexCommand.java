package com.mirai.command;

import java.util.regex.Pattern;

// 原始指令
public class RegexCommand extends CallableCommand {
    private Pattern regex;  //正则

    protected RegexCommand() {
    }

    protected void setRegex(Pattern regex) {
        this.regex = regex;
    }

    public Pattern getRegex() {
        return regex;
    }

}
