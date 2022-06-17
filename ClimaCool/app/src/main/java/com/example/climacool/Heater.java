package com.example.climacool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import java.sql.Array;
import java.util.Arrays;

public class Heater extends AppCompatActivity {

    double temperature = 0;
    String throtTopic;
    String modeTopic;
    String stateTopic;

    RadioGroup radioGroup;
    RadioButton radioButton;
    Button btnOn;
    Button btnOff;

    RadioButton first;
    RadioButton second;
    RadioButton third;

    Switch sw;


    String flag;
    MqttConnectOptions opts;

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String clientId = "client123134124";

    String []data;
    EditText etTemperature;

    //Define a handler with the MQTT broker
    MqttAndroidClient handler;

    String mode = "manual";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_item);
        itemswitch.setActionView(R.layout.use_switch);

        sw = (Switch) menu.findItem(R.id.switch_item).getActionView().findViewById(R.id.switch2);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView auto = findViewById(R.id.textView25);
                if (isChecked) {
                    auto.setVisibility(View.VISIBLE);
                    btnOn.setVisibility(View.INVISIBLE);
                    btnOff.setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView).setEnabled(true);
                    findViewById(R.id.editTextNumberDecimal).setEnabled(true);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                    try {
                        handler.publish(modeTopic, "auto".getBytes(StandardCharsets.UTF_8), 1, true);
                        handler.publish(data[0] + "/temperature", etTemperature.toString().getBytes(StandardCharsets.UTF_8), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    auto.setVisibility(View.INVISIBLE);
                    btnOn.setVisibility(View.VISIBLE);
                    btnOff.setVisibility(View.VISIBLE);
                    findViewById(R.id.textView).setEnabled(false);
                    findViewById(R.id.editTextNumberDecimal).setEnabled(false);
                    findViewById(R.id.button).setEnabled(false);
                    findViewById(R.id.button2).setEnabled(true);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                    try {
                        handler.publish(modeTopic, "manual".getBytes(StandardCharsets.UTF_8), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heater);

        //catch topic
        Intent received = getIntent();
        data = received.getStringArrayExtra("topic");
        flag = data[1];

        stateTopic = data[0] + "/state";
        throtTopic = data[0] + "/throttle";
        modeTopic = data[0] + "/mode";

        if(data[1].equals("no"))
            broker = "tcp://192.168.137.1:1883";

        String thisRoom;
        final String[] otherMode = new String[1];

        thisRoom = placeTitle(data[0]);

        radioGroup = findViewById(R.id.RadioGroupHeater);
        first = findViewById(R.id.radioButton10);
        second = findViewById(R.id.radioButton11);
        third = findViewById(R.id.radioButton12);


        btnOn = findViewById(R.id.button);
        btnOff = findViewById(R.id.button2);

        etTemperature = findViewById(R.id.editTextNumberDecimal);

        addListenerOnRadioGroup(getApplicationContext());

        //new MQTT connection
        opts = new MqttConnectOptions();
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
                if(topic.equals(throtTopic)){
                    switch(msg){
                        case "first":
                            first.toggle();
                            break;
                        case "second":
                            second.toggle();
                            break;
                        case "third":
                            third.toggle();
                            break;
                    }
                }

                else if(topic.equals(thisRoom + "/airconditioner/mode")){
                    otherMode[0] = msg;
                    if(msg.equals("auto")){
                        sw.setChecked(true);
                    }
                }

                else if(topic.equals(thisRoom + "/airconditioner/temperature")){
                    if(otherMode[0].equals("auto")){
                        temperature = Float.parseFloat(msg);
                    }
                }

                else if(topic.equals(stateTopic)){
                    switch(msg){
                        case "on":
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(true);
                            if(!mode.equals("manual")) {
                                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                    radioGroup.getChildAt(i).setEnabled(false);
                                }
                                etTemperature.setEnabled(true);
                            }
                            else {
                                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                    radioGroup.getChildAt(i).setEnabled(true);
                                }
                                etTemperature.setEnabled(false);
                            }
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

                else if(topic.equals(data[0] + "/mode")){
                    mode = msg;
                    if(msg.equals("auto") || otherMode[0].equals("auto")){
                        mode = "auto";
                        sw.setChecked(true);
                    }
                }

                else if(topic.equals(data[0] + "/temperature")){
                    temperature = Double.parseDouble(msg);
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
                        handler.subscribe(data[0] + "/+", 0);
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


        if(flag.equals("on")){
            sw.toggle();
        }

        etTemperature.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    temperature = Double.parseDouble(String.valueOf(etTemperature.getText()));
                    try {
                        handler.publish(data[0] + "/temperature", Double.toString(temperature).getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    //handled = true;
                }
                v.clearFocus();
                return handled;
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(true);
                }
                if(mode.equals("manual")) {
                    etTemperature.setEnabled(false);
                }
                else{
                    etTemperature.setEnabled(true);
                }
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
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }
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

    //listener for actions on the throttle
    public void addListenerOnRadioGroup(Context context) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if(radioButton.getId() == R.id.radioButton10){
                    try {
                        handler.publish(throtTopic, "first".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(context, "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton11){
                    try {
                        handler.publish(throtTopic, "second".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(context, "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton12){
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



    String placeTitle(String topic) {
        if (topic.contains("bedroom")) {
            setTitle("Bedroom");
            return "bedroom";
        } else if (topic.contains("bathroom")) {
            setTitle("Bathroom");
            return  "bathroom";
        } else if (topic.contains("livingroom")) {
            setTitle("Living room");
            return  "livingroom";
        } else if (topic.contains("office")) {
            setTitle("Office");
            return "office";
        } else {
            setTitle("Kitchen");
            return "kitchen";
        }
    }

}