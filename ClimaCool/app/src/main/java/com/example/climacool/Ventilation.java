package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Ventilation extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnOff;
    private Button btnOn;

    MqttConnectionHandler handler = new MqttConnectionHandler(
            "087086635ca64d93b3c6d5b0207bc124.s2.eu.hivemq.cloud",
            "user2",
            "user2Pass"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventilation);

        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        placeTitle(topic);

        handler.setTopic(topic);
        handler.subscribe();

        btnOff = findViewById(R.id.button5);
        btnOn = findViewById(R.id.button9);

        btnOff.setEnabled(false);

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.publish("off");
                btnOff.setEnabled(false);
                btnOn.setEnabled(true);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(false);
                }
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOff.setEnabled(true);
                btnOn.setEnabled(false);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(true);
                }
                handler.publish("on");
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
                handler.disconnect();
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
                    handler.publish("first");
                }
                else if(radioButton.getId() == R.id.radioButton6){
                    handler.publish("second");
                }
                else if(radioButton.getId() == R.id.radioButton5){
                    handler.publish("third");
                }
            }
        });
    }

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