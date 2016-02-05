#include <Usb.h> 
#include <AndroidAccessory.h> 

int interval = 5; // Värde för fördröjning av exekveringen

AndroidAccessory acc( // Accessory-värden
"Malmö University", 
"DA119A1", 
"Example 1, DA119A", 
"1.0", 
"http://edu.mah.se/DA119A", 
"0000000012345678" ); 
void setup() { 
  pinMode(led, OUTPUT); // Sätter output-port
  acc.powerOn(); // Starta kommunikation med AndroidAccessory-klassen

  Serial.begin(9600);
} 
void loop() { 
  byte msg[10]; 
  if (acc.isConnected()) { // Om kommunikationen är uppkopplad
    Serial.println("Connected");
    int len = acc.read(msg, sizeof(msg), 1); 
    if (len > 0) { // Om meddelande är längre än 0
      if(msg[0] == 'H') // Om första bokstaven == H
        digitalWrite(led, HIGH); // Tänd lampa  
      else if(msg[0] == 'L') // Om första bokstaven == L  
        digitalWrite(led, LOW); // Släck lampa
        
    } 
  } 

  delay(interval); // Fördröj 5 ms
}

