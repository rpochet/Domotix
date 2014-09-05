/**
 * DomotixLightSwitch.ino
 *
 * Device measuring temperature from an I2C ??? sensor.
 * Pins: I2C port and PIN_PWRPRESS (Power pin)
 *
 * Product name: DomotixLightSwitch (Domotix light switch with temperature sensor)
 * Author: Pochet Romuald
 * Creation date: 12 Nov 2013
 */

#include "regtable.h"
#include "panstamp.h"
#include "Wire.h"
#include <mpr121.h>
#include "Adafruit_MCP9808.h"

#define DEBUG

/**
 * LED pin
 */
const int ledPin = 4;

/**
 * SENSOR
 */
//#define SENSOR  SENSOR_TMP102
//#define SENSOR  SENSOR_MCP9808

const int sensorAddress = 0x48;
static byte dtSensorDelay[2];
word sensorDelay; // s
unsigned long nextUpdate;
#if SENSOR == SENSOR_MCP9808
// Create the MCP9808 temperature sensor object with default address 0x18
Adafruit_MCP9808 tempsensor = Adafruit_MCP9808();
#endif

/**
 * TOUCH BOARD
 */
#define TOUCH TOUCH_MPR121  

const int touchboardAddress = 0x5A;
int touchboardIRQPin = 3; // PD3, if change update pcEnableInterrupt, pcDisableInterrupt and PCINTMASK0
int touchboardIRQNumber = 1;
volatile boolean touchboardIRQ = false;

const int touchboardKeyNb = 12;

/**
 * TOUCH: 0, addr, regId
 * LED: 1, 
 */
const int touchConfigLength = 3;
static byte dtTouchConfig[touchboardKeyNb * touchConfigLength];

static byte toggleCommandValue[] = {0xff};
boolean touchStates[touchboardKeyNb]; // To keep track of the previous touch states
boolean touchStatesChange[touchboardKeyNb]; // To keep track of touch states change

/**
 * setup
 *
 * Arduino setup function
 */
void setup()
{
  int i;
#ifdef DEBUG
  Serial.begin(115200);
  delay(2000); // to allow starting serial console
#endif

  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);
  
  //eepromToDefaults();
  
  // Init panStamp
  panstamp.init();
  
  panstamp.setLowTxPower();
  
  getRegister(REGI_PRODUCTCODE)->getData();
  
  // panstamp init only read register value from EEPROM, 
  // so initialize other dependant value here
  initRegister();
    
  // Enter SYNC state
  panstamp.enterSystemState(SYSTATE_SYNC);
  
  // During 3 seconds, listen the network for possible commands whilst the LED blinks
  for(i = 0 ; i < 6 ; i++)
  {
    digitalWrite(ledPin, LOW);
    delay(400);
    digitalWrite(ledPin, HIGH);
    delay(100);
  }
  
#ifdef SENSOR
  initSensor();
#endif
  
#if TOUCH == TOUCH_MPR121
  initMpr121();
#endif
    
  nextUpdate = millis(); 
  
  // Transmit configuration
  getRegister(REGI_TXINTERVAL)->getData();
  // Transmit power voltage
  getRegister(REGI_VOLTSUPPLY)->getData(); 
  getRegister(REGI_SENSOR_DELAY)->getData();
  getRegister(REGI_TOUCH_CONFIG)->getData();
  
  // Switch to Rx OFF state
  // TODO set to OFF as it is a sleeping device
  panstamp.enterSystemState(SYSTATE_RXON);
  
  digitalWrite(ledPin, LOW);
  
#ifdef DEBUG
  Serial.println(freeRam());
#endif
}

void eepromToDefaults()
{
  int i;
  eepromToFactoryDefaults();
  EEPROM.write(EEPROM_CONFIG_SENSOR_DELAY, 0);
  EEPROM.write(EEPROM_CONFIG_SENSOR_DELAY + 1, 0);
  for(i = 0 ; i < touchboardKeyNb; i++)
  {
    EEPROM.write(EEPROM_TOUCH_CONFIG + i * touchConfigLength, 0);
    EEPROM.write(EEPROM_TOUCH_CONFIG + i * touchConfigLength + 1, 2);
    EEPROM.write(EEPROM_TOUCH_CONFIG + i * touchConfigLength + 2, 14 + i);
  }
  //panstamp.cc1101.setDevAddress(255, true);
}

