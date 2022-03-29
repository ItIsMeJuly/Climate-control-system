package com.example.climacool;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttConnectionHandler {
    private String host;
    private String username;
    private String password;
    private String topic;

    private Mqtt5BlockingClient client;

    public MqttConnectionHandler(String host, String username, String password){
        this.host = host;
        this.username = username;
        this.password = password;

        //build the client
        client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8884)
                .sslWithDefaultConfig()
                .webSocketConfig()
                .serverPath("mqtt")
                .applyWebSocketConfig()
                .buildBlocking();

        //connect to the server
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();
    }


    public void subscribe(){
        client.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();
    }

    public void addMoreSubscriptions(String topic){
        client.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();
    }

    public void publish(String message){
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(message))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public void disconnect(){
        client.disconnect();
    }

    public Mqtt5BlockingClient getClient() {
        return client;
    }
}
