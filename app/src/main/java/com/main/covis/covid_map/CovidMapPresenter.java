package com.main.covis.covid_map;

public class CovidMapPresenter implements CovidMapContract.Presenter {

    private CovidMapModel covidData;

    public CovidMapPresenter(){
        CovidMapModel newCovidData = new CovidMapModel("Wszyscy umrzemy");
        this.covidData = newCovidData;
    }
    @Override
    public String getResult(){
        String message = covidData.getMsg();
        return message;
    }


}
