package com.main.covis.covid_plot;

public class CovidPlotModel {

    private String msg;
    private String msg2;

    public CovidPlotModel() {}

    public CovidPlotModel(String message){
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
