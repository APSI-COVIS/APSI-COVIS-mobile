package com.main.covis.main;



public class MainPresenter implements MainContract.Presenter {
    private MainModel covidData;

    public MainPresenter(){
        MainModel newCovidData = new MainModel("Wszyscy umrzemy");
        this.covidData = newCovidData;
    }
    @Override
    public String getResult(){
        String message = covidData.getMsg();
        return message;
    }
}
