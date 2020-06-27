package com.voiceassistant.messageView;

import com.voiceassistant.dataBase.MessageEntity;
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }

    public Message(MessageEntity entity) {
        this.text = entity.text;
        this.isSend = entity.isSend==1;
        this.date = new Date(Long.parseLong(entity.date));
    }

}
