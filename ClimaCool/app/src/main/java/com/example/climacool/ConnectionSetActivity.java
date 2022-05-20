package com.example.climacool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConnectionSetActivity extends AppCompatActivity {


    private Button btnLAN;
    private Button btnWAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_set);

        btnLAN = findViewById(R.id.button11);
        btnWAN = findViewById(R.id.button12);

        Intent mainPage = new Intent(this, MainActivity.class);


        btnLAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPage.putExtra("internetFlag", "no");
                startActivity(mainPage);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        btnWAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPage.putExtra("internetFlag", "yes");
                startActivity(mainPage);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}