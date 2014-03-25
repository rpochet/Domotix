/**
 * DomotixLightController.ino
 *
 * Device measuring temperature and barometric pressure from
 * an I2C BMP085 sensor. This sketch makes use of Adafruit's BMP085 library
 * for Arduino: http://learn.adafruit.com/bmp085/using-the-bmp085
 * Pins: I2C port and PIN_PWRPRESS (Power pin)
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */

#include "regtable.h"
#include "panstamp.h"
#include "Wire.h"
#include "I2C_RL812L.h"
#include "Adafruit_Sensor.h"
#include "Adafruit_BMP085.h"


/**
 * LED pin
 */
#define LEDPIN         4
//#define DEBUG


/**
 * SENSOR
 */
static byte dtSensorDelay[2];
unsigned long sensorDelay; // s 60*10*3 = 3h
unsigned long nextUpdate;

#define TEMPPRESS        1     // Temperature + Pressure sensor = BMP085
#define BMP085_ID        10085
Adafruit_BMP085 bmp = Adafruit_BMP085(BMP085_ID);

/**
 * OUTPUT BOARD
 */
const byte PCF8574_0 = 0x48;
const byte PCF8574_1 = 0x44;
const byte PCF8574_2 = 0x40;
const byte PCF8574_3 = 0x4c;
const byte OUTPUT_PER_BOARD = 8;
const byte boardsAddr[] = { PCF8574_0, PCF8574_1, PCF8574_2, PCF8574_3 };

static byte dtPulseWidth[2];
unsigned long pulseWidth; // ms
unsigned int startCounter;

I2C_RL812L board1 = I2C_RL812L(PCF8574_0);
I2C_RL812L board2 = I2C_RL812L(PCF8574_1);
I2C_RL812L board3 = I2C_RL812L(PCF8574_2);
I2C_RL812L board4 = I2C_RL812L(PCF8574_3);
I2C_RL812L boards[] = { board1, board2, board3, board4 };

unsigned int currentOutput;
unsigned int currentOutputBoard;
unsigned int currentOutputInBoard;

boolean bStartPulse = false;
boolean bStopPulse = false;

ISR(TIMER1_OVF_vect)
{
    bStopPulse = true;
    
    TCCR1B &= ~(_BV(CS10) | _BV(CS11) | _BV(CS12));
}

/**
 * setup
 *
 * Arduino setup function
 */
void setup()
{
    //unsigned char clockSelectBits;
    int i;
    pinMode(LEDPIN, OUTPUT);
  
    // Init panStamp
    //eepromToFactoryDefaults();
    panstamp.init();
  
    // panstamp init only read register value from EEPROM, 
    // so initialize other dependant value here
    initRegister();
    
    // Enter SYNC state
    panstamp.enterSystemState(SYSTATE_SYNC);
  
    // During 3 seconds, listen the network for possible commands whilst the LED blinks
    for(i = 0 ; i < 6 ; i++)
    {
      digitalWrite(LEDPIN, LOW);
      delay(400);
      digitalWrite(LEDPIN, HIGH);
      delay(100);
    }
    
    // initialize timer1 
    TCCR1A = 0;               // set TCCR1A register to 0
    TCCR1B = 0;               // same for TCCR1B
    startCounter = 0x0000 - (F_CPU / 256 * pulseWidth /1000);
    
    delay(1000);

#ifdef DEBUG
    Serial.begin(115200);
    Serial.println(pulseWidth);
    Serial.println(startCounter);
    Serial.println(sensorDelay);
    Serial.println(freeRam());
#endif

    digitalWrite(LEDPIN, HIGH);
  
    // Initialize BMP085 boards
    initSensor();
  
    // Initialize PCF8574 boards
    initBoards();
  
    nextUpdate = millis(); 
  
    // Transmit configuration
    getRegister(REGI_PRODUCTCODE)->getData();
    getRegister(REGI_TXINTERVAL)->getData();
    getRegister(REGI_PULSE_WIDTH)->getData();
    getRegister(REGI_SENSOR_DELAY)->getData();
  
    // Switch to Rx OFF state
    panstamp.enterSystemState(SYSTATE_RXON);
  
    digitalWrite(LEDPIN, LOW);
}

