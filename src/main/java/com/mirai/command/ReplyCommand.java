package com.mirai.command;

public class ReplyCommand extends AbstractCommand {
    private String[] recvMessages;
    private String[] replyMessages;

    public void setReplyMessages(String[] replyMessages) {
        this.replyMessages = replyMessages;
    }

    public void setRecvMessages(String[] recvMessages) {
        this.recvMessages = recvMessages;
    }
}
