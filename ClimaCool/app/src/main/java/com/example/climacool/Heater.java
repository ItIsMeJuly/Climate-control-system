package com.example.climacool;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import java.lang.Character;
import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reactivex.plugins.RxJavaPlugins;

public class Heater extends AppCompatActivity {

    int temperature = 0;

    MqttConnectionHandler handler = new MqttConnectionHandler(
            "087086635ca64d93b3c6d5b0207bc124.s2.eu.hivemq.cloud",
            "user2",
            "user2Pass"
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heater);

        Intent received = getIntent();
        String topic = received.getStringExtra("topic");

        placeTitle(topic);

        handler.setTopic(topic);
        handler.subscribe();

        Button on = findViewById(R.id.button);
        Button off = findViewById(R.id.button2);
        EditText editText = findViewById(R.id.editTextNumberDecimal);

        editText.setEnabled(false);
        off.setEnabled(false);

        RxJavaPlugins.setErrorHandler (e -> { });

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
                on.setEnabled(false);
                off.setEnabled(true);
                handler.publish("on");
                handler.publish(Integer.toString(temperature));
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setEnabled(false);
                off.setEnabled(false);
                on.setEnabled(true);
                handler.publish("off");
            }
        });



        handler.getClient().toAsync().publishes(MqttGlobalPublishFilter.SUBSCRIBED, publish -> {
            if(publish.getPayload().isPresent()) {
                CharBuffer result = UTF_8.decode(publish.getPayload().get());
                if (Character.isDigit(result.charAt(0))) {
                    editText.setText(result);
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                handler.disconnect();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

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