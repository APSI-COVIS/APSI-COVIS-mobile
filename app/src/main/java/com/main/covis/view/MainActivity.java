package com.main.covis.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.main.covis.R;
import com.main.covis.contract.ContractCovid;
import com.main.covis.model.CovidData;
import com.main.covis.presenter.SimplePresenter;

public class MainActivity extends AppCompatActivity implements ContractCovid.View {

    private SimplePresenter presenter;
    TextView messageTV;
    Button messageBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTV = findViewById(R.id.helloTextView);
        messageBT = findViewById(R.id.helloButton);
        presenter = new SimplePresenter();
        messageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage(presenter.getResult());
            }
        });

    }

    @Override
    public void showMessage(String message){
        messageTV.setText(message);
    }
}
