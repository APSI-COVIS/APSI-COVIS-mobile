package com.main.covis.covid_plot;

import java.util.Date;
import java.util.List;

public class CovidPlotModel {

    public class CovidData{
        private List<Country> countries;
    }
    public class Country{
        private String name;
        private String code;
        List<CovidDateData> covidDatesData;
    }
    public class CovidDateData{
        private Date date;
        private Integer infectedNr;
        private Integer curedNr;
        private Integer deadNr;
    }

    private String msg;

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
