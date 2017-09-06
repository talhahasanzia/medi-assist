char data = 0;            //Variable for storing received data
void setup()
{
    Serial.begin(9600);
                            
   
}
void loop()
{
     if(Serial.available() > 0)      // Send data only when you receive data:
   {
      data = Serial.read();        //Read the incoming data & store into data
      Serial.print(data);          //Print Value inside data in Serial monitor
      Serial.print("\n");        
     Serial.write("Something to say");
  
     
   }    
}
