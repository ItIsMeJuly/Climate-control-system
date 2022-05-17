package com.example.climacool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

public class Monitoring extends AppCompatActivity {

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "emqx_test";

    //initialize a handler with the MQTT broker
    MqttAndroidClient handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        Intent received = getIntent();
        String mainTopic = received.getStringExtra("topic");

        placeTitle(mainTopic);

        EditText temp = findViewById(R.id.editTextNumber2);
        EditText humid = findViewById(R.id.editTextNumber);
        EditText co2 = findViewById(R.id.editTextNumber3);
        EditText meth = findViewById(R.id.editTextNumber4);


        //new MQTT connection
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("user1");
        opts.setPassword("user1Pass".toCharArray());

        handler = new MqttAndroidClient(getApplicationContext(), broker, "MobileApp");
        handler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Log.d("msg", msg);
                if (Character.isDigit(msg.charAt(0))) {
                    if(topic.equals(mainTopic + "/temperature")) {
                        temp.post(new Runnable() {
                            @Override
                            public void run() {
                                temp.setText(msg);
                            }
                        });
                    }
                    else if(topic.equals(mainTopic + "/humidity")){
                        humid.post(new Runnable() {
                            @Override
                            public void run() {
                                humid.setText(msg);
                            }
                        });
                    }
                    else if(topic.equals(mainTopic + "/CO2")){
                        co2.post(new Runnable() {
                            @Override
                            public void run() {
                                co2.setText(msg);
                            }
                        });
                    }
                    else if(topic.equals(mainTopic + "/methane")){
                        meth.post(new Runnable() {
                            @Override
                            public void run() {
                                meth.setText(msg);
                            }
                        });
                    }
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
                        handler.subscribe(mainTopic + "/+", 0);
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
    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            finish();
            try {
                handler.disconnect();
            } catch (MqttException e) {
                Toast.makeText(getApplicationContext(), "failed to disconnect", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    @Override
    public void finish(){
        super.finish();
        try {
            handler.disconnect();
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