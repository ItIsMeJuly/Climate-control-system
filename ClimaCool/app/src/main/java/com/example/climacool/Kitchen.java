package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class Kitchen extends AppCompatActivity {

    private RecyclerView rv;

    private ArrayList<Model> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        rv = findViewById(R.id.idKitchenRV);

        Intent received = getIntent();
        String firstPart = received.getStringExtra("topic");

        Intent vent = new Intent(this, Ventilation.class);
        Intent monitoring = new Intent(this, Monitoring.class);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                vent.putExtra("topic",firstPart + "fan");
                                Kitchen.this.startActivity(vent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 1:
                                monitoring.putExtra("topic",firstPart + "monitoring");
                                Kitchen.this.startActivity(monitoring);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                        }
                    }
                }));

        cards = new ArrayList<Model>();
        cards.add(new Model("Fans", R.drawable.ventilation));
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
    //methods
}