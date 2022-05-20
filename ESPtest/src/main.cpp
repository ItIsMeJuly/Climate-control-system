#include <WiFi.h>
#include <PubSubClient.h>
#include <HTTPClient.h>
#include <ESP32Ping.h>


// WiFi
const char *ssid = "Duli"; // Enter your WiFi name
//const char *password = "AJL6B:6^T*aQM";  // Enter WiFi password
//const char *ssid = "Fontys_WiFi_Start"; // Enter your WiFi name
const char *password = "uliduli1";  // Enter WiFi password

// MQTT Broker
//IPAddress local_host(192,168,68,52); Fontys
IPAddress local_host(192,168,43,136); 
const char * host = "h9111c9f.eu-central-1.emqx.cloud";
const char *topic = "esp32/test";
const char *mqtt_username = "user1";     //change to your username and pass
const char *mqtt_password = "user1Pass";
const int mqtt_port_local = 1883;
const int mqtt_port = 15219;

WiFiClient espClient;
PubSubClient client(espClient);

void callback(char *topic, byte *payload, unsigned int length);

void setup() {
  // Set software serial baud to 115200;
  Serial.begin(115200);
  // connecting to a WiFi network
  WiFi.begin(ssid, password);
  //WiFi.begin(ssid);
  while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the WiFi network");
  Serial.println(WiFi.localIP());
  //connecting to a mqtt broker
  bool flag = Ping.ping("www.google.com", 3);

  client.setCallback(callback);
  while (!client.connected()) {
      String client_id = "client";
      client_id += String(WiFi.macAddress());
      if(flag){
        Serial.printf("The client %s connects to the online mqtt broker\n", client_id.c_str());

        client.setServer(host, mqtt_port);
        if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
          Serial.println("Mqtt broker connected");
      } else {
          Serial.print("failed with state ");
          Serial.print(client.state());
          delay(2000);
      }
      }
      else{
        Serial.printf("The client %s connects to the local mqtt broker\n", client_id.c_str());

        client.setServer(local_host, mqtt_port_local);
        if (client.connect(client_id.c_str())) {
          Serial.println("Mqtt broker connected");
      } else {
          Serial.print("failed with state ");
          Serial.print(client.state());
          delay(2000);
      }
      }
      
  }
  // publish and subscribe
  //client.publish(topic, "test");
  client.subscribe(topic);
}

///dont change the param. Do what you have to with message in this function
void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
      Serial.print((char) payload[i]);
  }
  Serial.println();
  Serial.println("-----------------------");
}

void loop() {
  client.loop();   //start to collect messages
}