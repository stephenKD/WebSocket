package com.stephen.websocket.adapter;

public class ChatAppMsgDTO {
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    // Message content.
    private String msgContent;

    // Message type.
    private String msgType;

    public ChatAppMsgDTO(String msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
