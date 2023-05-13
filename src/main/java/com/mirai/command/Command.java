package com.mirai.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashSet;
import java.util.regex.Pattern;

// 原始指令
public class Command {
    private Pattern regex;  //正则
    private final HashSet<Scope> scopes = new HashSet<>();  //作用范围
    private MsgHandleable globalOnCall;  //作用效果
    private MsgHandleable userOnCall;
    private MsgHandleable groupOnCall;

    protected void setRegex(Pattern regex) {
        this.regex = regex;
    }


    protected void setGlobalOnCall(MsgHandleable onCall) {
        this.globalOnCall = onCall;
        userOnCall = onCall;
        groupOnCall = onCall;
    }

    protected void setUserOnCall(MsgHandleable onCall) {
        this.userOnCall = onCall;
    }

    protected void setGroupOnCall(MsgHandleable onCall) {
        this.groupOnCall = onCall;
    }

    protected final void addScope(Scope scope) {
        scopes.add(scope);
        clearScopes();
    }

    public Pattern getRegex() {
        return regex;
    }

    public HashSet<Scope> getScopes() {
        return scopes;
    }

    public void onCall(Scope scope, MessageEvent event, Contact contact, long qq) {
        //不同情况筛选
        if(scope==Scope.GROUP) {
            groupOnCall.handle(event, contact, qq);
        } else if(scope==Scope.USER) {
            userOnCall.handle(event, contact, qq);
        } else {
            globalOnCall.handle(event, contact, qq);
        }
    }

    private void clearScopes() {
        if(scopes.contains(Scope.GLOBAL) | ((Scope.values().length - scopes.size()==1) & !scopes.contains(Scope.GLOBAL))) {
            scopes.clear();
            scopes.add(Scope.GLOBAL);
        }
    }
}
