package com.example.climacool;

//import static com.example.climacool.R.id.textView3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;

    // Arraylist for storing data
    private ArrayList<Model> modelArrayList;

    //public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.idRVCourse);

        Intent receive = getIntent();
        String internetFlag = receive.getStringExtra("internetFlag");

        Intent bedroomIntent = new Intent(MainActivity.this, BedroomActivity.class);
        Intent bathroomIntent = new Intent(MainActivity.this, Bathroom.class);
        Intent livingroomIntent = new Intent(MainActivity.this, Livingroom.class);
        Intent officeIntent = new Intent(MainActivity.this, Office.class);
        Intent kitchenIntent = new Intent(MainActivity.this, Kitchen.class);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                bedroomIntent.putExtra("topic", new String[]{"bedroom/", internetFlag});
                                MainActivity.this.startActivity(bedroomIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 1:
                                bathroomIntent.putExtra("topic", new String[]{"bathroom/", internetFlag});
                                MainActivity.this.startActivity(bathroomIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 2:
                                livingroomIntent.putExtra("topic", new String[]{"livingroom/", internetFlag});
                                MainActivity.this.startActivity(livingroomIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 3:
                                officeIntent.putExtra("topic", new String[]{"office/", internetFlag});
                                MainActivity.this.startActivity(officeIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                            case 4:
                                kitchenIntent.putExtra("topic", new String[]{"kitchen/", internetFlag});
                                MainActivity.this.startActivity(kitchenIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                break;
                        }
                    }
                }));

        modelArrayList = new ArrayList<>();
        modelArrayList.add(new Model("Bedroom",  R.drawable.bedroom));
        modelArrayList.add(new Model("Bathroom",  R.drawable.bathroom));
        modelArrayList.add(new Model("Living room",  R.drawable.livingroom));
        modelArrayList.add(new Model("Office",  R.drawable.office));
        modelArrayList.add(new Model("Kitchen",  R.drawable.kitchen));

        // we are initializing our adapter class and passing our arraylist to it.
        Adapter adapter = new Adapter(this, modelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

    }
}