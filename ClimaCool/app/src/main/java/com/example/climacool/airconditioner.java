package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class airconditioner extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    int temperature;
    MqttConnectionHandler handler = new MqttConnectionHandler(
            "087086635ca64d93b3c6d5b0207bc124.s2.eu.hivemq.cloud",
            "user2",
            "user2Pass"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airconditioner);

        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        placeTitle(topic);

        handler.setTopic(topic);
        handler.subscribe();

        radioGroup = findViewById(R.id.radioGroup);
        EditText editText = findViewById(R.id.editTextNumberDecimal2);
        Button on = findViewById(R.id.button3);
        Button off = findViewById(R.id.button4);

        editText.setEnabled(false);
        off.setEnabled(false);
        radioGroup.setEnabled(false);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    temperature = Integer.parseInt(String.valueOf(editText.getText()));
                    handler.publish(Integer.toString(temperature));
                    handled = true;
                }
                return handled;
            }
        });

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setEnabled(true);
                handler.publish("on");
                off.setEnabled(true);
                on.setEnabled(false);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(true);
                }
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setEnabled(false);
                on.setEnabled(true);
                off.setEnabled(false);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(false);
                }
                handler.publish("off");
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = findViewById(selectedId);
                if(radioButton.getId() == R.id.radioButton){
                    handler.publish("first");
                }
                else if(radioButton.getId() == R.id.radioButton2){
                    handler.publish("second");
                }
                else if(radioButton.getId() == R.id.radioButton3){
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