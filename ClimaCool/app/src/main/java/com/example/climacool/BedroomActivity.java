package com.example.climacool;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
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

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

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
    /*
         static void sendBtnPress(String topic){
            final String host = "e91319399f09434ebbe85cb453c78b53.s2.eu.hivemq.cloud";
            final String username = "user2";
            final String password = "user2Pass";

            final Mqtt5BlockingClient client = MqttClient.builder()
                    .useMqttVersion5()
                    .serverHost(host)
                    .serverPort(8884)
                    .sslWithDefaultConfig()
                    .webSocketConfig()
                    .serverPath("mqtt")
                    .applyWebSocketConfig()
                    .buildBlocking();

            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .send();
            Log.d("", "connected");

            client.subscribeWith()
                    .topicFilter("my/test/topic3")
                    .qos(MqttQos.EXACTLY_ONCE)
                    .send();
            Log.d("", "subscribed");

            client.toAsync().publishes(ALL, publish -> {
                Log.d("", "Received message: " + publish.getTopic() + " -> " + UTF_8.decode(publish.getPayload().get()));

                client.disconnect();
            });

        /*client.publishWith()
                .topic("my/test/topic")
                .payload(UTF_8.encode("Hello"))
                .qos(MqttQos.EXACTLY_ONCE)
                .send();*/
