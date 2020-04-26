package com.main.covis.covid_map;

public interface CovidMapContract {

    interface View {
        void showMessage(String message);
    }

    interface Presenter {
        String getResult();
    }
}
