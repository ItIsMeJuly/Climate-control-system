package com.example.climacool;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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


public class lights extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private RadioButton partyButton;
    private RadioButton focusButton;
    private RadioButton normalButton;

    private String stateTopic;
    private String modeTopic;

    private String broker = "tcp://h9111c9f.eu-central-1.emqx.cloud:15219";

    //initialize a handler with the MQTT broker
    private MqttAndroidClient handler;

    //global variable for initializing the page with data from cloud
    private Integer hexColor = 0xffffffff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);

        Intent received = getIntent();
        String []data = received.getStringArrayExtra("topic");
        stateTopic = data[0] + "/state";
        modeTopic = data[0] + "/mode";

        placeTitle(data[0]);

        if(data[1].equals("no"))
            broker = "tcp://192.168.43.136:1883";

        //RxJavaPlugins.setErrorHandler (e -> { });

        partyButton = findViewById(R.id.radioButton7);
        focusButton = findViewById(R.id.radioButton8);
        normalButton = findViewById(R.id.radioButton9);

        Button btnColor = findViewById(R.id.button6);
        Button btnOn = findViewById(R.id.button7);
        Button btnOff = findViewById(R.id.button8);

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
                if(msg.equals("off")){
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                    btnColor.setEnabled(false);
                    btnOn.setEnabled(true);
                    btnOff.setEnabled(false);
                }
                else if(msg.equals("on")){
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                    btnOn.setEnabled(false);
                    btnOff.setEnabled(true);
                    btnColor.setEnabled(true);
                }
                else if(topic.equals(data[0] + "/color")){
                    hexColor = Integer.decode(msg);
                    ImageView view = findViewById(R.id.imageView);
                    view.setBackgroundColor(hexColor);
                }
                switch (msg){
                    case "disco":
                        partyButton.toggle();
                        break;
                    case "focus":
                        focusButton.toggle();
                        break;
                    case "normal":
                        normalButton.toggle();
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
                        handler.subscribe(finalTopic + "/+", 0);
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


        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(lights.this)
                        .setTitle("Choose color")
                        .initialColor(hexColor)
                        .setTitle("Pick lights color")
                        .alphaSliderOnly()
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(lights.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("select", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                ImageView view = findViewById(R.id.imageView);
                                view.setBackgroundColor(selectedColor);
                                hexColor = selectedColor;
                                try {
                                    handler.publish(data[0] + "/color", Integer.toString(hexColor).getBytes(), 1, true);
                                } catch (MqttException e) {
                                    Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });


        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    handler.publish(stateTopic, "on".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(true);
                }
                btnColor.setEnabled(true);
                btnOff.setEnabled(true);
                btnOn.setEnabled(false);
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(false);
                }
                try {
                    handler.publish(stateTopic, "off".getBytes(), 1, true);
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                btnOff.setEnabled(false);
                btnOn.setEnabled(true);
                btnColor.setEnabled(false);
            }
        });

        addListenerOnRadioGroup();

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
    }

    public void addListenerOnRadioGroup() {

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup3);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);
                if(radioButton.getId() == R.id.radioButton7){
                    try {
                        handler.publish(modeTopic, "disco".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton8){
                    try {
                        handler.publish(modeTopic, "focus".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else if(radioButton.getId() == R.id.radioButton9){
                    try {
                        handler.publish(modeTopic, "normal".getBytes(), 1, true);
                    } catch (MqttException e) {
                        Toast.makeText(getApplicationContext(), "failed to send info", Toast.LENGTH_LONG).show();
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