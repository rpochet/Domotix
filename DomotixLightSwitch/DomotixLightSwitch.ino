/**
 * DomotixLightSwitch.ino
 *
 * Device measuring temperature from an I2C ??? sensor. This sketch makes use of Adafruit's BMP085 library
 * for Arduino: http://learn.adafruit.com/bmp085/using-the-bmp085
 * Pins: I2C port and PIN_PWRPRESS (Power pin)
 *
 * Product name: DomotixLightSwitch (Domotix light switch with temperature sensor)
 * Author: Pochet Romuald
 * Creation date: 12 Nov 2013
 */

#include "regtable.h"
#include "panstamp.h"
#include <mpr121.h>
#include <Wire.h>
#include "sensor.h"

/**
 * LED pin
 */

#define DEBUG
#define TEMP                  1
#define MPR121_KEY_NB        12
#define TOUCH_STATE_LENGTH    1

const int ledPin = 4;
int mpr121IRQPin = 14; // A0, if change update pcEnableInterrupt, pcDisableInterrupt and PCINTMASK0
int mpr121IRQPinNumber = 8;
const int mpr121Address = 0x5A;

/**
 * Macros
 */
#define pcEnableInterrupt()     PCICR = 0x02; PCMSK1 = 0x01;    // Enable Pin Change interrupt on port mpr121IRQPinNumber
#define pcDisableInterrupt()    PCICR = 0x00; PCMSK1 = 0x01;    // Disable Pin Change interrupt on port mpr121IRQPinNumber

/**
 * Pin Change Interrupt flag
 */
volatile boolean pcIRQ = false;

unsigned int dtSensorDelay = 1000 * 60 * 60;
unsigned long timeout;
boolean touchStates[MPR121_KEY_NB]; // To keep track of the previous touch states
boolean touchStatesChange[MPR121_KEY_NB]; // To keep track of touch states change

/**
 * setup
 *
 * Arduino setup function
 */
void setup() 
{
  // Disable Interrupts
  cli();
  
  int i;

#ifdef DEBUG
  Serial.begin(9600);
  Serial.println("Pressure Sensor Test");
#endif

  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  pinMode(mpr121IRQPin, INPUT);
  digitalWrite(mpr121IRQPin, HIGH); //enable pullup resistor
  attachInterrupt(mpr121IRQPinNumber, mpr121Interrupt, FALLING); // 0 -> B8, MPR121 IRSQ goes low
  Wire.begin();
  mpr121Setup();

  //initSensor();

  // Init panStamp
  panstamp.init();

  // Transmit product code
  getRegister(REGI_PRODUCTCODE)->getData();

  // Enter SYNC state
  panstamp.enterSystemState(SYSTATE_SYNC);

  // During 3 seconds, listen the network for possible commands whilst the LED blinks
  for(i = 0; i < 6; i++) 
  {
    digitalWrite(ledPin, HIGH);
    delay(100);
    digitalWrite(ledPin, LOW);
    delay(400);
  }

  // Transmit periodic Tx interval
  getRegister(REGI_TXINTERVAL)->getData();

  // Switch to Rx OFF state
  panstamp.enterSystemState(SYSTATE_RXOFF);

  timeout = millis();
  
  // Enable Interrupts
  sei();
}

/**
 * loop
 *
 * Arduino main loop
 */
void loop() 
{
  pcDisableInterrupt();
  
  if (millis() == timeout) 
  {
    // time to send sensor register
    timeout += dtSensorDelay;

    // Transmit sensor data
    getRegister(REGI_SENSOR)->getData();
  }

  if (pcIRQ)
  {
    //Ready to receive new PC interrupts
    pcIRQ = false;
    for(int i = 0; i < MPR121_KEY_NB; i++) 
    {
      if(touchStatesChange[i] == 1) 
      {
        // Transmit touch data
        getRegister(REGI_TOUCH_START_IDX + i)->getData();
      }
    }
  }
  else
  {
  }

  pcEnableInterrupt();

  // Sleep for panstamp.txInterval seconds (register 10)
  panstamp.goToSleep();
}

