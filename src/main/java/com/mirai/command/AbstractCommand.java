package com.mirai.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashSet;

public abstract class AbstractCommand {

    final HashSet<Scope> scopes = new HashSet<>();  //作用范围
    MsgHandleable globalOnCall;  //作用效果
    MsgHandleable userOnCall;
    MsgHandleable groupOnCall;


    protected void setGlobalOnCall(MsgHandleable onCall) {
        globalOnCall = onCall;
        userOnCall = onCall;
        groupOnCall = onCall;
    }


    public void onCall(Scope scope, MessageEvent event, Contact contact, long qq, String[] args) {

        //不同情况筛选
        if(scope==Scope.GROUP) {
            groupOnCall.handle(event, contact, qq, args);
        } else if(scope==Scope.USER) {
            userOnCall.handle(event, contact, qq, args);
        } else {
            globalOnCall.handle(event, contact, qq, args);
        }
    }

    protected void setUserOnCall(MsgHandleable onCall) {
        this.userOnCall = onCall;
    }

    protected void setGroupOnCall(MsgHandleable onCall) {
        this.groupOnCall = onCall;
    }

    public HashSet<Scope> getScopes() {
        return scopes;
    }

    protected final void addScope(Scope scope) {
        scopes.add(scope);
//        clearScopes();
    }

//    void clearScopes() {
//        if(scopes.contains(Scope.GLOBAL) | ((Scope.values().length - scopes.size()==1) & !scopes.contains(Scope.GLOBAL))) {
//            scopes.clear();
//            scopes.add(Scope.GLOBAL);
//        }
//    }
}
