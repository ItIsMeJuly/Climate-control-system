package com.example.climacool;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Ventilation extends AppCompatActivity {

    //fields to access the components of the activity
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnOff;
    private Button btnOn;

    private String throtTopic;

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "emqx_test";

    //initialize a handler with the MQTT broker
    MqttAndroidClient handler;

    public Ventilation() throws MqttException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventilation);

        //get the path to this activity and add the topic
        Intent received = getIntent();
        String topic = received.getStringExtra("topic");
        String stateTopic = topic + "/state";
        throtTopic = topic + "/throttle";
        topic += "/#";

        //RxJavaPlugins.setErrorHandler (e -> { });

        //place the title of the activity window
        placeTitle(topic);

        //set the topic for messages to the handler

        btnOff = findViewById(R.id.button5);
        btnOn = findViewById(R.id.button9);

        //RxJavaPlugins.setErrorHandler (e -> { });


        //new MQTT connection
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("user1");
        opts.setPassword("user1Pass".toCharArray());

        handler = new MqttAndroidClient(getApplicationContext(), broker, "MobileApp");
        //set messages callback
        handler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                switch (msg){
                    case "first":
                        radioGroup.check(radioGroup.getChildAt(0).getId());
                        break;
                    case "second":
                        radioGroup.check(radioGroup.getChildAt(1).getId());
                        break;
                    case "third":
                        radioGroup.check(radioGroup.getChildAt(2).getId());
                        break;
                    case "on":
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(true);
                        }
                        btnOff.setEnabled(true);
                        btnOn.setEnabled(false);
                        break;
                    case "off":
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(false);
                        }
                        btnOn.setEnabled(true);
                        btnOff.setEnabled(false);
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        try {
            String finalTopic = topic;
            handler.connect(opts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        handler.subscribe(finalTopic, 0);
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


        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(false);
                }
                btnOff.setEnabled(false);
                btnOn.setEnabled(true);
                try {
                    handler.publish(stateTopic, "off".getBytes(), 1, true);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(true);
                }
                btnOff.setEnabled(true);
                btnOn.setEnabled(false);
                try {
                    handler.publish(stateTopic, "on".getBytes(), 1, true);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
                try {
                    handler.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        addListenerOnRadioGroup();
    }

    public void addListenerOnRadioGroup() {

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);
                if(radioButton.getId() == R.id.radioButton4){
                    try {
                        handler.publish(throtTopic, "first".getBytes(), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton6){
                    try {
                        handler.publish(throtTopic, "second".getBytes(), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton5){
                    try {
                        handler.publish(throtTopic, "third".getBytes(), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        try {
            handler.disconnect();
        } catch (MqttException e) {
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