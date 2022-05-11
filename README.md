# 347project: Iot HTL

### How to run System Management: ###
1.  Open IoT HTL directory in a java-supported IDE
2.  Enter "java SystemManagement.java" in the terminal
The user ID is "UserID" and the password is "Password"

### How to run IoT HTL: ###
1.  Open IoT HTL directory in a java-supported IDE
2.  Enter "java IoTHTL.java" in the terminal

### Commands: ###
- accelerate &lt;float speed&gt;: Moves the car a given number of spaces
- brake: Sets car speed to zero (useful for overriding cruise control, adaptive cruise control, etc.)
- steer &lt;left or right&gt;: turns the car in a direction by moving one space forward and then on space in the direction given, rotating the car as well.
- cc: toggles cruise control
- acc: toggles adaptive cruise control
- ab: toggles automatic braking
- as: toggles automatic steering (steers around obstacles)
- gps &lt;Destination&gt;: toggles gps and follows route to destination (route is preset, as it would not be calculated by IoT HTL but instead by planned maps API)
