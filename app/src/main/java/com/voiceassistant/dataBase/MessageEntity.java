package com.voiceassistant.dataBase;

import com.voiceassistant.messageView.Message;

public class MessageEntity {
    public String text;
    public String date;
    public int isSend;
    public MessageEntity(String text, String date, int isSend){
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }
    public MessageEntity(Message message){
        this.text = message.text;
        this.isSend =  message.isSend? 1:0;
        this.date = message.date.getTime() + "";
    }

}
