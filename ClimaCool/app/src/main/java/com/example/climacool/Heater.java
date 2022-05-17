package com.example.climacool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Heater extends AppCompatActivity {

    int temperature = 0;

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "emqx_test";

    //Define a handler with the MQTT broker
    MqttAndroidClient handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heater);

        //catch topic
        Intent received = getIntent();
        String topic = received.getStringExtra("topic");
        String stateTopic = topic + "/state";

        placeTitle(topic);

        Button btnOn = findViewById(R.id.button);
        Button btnOff = findViewById(R.id.button2);
        EditText etTemperature = findViewById(R.id.editTextNumberDecimal);

        //new MQTT connection
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("user1");
        opts.setPassword("user1Pass".toCharArray());

        handler = new MqttAndroidClient(getApplicationContext(), broker, clientId);

        //set callback for Messages
        handler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                if(msg.equals("off")){
                    etTemperature.setEnabled(false);
                    btnOn.setEnabled(true);
                    btnOff.setEnabled(false);
                }
                else if(msg.equals("on")){
                    btnOn.setEnabled(false);
                }
                else if (Character.isDigit(msg.charAt(0))) {
                    temperature = Integer.parseInt(msg);
                    etTemperature.setText(msg);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        try {
            handler.connect(opts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        handler.subscribe(topic + "/+", 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        etTemperature.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    temperature = Integer.parseInt(String.valueOf(etTemperature.getText()));
                    try {
                        handler.publish(topic + "/temperature", Integer.toString(temperature).getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    //handled = true;
                }
                return handled;
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTemperature.setEnabled(true);
                btnOn.setEnabled(false);
                btnOff.setEnabled(true);
                try {
                    handler.publish(stateTopic, "on".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTemperature.setEnabled(false);
                btnOff.setEnabled(false);
                btnOn.setEnabled(true);
                try {
                    handler.publish(stateTopic, "off".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                try {
                    handler.disconnect();
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to disconnect", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

    }

    @Override
    public void finish(){
        super.finish();
        try {
            handler.disconnect(getApplicationContext(), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "failed to disconnect", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "failed to disconnect", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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