int freeRam() {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void initRegister() 
{
    pulseWidth = dtPulseWidth[1] << 8 | dtPulseWidth[0];
    if(pulseWidth == 0) 
    {
        pulseWidth = 1000;
    }
    sensorDelay = dtSensorDelay[1] << 8 | dtSensorDelay[0];
    if(sensorDelay == 0) 
    {
      sensorDelay = 3600;
    }
}

void initSensor() 
{
#ifdef DEBUG
  Serial.print("Init sensor... ");
#endif

    // Initialise the sensor
    if(!bmp.begin())
    {
        // There was a problem detecting the BMP085 ... check your connections
#ifdef DEBUG
        Serial.println("Ooops, no BMP085 detected ... Check your wiring or I2C ADDR!");
#endif
        while(1);
    }

#ifdef DEBUG
    // Display some basic information on this sensor
    sensor_t sensor;
    bmp.getSensor(&sensor);
    Serial.println("------------------------------------");
    Serial.print  ("Sensor:       "); 
    Serial.println(sensor.name);
    Serial.print  ("Driver Ver:   "); 
    Serial.println(sensor.version);
    Serial.print  ("Unique ID:    "); 
    Serial.println(sensor.sensor_id);
    Serial.print  ("Max Value:    "); 
    Serial.print(sensor.max_value); 
    Serial.println(" hPa");
    Serial.print  ("Min Value:    "); 
    Serial.print(sensor.min_value); 
    Serial.println(" hPa");
    Serial.print  ("Resolution:   "); 
    Serial.print(sensor.resolution); 
    Serial.println(" hPa");  
    Serial.println("------------------------------------");
    Serial.println("");
    Serial.println("Done");
#endif
}

void initBoards()
{
    byte s;
  
#ifdef DEBUG
  Serial.print("Init boards... ");
#endif
  
    board1.init();
    board2.init();
    board3.init();
    board4.init();
  
#ifdef DEBUG
    Serial.print("Test board1... ");
    s = board1.getRelay(0);
    Serial.print("Relay 0: ");
    Serial.println(s);
    board1.toggleRelay(0);
    s = board1.getRelay(0);
    Serial.print("Relay 0: ");
    Serial.println(s);
    board1.toggleRelay(0);
    s = board1.getRelay(0);
    Serial.print("Relay 0: ");
    Serial.println(s);
    Serial.println("OK");
#endif
}

/**
 * loop
 *
 * Arduino main loop
 */
void loop()
{
    if (millis() >= nextUpdate) {
        
        // time to send sensor register
        nextUpdate = millis() + sensorDelay * 1000L;
        
        getRegister(REGI_SENSOR)->getData();
    }
    
    if(bStartPulse)
    {
        bStartPulse = false;
  
        byte x;
        
    #ifdef DEBUG
        Serial.println("Pulse started...");
    #endif
    
        digitalWrite(LEDPIN, HIGH);
        
        noInterrupts();           // disable all interrupts
        TCNT1 = startCounter;     // initialize counter value to 0
        TCCR1B |= _BV(CS12);      // Set CS10 and CS12 bits for 1024 prescaler
        TIMSK1 |= (1 << TOIE1);   // enable overflow interrupt
        interrupts();
        
        x |= (1 << currentOutputInBoard);  //Toogle n bit position to 1
        Wire.beginTransmission(boardsAddr[currentOutputBoard]); 
        Wire.write(x);
        Wire.endTransmission();
      
        //boards[currentOutputBoard].setRelay(currentOutputInBoard, 1);
    }
    
    if(bStopPulse)
    {
        bStopPulse = false;
        stopPulse();
    }
}

void startPulse()
{  
    bStartPulse = true;
  
    #ifdef DEBUG
        Serial.println(currentOutput);
        Serial.println(currentOutputBoard);
        Serial.println(currentOutputInBoard);
    #endif
}

void stopPulse()
{
    byte x;
    
#ifdef DEBUG
    Serial.println("Pulse stopped");
#endif

    digitalWrite(LEDPIN, LOW);
    
    x &= ~(1 << currentOutputInBoard); //Toogle n bit position to 0
    Wire.beginTransmission(boardsAddr[currentOutputBoard]); 
    Wire.write(x);
    Wire.endTransmission();
    
    //boards[currentOutputBoard].toggleRelay(currentOutputInBoard);
}


