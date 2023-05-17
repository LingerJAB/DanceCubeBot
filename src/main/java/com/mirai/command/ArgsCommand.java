package com.mirai.command;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ArgsCommand extends AbstractCommand {
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private static final Pattern WORD = Pattern.compile("[0-9a-zA-z]+");
    private static final Pattern CHAR = Pattern.compile("\\S+");

    private String prefix;
    private ArrayList<String> args;
    private ArrayList<Pattern> form;
    private MsgHandleable onCall;

    private Consumer<String> testOnCall;

    public void setForm(@NotNull List<Pattern> form) {
        this.form = new ArrayList<>(form);

    }


    /**
     * 检查格式
     *
     * @param command 需要的指令
     * @param args    传递的参数
     * @return -1为成功，否则为匹配错误的索引
     */
    public static int checkError(ArgsCommand command, ArrayList<String> args) {
        ArrayList<Pattern> list = command.form;
        for(int i = 0; i<list.size(); i++) {
            if(!list.get(i).matcher(args.get(i)).find()) return i;
        }

        return -1;
    }

    @Test
    void Test() {
        String cmd = "/print   \n2862125721\n\n 4";

        ArrayList<String> args = new ArrayList<>(List.of(cmd.trim().split("\\s+")));
        args.remove(0);
        System.out.println("args: " + args);

        //-----------------------------

        ArgsCommand command = new ArgsCommand();
        command.prefix = "/give";
//        command.args = args;
        command.setForm(List.of(NUMBER, CHAR));
        command.testOnCall = System.out::println;

        if(checkError(command, args)<0) {
            command.testOnCall.accept(args.toString());
        }

    }

}
