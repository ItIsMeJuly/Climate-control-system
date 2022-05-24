package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class Office extends AppCompatActivity {

    private RecyclerView rv;

    private ArrayList<Model> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);

        rv = findViewById(R.id.idOfficeRV);

        //catch topic
        Intent received = getIntent();
        String []data = received.getStringArrayExtra("topic");

        Intent heater = new Intent(this, Heater.class);
        Intent lights = new Intent(this, lights.class);
        Intent airconditioner = new Intent(this, airconditioner.class);
        Intent monitoring = new Intent(this, Monitoring.class);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                heater.putExtra("topic",new String[]{data[0] + "heater", data[1]});
                                Office.this.startActivity(heater);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 1:
                                lights.putExtra("topic", new String[]{data[0] + "light", data[1]});
                                Office.this.startActivity(lights);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 2:
                                airconditioner.putExtra("topic", new String[]{data[0] + "airconditioner", data[1]});
                                Office.this.startActivity(airconditioner);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 3:
                                monitoring.putExtra("topic",new String[]{data[0] + "monitoring", data[1]});
                                Office.this.startActivity(monitoring);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                        }
                    }
                }));

        cards = new ArrayList<Model>();
        cards.add(new Model("Heater", R.drawable.heater));
        cards.add(new Model("Light system", R.drawable.lights));
        cards.add(new Model("Air conditioning", R.drawable.airconditioner));
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