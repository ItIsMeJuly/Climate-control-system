package com.example.climacool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;

public class airpurifier extends AppCompatActivity {

    private Button spray;

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "emqx_test";
    MqttAndroidClient handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airpurifier);

        //catch topic
        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        //new MQTT connection
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("user1");
        opts.setPassword("user1Pass".toCharArray());

        handler = new MqttAndroidClient(getApplicationContext(), broker, "MobileApp");

        try {
            handler.connect(opts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        spray = findViewById(R.id.button10);

        spray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    handler.publish(topic + "/spray", "press".getBytes(), 1, false);
                    Toast.makeText(getApplicationContext(), "wait 3 seconds", Toast.LENGTH_LONG).show();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}