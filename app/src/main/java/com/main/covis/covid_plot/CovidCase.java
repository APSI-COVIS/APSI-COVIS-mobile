package com.main.covis.covid_plot;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CovidCase {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("cases")
    @Expose
    private int cases;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

}
