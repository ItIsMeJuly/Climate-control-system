package com.example.climacool;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;

import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class Monitoring extends AppCompatActivity {

    MqttConnectionHandler handler = new MqttConnectionHandler(
            "087086635ca64d93b3c6d5b0207bc124.s2.eu.hivemq.cloud",
            "user2",
            "user2Pass"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        placeTitle(topic);

        handler.setTopic(topic + "/+");
        handler.subscribe();

        EditText temp = findViewById(R.id.editTextNumber2);
        EditText humid = findViewById(R.id.editTextNumber);
        EditText co2 = findViewById(R.id.editTextNumber3);
        EditText meth = findViewById(R.id.editTextNumber4);


        handler.getClient().toAsync().publishes(MqttGlobalPublishFilter.ALL, publish -> {
            if(publish.getPayload().isPresent()) {
                CharBuffer result = UTF_8.decode(publish.getPayload().get());
                if (Character.isDigit(result.charAt(0))) {
                    if(String.valueOf(publish.getTopic()).equals(topic + "/temp")) {
                        temp.post(new Runnable() {
                            @Override
                            public void run() {
                                temp.setText(result.toString());
                            }
                        });
                    }
                    else if(String.valueOf(publish.getTopic()).equals(topic + "/humid")){
                        humid.post(new Runnable() {
                            @Override
                            public void run() {
                                humid.setText(result.toString());
                            }
                        });
                    }
                    else if(String.valueOf(publish.getTopic()).equals(topic + "/CO2")){
                        co2.post(new Runnable() {
                            @Override
                            public void run() {
                                co2.setText(result.toString());
                            }
                        });
                    }
                    else if(String.valueOf(publish.getTopic()).equals(topic + "/meth")){
                        meth.post(new Runnable() {
                            @Override
                            public void run() {
                                meth.setText(result.toString());
                            }
                        });
                    }
                }
            }
        });

    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            finish();
            handler.disconnect();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    @Override
    public void finish(){
        super.finish();
        handler.disconnect();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    void placeTitle(String topic) {
        if (topic.contains("bedroom")) {
            setTitle("Bedroom");
        } else if (topic.contains("bathroom")) {
            setTitle("Bathroom");
        } else if (topic.contains("livingroom")) {
            setTitle("Living room");
        } else if (topic.contains("office")) {
            setTitle("Office");
        } else {
            setTitle("Kitchen");
        }
    }
}