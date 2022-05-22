#include <WiFi.h>
#include <PubSubClient.h>
#include <HTTPClient.h>
#include <ESP32Ping.h>
#include <stdlib.h>
#include <stdint.h>

// RGB pins
#define redPin 13
#define greenPin 12
#define bluePin 14

// states
String state = "";
long color = 0;
String mode = "";

// random generator
long randomColor;

// timer
hw_timer_t *timer = NULL;

// WiFi
const char *ssid = "Duli";
const char *password = "uliduli1"; // Enter WiFi password

// MQTT Broker
IPAddress local_host(192, 168, 43, 136);
const char *host = "h9111c9f.eu-central-1.emqx.cloud";
const char *topicColor = "livingroom/light/color";
const char *topicMode = "livingroom/light/mode";
const char *topicState = "livingroom/light/state";
const char *mqtt_username = "user1"; // change to your username and pass
const char *mqtt_password = "user1Pass";
const int mqtt_port_local = 1883;
const int mqtt_port = 15219;

WiFiClient espClient;
PubSubClient client(espClient);

// function declarations
void callback(char *topic, byte *payload, unsigned int length);
void convertRGB(long number);
void modes();
void setPins();
void IRAM_ATTR onTimer();


void setup()
{
  // setTimer
  randomSeed(analogRead(35));
  timer = timerBegin(0, 80, true);
  timerAttachInterrupt(timer, &onTimer, true);
  timerAlarmWrite(timer, 1000000, true);
  timerAlarmEnable(timer);

  // Set software serial baud to 115200;
  Serial.begin(115200);

  // connecting to a WiFi network
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the WiFi network");
  Serial.println(WiFi.localIP());

  // try ping to check internet availability
  bool flag = Ping.ping("www.google.com", 3);

  client.setCallback(callback);
  while (!client.connected())
  {
    String client_id = "client";
    client_id += String(WiFi.macAddress());
    if (flag)
    {
      Serial.printf("The client %s connects to the remote mqtt broker\n", client_id.c_str());

      client.setServer(host, mqtt_port);
      if (client.connect(client_id.c_str(), mqtt_username, mqtt_password))
      {
        Serial.println("Mqtt broker connected");
      }
      else
      {
        Serial.print("failed with state ");
        Serial.print(client.state());
        delay(2000);
      }
    }
    else
    {
      Serial.printf("The client %s connects to the local mqtt broker\n", client_id.c_str());

      bool localFlag = Ping.ping(local_host, 3);
      client.setServer(local_host, mqtt_port_local);
      if (client.connect(client_id.c_str()))
      {
        Serial.println("Mqtt broker connected");
      }
      else
      {
        Serial.print("failed with state ");
        Serial.print(client.state());
        delay(2000);
      }
    }
  }
  // subscribe to topics
  client.subscribe(topicColor);
  client.subscribe(topicMode);
  client.subscribe(topicState);
}


void loop()
{
  client.loop(); // start to collect messages

  // turn LED off if state is OFF
  if (state.equals("off"))
  {
    convertRGB(0);
  }
}



// the function to receive messages from the server
void callback(char *topic, byte *payload, unsigned int length)
{
  String data; // variable to receive data

  // extract the bytes from the server as STRING
  for (int i = 0; i < length; i++)
  {
    data.concat((char)payload[i]);
  }

  // if the message has the state of the lights
  if (!strcmp(topic, topicState))
  {
    if (data.equals("on"))
    {
      state = "on";
    }
    else
    {
      state = "off";
    }
  }

  // if the message is with the color code
  if (!strcmp(topic, topicColor))
  {
    color = data.toInt();
  }

  // if message is with the MODE
  if (!strcmp(topic, topicMode))
  {
    mode = data;
  }

  // internal call to modes if state is ON
  if (state.equals("on"))
  {
    modes();
  }
}

// timed function to change light's color
void IRAM_ATTR onTimer()
{
  if (mode.equals("disco") && state.equals("on"))
  {
    randomColor = esp_random();
    convertRGB(randomColor);
  }
}

// switch between focus and normal as they are not a timed event
void modes()
{
  if (mode.equals("focus"))
  {
    convertRGB(0x99ccff); //focus light color
  }
  else if (mode.equals("normal"))
  {
    convertRGB(color);
  }
}

// set PINs
void setPins()
{
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
}

// convert long to RGB format and insert the values
void convertRGB(long number)
{
  // 8bit colors
  uint8_t red;
  uint8_t green;
  uint8_t blue;

  blue |= number; // least significant BYTE is blue
  number >>= 8;
  green |= number; // now least significant BYTE is green
  number >>= 8;
  red |= number; // now least significant BYTE is red

  // send the data to the RGB led
  analogWrite(bluePin, blue);
  analogWrite(greenPin, green);
  analogWrite(redPin, red);
}