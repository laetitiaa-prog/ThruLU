package com.example.myapplication;

public class MessageModel {
    public static final String USER = "user";
    public static final String BOT = "bot";
    String message,viewType;

    public MessageModel(String message, String viewType){
        this.message = message;
        this.viewType = viewType;
    }

    public MessageModel(){



    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