void mpr121Interrupt() 
{
  panstamp.rtc.wakeUp();
  
  // Was default in previous version of WakeUp
  panstamp.enterSystemState(SYSTATE_RXON);
    
  pcIRQ = true;
  
  //read the touch state from the MPR121
  Wire.requestFrom(mpr121Address, 2); 
  byte LSB = Wire.read();
  byte MSB = Wire.read();
  uint16_t touched = ((MSB << 8) | LSB); //16bits that make up the touch states

  for (int i = 0; i < MPR121_KEY_NB; i++)
  {  // Check what electrodes were pressed
    if(touched & (1 << i))
    {
      if(touchStates[i] == 0)
      {
        touchStatesChange[i] = 1;
#ifdef DEBUG
        Serial.print("pin ");
        Serial.print(i);
        Serial.println(" was just touched");
#endif
      }
      touchStates[i] = 1;      
    }
    else
    {
      if(touchStates[i] == 1)
      {
#ifdef DEBUG
        Serial.print("pin ");
        Serial.print(i);
        Serial.println(" is no longer being touched");
#endif
      }
      touchStates[i] = 0;
    }
  }
}

void mpr121Setup(void)
{
  mpr121SetRegister(mpr121Address, ELE_CFG, 0x00); 

  // Section A - Controls filtering when data is > baseline.
  mpr121SetRegister(mpr121Address, MHD_R, 0x01);
  mpr121SetRegister(mpr121Address, NHD_R, 0x01);
  mpr121SetRegister(mpr121Address, NCL_R, 0x00);
  mpr121SetRegister(mpr121Address, FDL_R, 0x00);

  // Section B - Controls filtering when data is < baseline.
  mpr121SetRegister(mpr121Address, MHD_F, 0x01);
  mpr121SetRegister(mpr121Address, NHD_F, 0x01);
  mpr121SetRegister(mpr121Address, NCL_F, 0xFF);
  mpr121SetRegister(mpr121Address, FDL_F, 0x02);

  // Section C - Sets touch and release thresholds for each electrode
  mpr121SetRegister(mpr121Address, ELE0_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE0_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE1_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE1_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE2_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE2_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE3_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE3_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE4_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE4_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE5_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE5_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE6_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE6_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE7_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE7_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE8_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE8_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE9_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE9_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE10_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE10_R, REL_THRESH);

  mpr121SetRegister(mpr121Address, ELE11_T, TOU_THRESH);
  mpr121SetRegister(mpr121Address, ELE11_R, REL_THRESH);

  // Section D
  // Set the Filter Configuration
  // Set ESI2
  mpr121SetRegister(mpr121Address, FIL_CFG, 0x04);

  // Section E
  // Electrode Configuration
  // Set ELE_CFG to 0x00 to return to standby mode
  mpr121SetRegister(mpr121Address, ELE_CFG, 0x0C);  // Enables all 12 Electrodes

  // Section F
  // Enable Auto Config and auto Reconfig
  /*mpr121SetRegister(mpr121Address, ATO_CFG0, 0x0B);
   mpr121SetRegister(mpr121Address, ATO_CFGU, 0xC9);  // USL = (Vdd-0.7)/vdd*256 = 0xC9 @3.3V   mpr121SetRegister(mpr121Address, ATO_CFGL, 0x82);  // LSL = 0.65*USL = 0x82 @3.3V
   mpr121SetRegister(mpr121Address, ATO_CFGT, 0xB5);*/  // Target = 0.9*USL = 0xB5 @3.3V

  mpr121SetRegister(mpr121Address, ELE_CFG, 0x0C);
}

void mpr121SetRegister(int address, unsigned char r, unsigned char v)
{
  Wire.beginTransmission(address);
  Wire.write(r);
  Wire.write(v);
  Wire.endTransmission();
}


