package com.example.climacool;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioButton first;
    RadioButton second;
    RadioButton third;
    EditText etHumidity;

    Button btnOff;
    Button btnOn;

    String throtTopic;
    String humidTopic;

    String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";

    Switch sw;
    String modeTopic;
    String mode = "manual";

    //initialize a handler with the MQTT broker
    MqttAndroidClient handler;

    double humidity;

    public Ventilation() throws MqttException {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_item);
        itemswitch.setActionView(R.layout.use_switch);

        sw = (Switch) menu.findItem(R.id.switch_item).getActionView().findViewById(R.id.switch2);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView auto = findViewById(R.id.textView27);
                if (isChecked) {
                    auto.setVisibility(View.VISIBLE);
                    btnOn.setVisibility(View.INVISIBLE);
                    btnOff.setVisibility(View.INVISIBLE);
                    etHumidity.setEnabled(true);
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
                    etHumidity.setEnabled(false);
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
        setContentView(R.layout.activity_ventilation);

        //get the path to this activity and add the topic
        Intent received = getIntent();
        String []data = received.getStringArrayExtra("topic");
        modeTopic = data[0] + "/mode";
        String stateTopic = data[0] + "/state";
        throtTopic = data[0] + "/throttle";
        humidTopic = data[0] + "/humidity";
        data[0] += "/#";

        //RxJavaPlugins.setErrorHandler (e -> { });

        if(data[1].equals("no"))
            broker = "tcp://192.168.137.1:1883";

        //place the title of the activity window
        placeTitle(data[0]);

        //set the topic for messages to the handler

        btnOff = findViewById(R.id.button5);
        btnOn = findViewById(R.id.button9);

        first = findViewById(R.id.radioButton4);
        second = findViewById(R.id.radioButton6);
        third = findViewById(R.id.radioButton5);

        etHumidity = findViewById(R.id.editTextNumber5);


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
                if(topic.equals(modeTopic)){
                    mode = msg;
                    if(msg.equals("auto")){
                        sw.setChecked(true);
                        etHumidity.setEnabled(true);
                    }
                }
                else if(topic.equals(humidTopic)){
                    humidity = Double.parseDouble(msg);
                    etHumidity.setText(msg);
                }
                switch (msg){
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
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(true);
                        }
                        btnOff.setEnabled(true);
                        btnOn.setEnabled(false);
                        if(!mode.equals("manual")) {
                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                radioGroup.getChildAt(i).setEnabled(false);
                            }
                            etHumidity.setEnabled(true);
                        }
                        else {
                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                radioGroup.getChildAt(i).setEnabled(true);
                            }
                            etHumidity.setEnabled(false);
                        }
                        break;
                    case "off":
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(false);
                        }
                        btnOn.setEnabled(true);
                        btnOff.setEnabled(false);
                        etHumidity.setEnabled(false);
                        break;
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

        etHumidity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //get the value from the text box and send it to MQTT broker
                    humidity = Double.parseDouble(String.valueOf(etHumidity.getText()));
                    try {
                        handler.publish(humidTopic, Double.toString(humidity).getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to deliver info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                v.clearFocus();
                return handled;
            }
        });


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