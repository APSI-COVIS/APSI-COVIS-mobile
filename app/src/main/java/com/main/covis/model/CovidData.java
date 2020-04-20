package com.main.covis.model;

public class CovidData {

    private String msg;

    public CovidData() {}

    public CovidData(String message){
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
