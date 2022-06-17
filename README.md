![The Project's Logo](https://github.com/ItIsMeJuly/Climate-control-system/blob/Documents/Documents/CliMate%20Logo.png)
# Climate-control-system
A Climate control system which uses arduinos with sensors and actuators to monitor the readings of a room (ex. temperature, humidity, gases and etc). This project consists of 5 parts:</br>
<ol>
  <li> Phone application 📱 </li>
  <ul> 
    <li> It is an Android App with User-friendly interface that can control and monitor all the sensors. It has different windows for the rooms and sub-systems. The application is connected to an IoT server for MQTT messages. The phone app can control all the systems</li>
  </ul>
  <li> MQTT cloud ☁️</li>
  <ul>
    <li> An MQTT broker to distribute all the messages with a specific topic to all devices subscribed to it. In other words, it is a connection between the Phone App, Desktop App and the boards as they are all subscribers in the EMQX cloud. </li>
  </ul>
  <li> ESP32 boards (Embedded systems) 📺</li>
  <ul>
    <li> Embedded systems boards that are the actual representation of the system with all the necessary sensors and actuators. They do all the monitoring and cooling/heating in the room. They are also connected to the MQTT cloud where they read the state, threshold, mode and etc.</li>
  </ul>
  <li> Desktop application (Windows Forms C#) 💻</li>
  <ul>
    <li> A desktop application developed specifically for a better managment and control of the climate system. It is connected to the MQTT server and if no WiFi connection is detected, acts as a server(LAN) to distribute messages to every connection.</li>
  </ul>
</ol>

This is a group project where my job was to establish the connection to the broker for all boards, Desktop App and Phone App. I was in charge of building the Android Application and the Lights system as well and fix/debug the solutions of my fellow teammates.
