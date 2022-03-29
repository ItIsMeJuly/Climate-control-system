package com.example.climacool;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class lights extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int color;
    private String state;
    MqttConnectionHandler handler = new MqttConnectionHandler(
            "087086635ca64d93b3c6d5b0207bc124.s2.eu.hivemq.cloud",
            "user2",
            "user2Pass"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);

        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        placeTitle(topic);

        handler.setTopic(topic);
        handler.subscribe();

        Button btn = findViewById(R.id.button6);


        btn.setEnabled(false);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(lights.this)
                        .setTitle("Choose color")
                        .initialColor(0xffffff)
                        .setTitle("Pick lights color")
                        .alphaSliderOnly()
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(lights.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("select", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                ImageView view = findViewById(R.id.imageView);
                                view.setBackgroundColor(selectedColor);
                                color = selectedColor;
                                handler.publish(String.valueOf(color));
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


        Button on = findViewById(R.id.button7);
        Button off = findViewById(R.id.button8);

        off.setEnabled(false);

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.publish("on");
                btn.setEnabled(true);
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
                handler.publish("off");
                off.setEnabled(false);
                on.setEnabled(true);
                btn.setEnabled(false);
                for(int i = 0; i < radioGroup.getChildCount(); i++){
                    radioGroup.getChildAt(i).setEnabled(false);
                }
            }
        });

        addListenerOnRadioGroup();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
                handler.disconnect();
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
                    state = "first";
                    handler.publish(state);
                }
                else if(radioButton.getId() == R.id.radioButton8){
                    state = "second";
                    handler.publish(state);
                }
                else if(radioButton.getId() == R.id.radioButton9){
                    state = "third";
                    handler.publish(state);
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