package com.mirai.command;

import java.util.regex.Pattern;

public class ArgsCommand extends AbstractCommand {
    public static final Pattern NUMBER = Pattern.compile("\\d+");
    public static final Pattern WORD = Pattern.compile("[0-9a-zA-z]+");
    public static final Pattern CHAR = Pattern.compile("\\S+");
    public static final Pattern ANY = Pattern.compile(".+");

    private String[] prefix;
    private Pattern[] form;

    public String[] getPrefix() {
        return prefix;
    }

    protected void setPrefix(String[] prefix) {
        this.prefix = prefix;
    }

    protected void setForm(Pattern[] form) {
        this.form = form;
    }


    /**
     * 检查格式
     *
     * @param command 需要的指令
     * @param args    传递的参数
     * @return -1为成功，-2为无参数，否则为匹配错误的索引
     */
    public static int checkError(ArgsCommand command, String[] args) {
        if(args == null) return -2;

        if(args.length<command.form.length) return 0;
        Pattern[] patterns = command.form;
        for(int i = 0; i<patterns.length; i++) {
            if(!patterns[i].matcher(args[i]).find()) return i;
        }
        return -1;
    }
}