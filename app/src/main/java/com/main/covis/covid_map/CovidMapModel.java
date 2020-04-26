package com.main.covis.covid_map;

public class CovidMapModel {

    private String msg;

    public CovidMapModel() {}

    public CovidMapModel(String message){
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
