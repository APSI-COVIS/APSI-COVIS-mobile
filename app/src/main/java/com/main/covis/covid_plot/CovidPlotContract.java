package com.main.covis.covid_plot;

public interface CovidPlotContract {

    interface View {
        void showMessage(String message);
    }

    interface Presenter {
        String getResult();
    }
}
