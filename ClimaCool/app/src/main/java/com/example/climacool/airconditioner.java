package com.example.climacool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class airconditioner extends AppCompatActivity {

    //properties to access components of the activity
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText etTemperature;
    private Button btnOn;
    private Button btnOff;

    //the temperature to be send to the board
    private int temperature;
    private String topic;
    private String stateTopic;
    private String tempTopic;
    private String throtTopic;

    //create a handler for the MQTT connections
    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "emqx_test";

    MqttAndroidClient handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airconditioner);

        //receive the path to this activity and set topic
        Intent received = getIntent();
        topic = received.getStringExtra("topic");
        stateTopic = topic + "/state";
        tempTopic = topic + "/temperature";
        throtTopic = topic + "/throttle";
        topic += "/#";

        //RxJavaPlugins.setErrorHandler (e -> { });

        placeTitle(topic);

        //variables to customize and access the values of the buttons and text boxes
        radioGroup = findViewById(R.id.radioGroup);
        etTemperature = findViewById(R.id.editTextNumberDecimal2);
        btnOn = findViewById(R.id.button3);
        btnOff = findViewById(R.id.button4);


        //new MQTT connection
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("user1");
        opts.setPassword("user1Pass".toCharArray());

        handler = new MqttAndroidClient(getApplicationContext(), broker, "MobileApp");

        //set messages listener
        handler.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());

                if(Character.isDigit(msg.charAt(0))){
                    temperature = Integer.parseInt(msg.toString());
                    etTemperature.setText(msg);
                }
                else{
                    switch (msg) {
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
                            etTemperature.setEnabled(true);
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(true);
                            break;
                        case "off":
                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                radioGroup.getChildAt(i).setEnabled(false);
                            }
                            etTemperature.setEnabled(false);
                            btnOn.setEnabled(true);
                            btnOff.setEnabled(false);
                            break;
                    }
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


        etTemperature.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //get the value from the text box and send it to MQTT broker
                    temperature = Integer.parseInt(String.valueOf(etTemperature.getText()));
                    try {
                        handler.publish(tempTopic, Integer.toString(temperature).getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                return handled;
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //custom design features
                {
                    etTemperature.setEnabled(true);
                    btnOff.setEnabled(true);
                    btnOn.setEnabled(false);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                }
                try {
                    handler.publish(stateTopic, "on".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to deliver info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //custom design features
                {
                    etTemperature.setEnabled(false);
                    btnOn.setEnabled(true);
                    btnOff.setEnabled(false);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                }
                try {
                    handler.publish(stateTopic, "off".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to deliver info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        //override transition animation
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

        addListenerOnRadioGroup(this);
    }

    //adds listener for changed state on the radio buttons
    public void addListenerOnRadioGroup(Context context) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if(radioButton.getId() == R.id.radioButton){
                    try {
                        handler.publish(throtTopic, "first".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(context, "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton2){
                    try {
                        handler.publish(throtTopic, "second".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(context, "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton3){
                    try {
                        handler.publish(throtTopic, "third".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(context, "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //override the finish method to apply transition
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

    //Set the title of the current activity in the app
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