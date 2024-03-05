int devicePins[] = {2, 3, 4, 5}; // Array to store pin numbers
bool deviceStates[] = {true, true, true, true}; // Array to track the state of each device

void setup() {
  for (int i = 0; i < 4; i++) {
    pinMode(devicePins[i], OUTPUT);
    digitalWrite(devicePins[i], HIGH); // Initialize all devices to HIGH
  }
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()> 0) {
    char val = Serial.read(); // Declare val inside the loop to get the latest value
    int value = val - '0' ;
    Serial.println(value);

    if (value >= 1 && value <= 4) {
      int deviceIndex = value - 1; // Convert char to the corresponding index in the array
      if(deviceStates[deviceIndex]){
        digitalWrite(devicePins[deviceIndex], LOW);
        deviceStates[deviceIndex] = false; // Update the state of the device
      } else {
        digitalWrite(devicePins[deviceIndex], HIGH);
        deviceStates[deviceIndex] = true; // Update the state of the device
      }
    } else if (value == 0) {
      for (int i = 0; i < 4; i++) {
        if (!deviceStates[i]) {
          digitalWrite(devicePins[i], HIGH); // Turn off only the specified device that is currently open
          deviceStates[i] = true; // Update the state of the device
        }
      }
    }
    else{
      for(int i = 0; i < 4; i++){
          if(deviceStates[i]){
            digitalWrite(devicePins[i], LOW);
            deviceStates[i] = false;
          }
      }
    }
  }

  delay(100);
}
