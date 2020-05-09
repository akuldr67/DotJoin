package com.example.dotjoin;

import java.util.ArrayList;

public class Chat {
    private ArrayList<Message> Messages;

    public Chat(){
        Messages = new ArrayList<>();
    }

    public Chat(ArrayList<Message> messages) {
        Messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return Messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        Messages = messages;
    }
}
