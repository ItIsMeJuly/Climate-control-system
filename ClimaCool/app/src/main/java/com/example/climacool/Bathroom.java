package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class Bathroom extends AppCompatActivity {

    private RecyclerView rv;

    // Arraylist for storing data
    private ArrayList<Model> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom);

        //catch topic
        Intent received = getIntent();
        String firstPart = received.getStringExtra("topic");

        rv = findViewById(R.id.idBarhroomRV);

        Intent airpurifier = new Intent(this, airpurifier.class);
        Intent monitoring = new Intent(this, Monitoring.class);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                airpurifier.putExtra("topic",firstPart + "air_purifier");
                                Bathroom.this.startActivity(airpurifier);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 1:
                                monitoring.putExtra("topic", firstPart + "monitoring");
                                Bathroom.this.startActivity(monitoring);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                        }
                    }
                }));

        cards = new ArrayList<Model>();
        cards.add(new Model("Ventilation", R.drawable.ventilation));
        cards.add(new Model("Monitoring", R.drawable.monitoring));


        AdapterSubmenu adapter = new AdapterSubmenu(this, cards);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);


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