int freeRam() {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void initRegister() 
{
  sensorDelay = word(dtSensorDelay[1], dtSensorDelay[0]);
  if(sensorDelay == 0 || sensorDelay == 4294967295) 
  {
    sensorDelay = 3600; // 0x0E10
    EEPROM.write(EEPROM_CONFIG_SENSOR_DELAY + 1, 0x0E);
    EEPROM.write(EEPROM_CONFIG_SENSOR_DELAY, 0x10);
  }
  
#ifdef DEBUG
  Serial.print("sensorDelay: ");
  Serial.println(sensorDelay);
#endif
}

#ifdef SENSOR
void initSensor() 
{
#ifdef DEBUG
  Serial.print("Init sensor... ");
#endif

#if SENSOR == SENSOR_MCP9808
  if (!tempsensor.begin()) {
    Serial.println("Couldn't find MCP9808!");
    while (1);
  }
#elif SENSOR == SENSOR_TMP102
  Wire.begin();
#endif
    
#ifdef DEBUG
  Serial.println(" OK");
#endif
}
#endif

/**
 * MPR121 Interrupt 
 * 
 */
void mpr121Interrupt() 
{
  // Wake Up and SYSTATE_RXON
  //panstamp.wakeUp();
    
  touchboardIRQ = true;
}

/**
 * Init MPR121 
 * - Setup IRQ pin, FALLING
 * - Setup MPR121
 */
#if TOUCH == TOUCH_MPR121
void initMpr121() 
{
#ifdef DEBUG
  Serial.print("Init MPR121... ");
#endif
    
  Wire.begin();
  mpr121Setup();
  
  pinMode(touchboardIRQPin, INPUT);
  //digitalWrite(touchboardIRQPin, HIGH); //enable pullup resistor but already done on touchboard
  attachInterrupt(touchboardIRQNumber, mpr121Interrupt, LOW); // MPR121 IRQ goes low
}
#endif

/**
 * loop
 *
 * Arduino main loop
 */
void loop() 
{
// Sensor SENSOR data
  if (millis() >= nextUpdate) {
    nextUpdate = millis() + sensorDelay * 1000L;
#ifdef SENSOR
    getRegister(REGI_SENSOR)->getData();
#endif
  }

// Handle touch board event
  if (touchboardIRQ)
  {
#ifdef DEBUG
    Serial.println("Mpr121 touched");
#endif
    //read the touch state from the MPR121
    Wire.requestFrom(touchboardAddress, 2); 
    byte LSB = Wire.read();
    byte MSB = Wire.read();
    word touched = word(MSB, LSB); // 16bits that make up the touch states
  
    for (int i = 0; i < touchboardKeyNb; i++)
    {
      // Check what electrodes were touched
      if(touched & (1 << i))
      {
        // Electrode is touched
        if(touchStates[i] == 0)
        {
          touchStatesChange[i] = 1;
  #ifdef DEBUG
          Serial.print("pin ");Serial.print(i);Serial.println(" was just touched");
  #endif
        }
        touchStates[i] = 1;      
      }
      else
      {
        // Electrode is not touched
        if(touchStates[i] == 1)
        {
          touchStatesChange[i] = 1;
  #ifdef DEBUG
          Serial.print("pin ");Serial.print(i);Serial.println(" is no longer being touched");
  #endif
        }
        touchStates[i] = 0;
      }
    }
    //Ready to receive new PC interrupts
    touchboardIRQ = false;
    for(int i = 0; i < touchboardKeyNb; i++) 
    {
      if(touchStatesChange[i] == 1) 
      {
        // Transmit touch data
        //getRegister(REGI_TOUCH_START_IDX + i)->getData();
        SWPACKET packet = SWPACKET();
        packet.destAddr = dtTouchConfig[i * touchConfigLength + 1];
        packet.srcAddr = panstamp.swapAddress;
        packet.hop = 0;
        packet.security = panstamp.security & 0x0F;
        packet.nonce = ++panstamp.nonce;
        packet.function = SWAPFUNCT_CMD;
        packet.regAddr = dtTouchConfig[i * touchConfigLength + 1];
        packet.regId = dtTouchConfig[i * touchConfigLength + 2];
        packet.value.length = 1;
        packet.value.data = toggleCommandValue;
        packet.value.type = SWDTYPE_OTHER;
        packet.send();
      }
    }
  }

  // Sleep for panstamp.txInterval seconds (register 10)
  //panstamp.goToSleep();
}

void mpr121Setup(void)
{
  mpr121SetRegister(touchboardAddress, ELE_CFG, 0x00); 

  // Section A - Controls filtering when data is > baseline.
  mpr121SetRegister(touchboardAddress, MHD_R, 0x01);
  mpr121SetRegister(touchboardAddress, NHD_R, 0x01);
  mpr121SetRegister(touchboardAddress, NCL_R, 0x00);
  mpr121SetRegister(touchboardAddress, FDL_R, 0x00);

  // Section B - Controls filtering when data is < baseline.
  mpr121SetRegister(touchboardAddress, MHD_F, 0x01);
  mpr121SetRegister(touchboardAddress, NHD_F, 0x01);
  mpr121SetRegister(touchboardAddress, NCL_F, 0xFF);
  mpr121SetRegister(touchboardAddress, FDL_F, 0x02);

  // Section C - Sets touch and release thresholds for each electrode
  mpr121SetRegister(touchboardAddress, ELE0_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE0_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE1_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE1_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE2_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE2_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE3_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE3_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE4_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE4_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE5_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE5_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE6_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE6_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE7_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE7_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE8_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE8_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE9_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE9_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE10_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE10_R, REL_THRESH);

  mpr121SetRegister(touchboardAddress, ELE11_T, TOU_THRESH);
  mpr121SetRegister(touchboardAddress, ELE11_R, REL_THRESH);

  // Section D
  // Set the Filter Configuration
  // Set ESI2
  mpr121SetRegister(touchboardAddress, FIL_CFG, 0x04);

  // Section E
  // Electrode Configuration
  // Set ELE_CFG to 0x00 to return to standby mode
  mpr121SetRegister(touchboardAddress, ELE_CFG, 0x0C);  // Enables all 12 Electrodes

  // Section F
  // Enable Auto Config and auto Reconfig
  /*mpr121SetRegister(touchboardAddress, ATO_CFG0, 0x0B);
   mpr121SetRegister(touchboardAddress, ATO_CFGU, 0xC9);  // USL = (Vdd-0.7)/vdd*256 = 0xC9 @3.3V   mpr121SetRegister(touchboardAddress, ATO_CFGL, 0x82);  // LSL = 0.65*USL = 0x82 @3.3V
   mpr121SetRegister(touchboardAddress, ATO_CFGT, 0xB5);*/  // Target = 0.9*USL = 0xB5 @3.3V

  mpr121SetRegister(touchboardAddress, ELE_CFG, 0x0C);
}

void mpr121SetRegister(int address, unsigned char r, unsigned char v)
{
  Wire.beginTransmission(address);
  Wire.write(r);
  Wire.write(v);
  Wire.endTransmission();
}

