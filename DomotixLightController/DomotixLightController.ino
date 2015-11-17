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

#include "HardwareSerial.h"
#include "regtable.h"
#include "swap.h"
#include "domotix.h"
#include "Wire.h"
#include "Adafruit_Sensor.h"
#include "Adafruit_BMP085.h"

//#define DEBUG
/**
 * Uncomment if you are reading Vcc from A0. All battery-boards do this
 */
#define VOLT_SUPPLY_A0   1

/**
 * LED pin
 */
#define ledPin         4

/**
 * SENSOR
 */
byte dtSensorDelay[2];
unsigned long sensorDelay; // s
unsigned long nextUpdate;

#define TEMPPRESS        1     // Temperature + Pressure sensor = BMP085
#define BMP085_ID        10085
Adafruit_BMP085 bmp = Adafruit_BMP085(BMP085_ID);

/**
 * OUTPUT BOARD
 */
const byte PCF8574_0 = 0x4E;
const byte PCF8574_1 = 0x4C;
const byte PCF8574_2 = 0x4A;
const byte PCF8574_3 = 0x48;
const byte OUTPUT_PER_BOARD = 8;
const byte boardsAddr[] = { PCF8574_0 >> 1, PCF8574_1 >> 1, PCF8574_2 >> 1, PCF8574_3 >> 1 };

byte dtPulseWidth[2];
unsigned long pulseWidth; // ms
unsigned int startCounter;
boolean bStoreOutputs = false;

static byte dtOutputs[32];
unsigned int currentOutput;
unsigned int currentOutputValue;
unsigned int currentOutputBoard;
unsigned int currentOutputInBoard;

boolean bStartPulse = false;
volatile boolean bStopPulse = false;

/**
 * Timer1 overflow interrupt routine
 *
 */
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
    int i;
    
#ifdef DEBUG
    Serial.begin(115200);
    delay(2000); // to allow starting serial console
#endif

    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, HIGH);
    
    //eepromToDefaults();
    
    // Init panStamp
    //panstamp.init(CFREQ_868);  // Not necessary unless you want a different frequency
    panstamp.setHighTxPower();

    // Init swap
    swap.init();
    
    // Declare callback function for dispatching the incoming SWAP packets
    //panstamp.setSwapPacketCallBack(swapPacketReceived);
    swap.attachInterrupt(OTHER, swapPacketReceived);
    
    swap.getRegister(REGI_PRODUCTCODE)->getData();
    if(swap.devAddress == CC1101_DEFVAL_ADDR) {
        swap.getRegister(REGI_HWVERSION)->getData();
        swap.getRegister(REGI_FWVERSION)->getData();
    }
    
    // panstamp init only read register value from EEPROM, 
    // so initialize other dependant value here
    initRegister();
      
    // Enter SYNC state
    swap.enterSystemState(SYSTATE_SYNC);
    
    // During 3 seconds, listen the network for possible commands whilst the LED blinks
    for(i = 0 ; i < 6 ; i++)
    {
        digitalWrite(ledPin, LOW);
        delay(400);
        digitalWrite(ledPin, HIGH);
        delay(100);
    }
    
    // initialize timer1 
    TCCR1A = 0;               // set TCCR1A register to 0
    TCCR1B = 0;               // same for TCCR1B
    startCounter = 0x0000 - (F_CPU / 256 * pulseWidth /1000);

#ifdef DEBUG
    Serial.println(sensorDelay);
    Serial.println(pulseWidth);
    Serial.println(startCounter);
    Serial.println(freeRam());
#endif
  
    // Initialize BMP085 boards
    initSensor();
  
    // Initialize PCF8574 boards
    initBoards();
    
    nextUpdate = millis(); 
  
    // Transmit configuration
    swap.getRegister(REGI_TXINTERVAL)->getData();
    swap.getRegister(REGI_PULSE_WIDTH)->getData();
    swap.getRegister(REGI_SENSOR_DELAY)->getData();
    swap.getRegister(REGI_OUTPUTS)->getData();
  
    // Switch to Rx OFF state
    swap.enterSystemState(SYSTATE_RXON);
  
    digitalWrite(ledPin, LOW);
}

/**
 * Reset EPEPROM to default value
 *
 */
