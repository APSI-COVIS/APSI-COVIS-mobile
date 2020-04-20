package com.main.covis.contract;

public interface ContractCovid {

    interface View {
        void showMessage(String message);
    }

    interface Presenter {
        String getResult();
    }
}
