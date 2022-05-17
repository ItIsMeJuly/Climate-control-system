package com.example.climacool;


import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BedroomActivity extends AppCompatActivity {

    private RecyclerView rv;

    // Arraylist for storing data
    private ArrayList<Model> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom);

        Intent intent = getIntent();
        String firstPart = intent.getStringExtra("topic");

        rv = findViewById(R.id.idRVCard);

        Intent heater = new Intent(this, Heater.class);
        Intent airconditioner = new Intent(this, airconditioner.class);
        Intent monitoring = new Intent(this, Monitoring.class);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                heater.putExtra("topic",firstPart + "heater");
                                BedroomActivity.this.startActivity(heater);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 1:
                                airconditioner.putExtra("topic", firstPart + "airconditioner");
                                BedroomActivity.this.startActivity(airconditioner);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 2:
                                monitoring.putExtra("topic", firstPart + "monitoring");
                                BedroomActivity.this.startActivity(monitoring);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                        }
                    }
                }));

        cards = new ArrayList<Model>();
        cards.add(new Model("Heaters", R.drawable.heater));
        cards.add(new Model("Air conditioner", R.drawable.airconditioner));
        cards.add(new Model("Monitoring", R.drawable.monitoring));

        AdapterSubmenu adapter = new AdapterSubmenu(this, cards);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);


        //Toast.makeText(BedroomActivity.this, "Heaters turned on", Toast.LENGTH_SHORT).show();


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
