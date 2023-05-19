package com.mirai.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ArgsCommand extends CallableCommand {
    public static final Pattern NUMBER = Pattern.compile("\\d+");
    public static final Pattern WORD = Pattern.compile("[0-9a-zA-z]+");
    public static final Pattern CHAR = Pattern.compile("\\S+");

    private String prefix;
    private Pattern[] form;

//    private Consumer<String> testOnCall;

    public String getPrefix() {
        return prefix;
    }

    protected void setPrefix(String prefix) {
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
     * @return -1为成功，否则为匹配错误的索引
     */
    public static int checkError(ArgsCommand command, String[] args) {
        Pattern[] list = command.form;
        for(int i = 0; i<list.length; i++) {
            if(!list[i].matcher(args[i]).find()) return i;
        }

        return -1;
    }

    @Test
    void Test() {
        String cmd = "";
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(cmd.split("\\s+")));
        System.out.println("size:" + strings.size());
        System.out.println(strings);
        System.out.println(new ArrayList<>(null).size());


    }

}
