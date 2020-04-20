package com.main.covis.presenter;

import com.main.covis.contract.ContractCovid;
import com.main.covis.model.CovidData;

public class SimplePresenter implements ContractCovid.Presenter {

    private CovidData covidData;

    public SimplePresenter(){
        CovidData newCovidData = new CovidData("Wszyscy umrzemy");
        this.covidData = newCovidData;
    }
    @Override
    public String getResult(){
        String message = covidData.getMsg();
        return message;
    }


}
