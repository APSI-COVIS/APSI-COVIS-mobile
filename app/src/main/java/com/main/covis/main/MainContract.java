package com.main.covis.main;

public class MainContract {
    interface View {
        void showMessage(String message);
    }

    interface Presenter {
        String getResult();
    }
}
