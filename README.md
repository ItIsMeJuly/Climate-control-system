# Climate-control-system
A Climate control system which uses arduinos with sensors and actuators to monitor the readings of a room (ex. temperature, humidity, gases and etc). This project consists of 5 parts:</br>
<ol>
  <li> Phone application 📱 </li>
  <ul> 
    <li> It is an Android App with User-friendly interface that can control and monitor all the sensors. It has different windows for the rooms and sub-systems. The application is connected to an IoT server for MQTT messages. It has access to read and write into the database all the changes in the systems.</li>
  </ul>
  <li> WebSocket ☁️</li>
  <ul>
    <li> A websocket to distribute all the messages with a specific topic to all devices subscribed to it. In other words, it is a connection between the Phone App, Desktop App and the boards as they are all subscribers in the HiveMQ server. </li>
  </ul>
  <li> Database (MySQL) 📼</li>
  <ul>
    <li> A database to track all activities by date. When a system is turned on or off or the light system is set to party mode, everything is saved into the database for easier access and monitoring of all activities.</li>
  </ul>
  <li> Arduino boards (Embedded systems) 📺</li>
  <ul>
    <li> Embedded systems boards that are the actual representation of the system with all the necessary sensors and actuators. They do all the monitoring and cooling/heating in the room. They are also connected to the MQTT server but not to the database.</li>
  </ul>
  <li> Desktop application (Windows Forms C#) 💻</li>
  <ul>
    <li> A desktop application developed specifically for a better managment and control of the climate system. It is connected to the MQTT server as a subscriber to all topics and is in charge of reading and writing to the database when an action takes place.</li>
  </ul>
</ol>
