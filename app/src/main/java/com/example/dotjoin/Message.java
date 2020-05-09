package com.example.dotjoin;

public class Message {
    private String SenderName;
    private String Message;
    private String SenderDeviceToken;

    public Message(String senderName, String message, String senderDeviceToken) {
        SenderName = senderName;
        Message = message;
        SenderDeviceToken = senderDeviceToken;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSenderDeviceToken() {
        return SenderDeviceToken;
    }

    public void setSenderDeviceToken(String senderDeviceToken) {
        SenderDeviceToken = senderDeviceToken;
    }
}
