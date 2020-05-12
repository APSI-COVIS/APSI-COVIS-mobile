package com.main.covis.main;

public class MainModel {
    private String msg;

    public MainModel() {}

    public MainModel(String message){
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