void eepromToDefaults()
{
    STORAGE nvMem;
  
    int i;
    eepromToFactoryDefaults();

    uint8_t address[] = {0, 2};
    nvMem.write(address, DEFAULT_NVOLAT_SECTION, NVOLAT_DEVICE_ADDR, sizeof(address));
    
    uint8_t sensorDelay[] = {0, 0};
    nvMem.write(sensorDelay, DEFAULT_NVOLAT_SECTION, NVOLAT_CONFIG_SENSOR_DELAY, sizeof(sensorDelay));
    
    uint8_t pulseWidth[] = {0, 0};
    nvMem.write(pulseWidth, DEFAULT_NVOLAT_SECTION, NVOLAT_CONFIG_SENSOR_DELAY, sizeof(pulseWidth));
    
    uint8_t outputs[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    nvMem.write(outputs, DEFAULT_NVOLAT_SECTION, NVOLAT_CONFIG_SENSOR_DELAY, sizeof(dtOutputs));
}

/**
 * Get the free ram
 *
 */
int freeRam() {
    extern int __heap_start, *__brkval; 
    int v; 
    return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

/**
 * Init Register
 * - convert panstamp register to real value
 * - store default value in EEPROM
 *
 */
void initRegister() 
{   
    pulseWidth = (dtPulseWidth[1] << 8) | dtPulseWidth[0];
    if(pulseWidth == 0) 
    {
        pulseWidth = 1000; // 0x0384
    }
    sensorDelay = (dtSensorDelay[1] << 8) | dtSensorDelay[0];
    if(sensorDelay == 0) 
    {
        sensorDelay = 3600; // 0x0E10
    }
}

/**
 * Init Sensor
 *
 */
void initSensor() 
{
#ifdef DEBUG
    Serial.println("Init sensor... ");
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

/**
 * Init Boards
 *
 */
void initBoards()
{
#ifdef DEBUG
    Serial.println("Init boards...");
#endif
    
    Wire.begin();
    
#ifdef DEBUG

    Wire.requestFrom(boardsAddr[0], (byte) 1); 
    Serial.println(Wire.read(), HEX);
    Wire.requestFrom(boardsAddr[1], (byte) 1); 
    Serial.println(Wire.read(), HEX);
    Wire.requestFrom(boardsAddr[2], (byte) 1); 
    Serial.println(Wire.read(), HEX);
    Wire.requestFrom(boardsAddr[3], (byte) 1); 
    Serial.println(Wire.read(), HEX);
    
    Wire.beginTransmission(boardsAddr[0]);
    Wire.write(0x00);
    Wire.endTransmission();
    delay(200);
    Wire.beginTransmission(boardsAddr[0]);
    Wire.write(0xff);
    Wire.endTransmission();
    
    Serial.println("Done");
#endif
}

/**
 * swapPacketReceived
 *
 * Function automatically called by the panStamp API whenever a SWAP
 * packet is received
 *
 * 'swPacket'    SWAP packet received
 */
void swapPacketReceived(SWPACKET *swPacket)
{
    if (swPacket->destAddr == swap.devAddress && swPacket->regId == REGI_OUTPUTS)
    {
        if(swPacket->function == SWAPFUNCT_LIGHT)
        {
        #ifdef DEBUG
            Serial.println("swap ok");
        #endif
    
            currentOutput = swPacket->value.data[0];
            currentOutputBoard = currentOutput / OUTPUT_PER_BOARD;
            currentOutputInBoard = currentOutput % OUTPUT_PER_BOARD;
            
            currentOutputValue = swPacket->value.data[1];
        #ifdef DEBUG
            Serial.println(dtOutputs[currentOutput]);
            Serial.println(currentOutputValue);
        #endif
            
            if(currentOutputValue == lightOutputToggle) // Toggle
            {
        #ifdef DEBUG
            Serial.println("toggle");
        #endif
                if(dtOutputs[currentOutput] == lightOutputOff)
                {
                    currentOutputValue = lightOutputOn;
                }
                else
                {
                    currentOutputValue = lightOutputOff;
                }
                requestPulse();
            } 
            else if(currentOutputValue == lightOutputOff && dtOutputs[currentOutput] != lightOutputOff) // Switch OFF
            {
        #ifdef DEBUG
            Serial.println("switchOff");
        #endif
                requestPulse();
            }
            else if(currentOutputValue == lightOutputOn && dtOutputs[currentOutput] != lightOutputOn) // Switch ON
            {
        #ifdef DEBUG
            Serial.println("switchOn");
        #endif
                requestPulse();
            }
        #ifdef DEBUG
            else 
            {
                Serial.println("Nothing");
            }
        #endif
        }
        else if(swPacket->function == SWAPFUNCT_LIGHT_RESET)
        {
            byte i;
            
            for(i = 0; i < sizeof(dtOutputs); i++)
            { 
                dtOutputs[i] = 0;
            }
            swap.getRegister(REGI_OUTPUTS)->setData(dtOutputs);
        #ifdef DEBUG
            Serial.println("reset");
        #endif
        }
    } else {
    #ifdef DEBUG
        Serial.println("Inv swap");
    #endif
    }
}
   
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
        swap.getRegister(REGI_SENSOR)->getData();
    }
    
    if(bStartPulse)
    {
        bStartPulse = false;
        startPulse();
    }
    
    if(bStopPulse)
    {
        bStopPulse = false;
        
        dtOutputs[currentOutput] = currentOutputValue;
        if(bStoreOutputs)
        {
            swap.getRegister(REGI_OUTPUTS)->setData(dtOutputs);
        }
        else
        {
            swap.getRegister(REGI_OUTPUTS)->getData();
        }
        stopPulse();
    }
}

void requestPulse()
{  
    bStartPulse = true;
  
    #ifdef DEBUG
        Serial.println("requestPulse");
        Serial.println(currentOutput);
        Serial.println(currentOutputBoard);
        Serial.println(currentOutputInBoard);
    #endif
}

void startPulse()
{
    byte x;
   
    x = 0xff ^ (1 << currentOutputInBoard);  //Toogle n bit position to 1
    
#ifdef DEBUG
    Serial.print("Pulse started...");
    Serial.println(x);
#endif

    digitalWrite(ledPin, HIGH);
    
    noInterrupts();           // disable all interrupts
    TCNT1 = startCounter;     // initialize counter value to 0
    TCCR1B |= _BV(CS12);      // Set CS10 and CS12 bits for 1024 prescaler
    TIMSK1 |= (1 << TOIE1);   // enable overflow interrupt
    interrupts();
    
    Wire.beginTransmission(boardsAddr[currentOutputBoard]);
    Wire.write(x);
    Wire.endTransmission();
}

void stopPulse()
{
#ifdef DEBUG
    Serial.println("Pulse stopped");
#endif
    
    Wire.beginTransmission(boardsAddr[currentOutputBoard]); 
    Wire.write(0xff);
    Wire.endTransmission();

    digitalWrite(ledPin, LOW);
}


