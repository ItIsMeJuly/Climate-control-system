package com.example.climacool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class airconditioner extends AppCompatActivity {

    //properties to access components of the activity
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioButton first;
    RadioButton second;
    RadioButton third;

    EditText etTemperature;
    Button btnOn;
    Button btnOff;

    //the temperature to be send to the board
    float temperature;
    String[] data;
    String stateTopic;
    String tempTopic;
    String throtTopic;

    Switch sw;

    //create a handler for the MQTT connections
    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";
    String modeTopic;
    String mode = "manual";

    MqttAndroidClient handler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_item);
        itemswitch.setActionView(R.layout.use_switch);

        sw = (Switch) menu.findItem(R.id.switch_item).getActionView().findViewById(R.id.switch2);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView auto = findViewById(R.id.textView26);
                if (isChecked) {
                    auto.setVisibility(View.VISIBLE);
                    btnOn.setVisibility(View.INVISIBLE);
                    btnOff.setVisibility(View.INVISIBLE);
                    findViewById(R.id.editTextNumberDecimal2).setEnabled(true);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                    try {
                        handler.publish(modeTopic, "auto".getBytes(StandardCharsets.UTF_8), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    mode = "auto";
                }
                else{
                    auto.setVisibility(View.INVISIBLE);
                    btnOn.setVisibility(View.VISIBLE);
                    btnOff.setVisibility(View.VISIBLE);
                    findViewById(R.id.editTextNumberDecimal2).setEnabled(false);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                    try {
                        handler.publish(modeTopic, "manual".getBytes(StandardCharsets.UTF_8), 1, true);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    mode = "manual";
                    btnOn.setEnabled(false);
                    btnOff.setEnabled(true);
                }
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airconditioner);

        //receive the path to this activity and set topic
        Intent received = getIntent();
        data = received.getStringArrayExtra("topic");
        String mainTopic = data[0];
        stateTopic = data[0] + "/state";
        tempTopic = data[0] + "/temperature";
        throtTopic = data[0] + "/throttle";
        data[0] += "/#";


        if(data[1].equals("no"))
            broker = "tcp://192.168.43.136:1883";

        placeTitle(data[0]);

        //variables to customize and access the values of the buttons and text boxes
        radioGroup = findViewById(R.id.radioGroup);
        first = findViewById(R.id.radioButton);
        second = findViewById(R.id.radioButton2);
        third = findViewById(R.id.radioButton3);

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
                    temperature = Float.parseFloat(msg);
                    etTemperature.setText(msg);
                }
                else if(topic.equals(mainTopic + "/mode")){
                    mode = msg;
                    if(msg.equals("auto"))
                        sw.setChecked(true);
                }
                else{
                    switch (msg) {
                        case "first":
                            first.toggle();
                            break;
                        case "second":
                            second.toggle();
                            break;
                        case "third":
                            third.toggle();
                            break;
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
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        try {
            String finalTopic = data[0];
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
                        handler.publish(tempTopic, Float.toString(temperature).getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                v.clearFocus();
                return handled;
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //custom design features
                {
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