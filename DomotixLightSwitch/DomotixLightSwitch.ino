/**
 * DomotixLightSwitch.ino
 *
 * Device measuring temperature from an I2C ??? sensor.
 *
 * Product name: DomotixLightSwitch (Domotix light switch with temperature sensor)
 * Author: Pochet Romuald
 * Creation date: 12 Nov 2013
 */

#include "HardwareSerial.h"
#include "EEPROM.h"
#include "Wire.h"
#include "MPR121.h"
#include "regtable.h"
#include "swap.h"
#include "Adafruit_MCP9808.h"

#define DEBUG
#define USE_INTERRUPT
/**
 * Uncomment if you are reading Vcc from A0. All battery-boards do this
 */
#define VOLT_SUPPLY_A0   1

/**
 * Macros
 */
#ifdef USE_INTERRUPT
#define enableINT1irq()          attachInterrupt(touchboardIRQNumber, mpr121Interrupt, FALLING)
#define disableINT1irq()         detachInterrupt(touchboardIRQNumber)
#endif


/**
 * SENSOR
 */
byte dtSensorDelay[2];
unsigned long sensorDelay = 10; // s
unsigned long nextUpdate;
const int sensorAddress = 0x48;
Adafruit_MCP9808 tempsensor = Adafruit_MCP9808(); // Create the MCP9808 temperature sensor object with default address 0x18

/**
 * TOUCH BOARD
 */
const int touchboardAddress = 0x5A;
int touchboardIRQPin = 3;                 // PD3, if change update pcEnableInterrupt, pcDisableInterrupt and PCINTMASK0
int touchboardIRQNumber = 1;              // INT1
#ifdef USE_INTERRUPT
volatile boolean touchboardIRQ = false;    // Set when touch event is available
#else
volatile boolean touchboardIRQ = true;    // Set when touch event is available
#endif
const int touchboardKeyNb = 12;
const mpr121_proxmode_t mpr121ProxMode = PROX0_11;
MPR121_settings_t mpr121Settings;

#define SWAPFUNCT_LIGHT     SWAPFUNCT_CMD1
#define SWAPREG_OUTPUTS     14
#define SWAPFUNCT_LED       SWAPFUNCT_CMD3
#define SWAPREG_LEDS        0
const byte lightOutputOff = 0;
const byte lightOutputOn = 254;
const byte lightOutputToggle = -1;

/**
 * TOUCH: 0, addr, light
 * LED: 1, dim, init
 */
const int touchConfigLength = 3;
static byte dtTouchConfig[touchboardKeyNb * touchConfigLength];


/**
 * swapReceived
 *
 * Function automatically called by the panStamp API whenever a SWAP 
 * packet is received
 *
 * 'swap'    SWAP packet received
 *
 *   swap->function: Function
 *   swap->regAddr: Register address
 *   swap->regId: Register ID
 *   swap->srcAddr: Source address
 *   swap->value.length: Length of data field
 *   swap->value.data: Array of data bytes
 */
/*void swapReceived(SWPACKET *swap)
{
  short i;
  
   if ((swap->function == SWAPFUNCT_STA) & (swap->regId == SWAPREG_OUTPUTS))
   {
       for(i = 0; i < touchboardKeyNb; i++) 
       {
           if(dtTouchConfig[i * touchConfigLength + 1] == swap->regAddr)
           {
               if(swap->value.data[dtTouchConfig[i * touchConfigLength + 2]] > 0)
               {
                   // Switch LED On
               }
           }
       }
   }
   else if (swap->function == SWAPFUNCT_CMD3)
     ;
}*/

/**
 * setup
 *
 * Arduino setup function
 */
void setup()
{
    int i = 0;
    
#ifdef DEBUG
    Serial.begin(115200);
    delay(2000); // to allow starting serial console
#endif

    //eepromToDefaults();
    
    // Init panStamp
    //panstamp.init(CFREQ_868);  // Not necessary unless you want a different frequency

    // Init swap
    swap.init();
    
    nextUpdate = millis();
    
    swap.getRegister(REGI_PRODUCTCODE)->getData();
    if(swap.devAddress == CC1101_DEFVAL_ADDR) {
        swap.getRegister(REGI_HWVERSION)->getData();
        swap.getRegister(REGI_FWVERSION)->getData();
    }
    
#ifdef DEBUG
    Serial.println(sensorDelay);
    Serial.println(freeRam());
#endif
    
    // panstamp init only read register value from EEPROM, 
    // so initialize other dependant value here
    initRegister();
    
    initSensor();

    initMpr121();
      
    // Enter SYNC state
    swap.enterSystemState(SYSTATE_SYNC);
  
    // During 3 seconds, listen the network for possible commands whilst the LED blinks
    for(i = 0 ; i < 6 ; i++)
    {
        //digitalWrite(ledPin, LOW);
        delay(400);
        //digitalWrite(ledPin, HIGH);
        delay(100);
    }
  
    // Transmit configuration
    swap.getRegister(REGI_TXINTERVAL)->getData();
    swap.getRegister(REGI_VOLTSUPPLY)->getData(); 
    swap.getRegister(REGI_SENSOR_DELAY)->getData();
    swap.getRegister(REGI_TOUCH_CONFIG)->getData();
    
    // Switch to Rx OFF state
    // TODO set to OFF as it is a sleeping device
    swap.enterSystemState(SYSTATE_RXON);
    
    //swap.attachInterrupt(OTHER, swapReceived);
    
#ifdef DEBUG
    Serial.println(freeRam());
#endif
}

/**
 *
 */
void eepromToDefaults()
{
    STORAGE nvMem;
  
    int i;
    eepromToFactoryDefaults();

    uint8_t address[] = {0, 4};
    nvMem.write(address, DEFAULT_NVOLAT_SECTION, NVOLAT_DEVICE_ADDR, sizeof(address));
    
    uint8_t sensorDelay[] = {0, 0};
    nvMem.write(sensorDelay, DEFAULT_NVOLAT_SECTION, NVOLAT_CONFIG_SENSOR_DELAY, sizeof(sensorDelay));
    
    uint8_t touchConfig[] = {0, 2, 0};
    for(i = 0 ; i < touchboardKeyNb; i++)
    {
        touchConfig[2] = i;
        nvMem.write(touchConfig, DEFAULT_NVOLAT_SECTION, NVOLAT_TOUCH_CONFIG + i * touchConfigLength, sizeof(touchConfig));
    }
}

/**
 *
 */
int freeRam() 
{
    extern int __heap_start, *__brkval; 
    int v; 
    return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

/**
 *
 */
void initRegister() 
{
    sensorDelay = word(dtSensorDelay[1], dtSensorDelay[0]);
    if(sensorDelay == 0 || sensorDelay == 4294967295) 
    {
        sensorDelay = 3600; // 0x0E10
        EEPROM.write(NVOLAT_CONFIG_SENSOR_DELAY + 1, 0x0E);
        EEPROM.write(NVOLAT_CONFIG_SENSOR_DELAY, 0x10);
    }
  
#ifdef DEBUG
    Serial.print("sensorDelay: ");
    Serial.println(sensorDelay);
#endif
}

/**
 *
 */
void initSensor() 
{
#ifdef DEBUG
    Serial.print("Init sensor... ");
#endif

    if (!tempsensor.begin()) {
        Serial.println("Couldn't find MCP9808!");
        while (1);
    }
    
#ifdef DEBUG
    Serial.print(tempsensor.readTempC());
    Serial.println(" OK");
#endif
}

/**
 * Init MPR121 
 * - Setup IRQ pin, FALLING
 * - Setup MPR121
 */
void initMpr121() 
{
    short electrode = 0;
    short numDigPins = 0;
    
#ifdef DEBUG
    Serial.println("Init MPR121... ");
#endif
    
    for (uint8_t i = 0; i < touchboardKeyNb; i++) 
    {
        if(dtTouchConfig[i * touchConfigLength] == 0) 
        {
#ifdef DEBUG
    Serial.print(i);
    Serial.println(" is electrode");
#endif
            // Electrode
            electrode++;
        }
        else
        {
#ifdef DEBUG
    Serial.print(i);
    Serial.println(" is LED");
#endif
            // LED
            numDigPins++;
            MPR121.pinMode(i, OUTPUT_HS);
        }
    }

    MPR121.setNumElectrodes(electrode);
    //MPR121.setNumDigPins(numDigPins);
    MPR121.setProxMode(mpr121ProxMode);
    
    if (!MPR121.begin(touchboardAddress)) {
        Serial.println("MPR121 not found, check wiring?");
        while (1);
    }
    
#ifdef DEBUG
    Serial.print(electrode);
    Serial.print(" ");
    Serial.print(numDigPins);
    Serial.print(" ");
    Serial.print(MPR121.getRegister(ECR), HEX);
#endif
    
#ifdef USE_INTERRUPT
#ifdef DEBUG
    Serial.println("Use interrupt");
#endif
        
    pinMode(touchboardIRQPin, INPUT);
    //digitalWrite(touchboardIRQPin, HIGH); //enable pullup resistor but already done on touchboard
    enableINT1irq();
#endif

#ifdef DEBUG
    Serial.println(" OK");
#endif
}

/**
 * MPR121 Interrupt 
 * 
 */
#ifdef USE_INTERRUPT
void mpr121Interrupt(void) 
{
    // Wake Up and SYSTATE_RXON
    //panstamp.wakeUp();
    
    touchboardIRQ = true;
}
#endif

/**
 * loop
 *
 * Arduino main loop
 */
void loop() 
{
    static byte commandValue[2];
    
    // Sensor SENSOR data
    if (millis() >= nextUpdate)
    {
        nextUpdate = millis() + sensorDelay * 1000L;
        swap.getRegister(REGI_SENSOR)->getData();
    }
  
    if (touchboardIRQ)
    {
        // Get the currently touched pads
        MPR121.updateTouchData();
        for (uint8_t i = 0; i < touchboardKeyNb; i++) 
        {
            if(MPR121.isNewRelease(i)) 
            {
                // Transmit touch data
                SWPACKET packet = SWPACKET();
                packet.destAddr = dtTouchConfig[i * touchConfigLength + 1];
                packet.srcAddr = swap.devAddress;
                packet.hop = 0;
                packet.security = swap.security & 0x0F;
                packet.nonce = ++swap.nonce;
                packet.function = SWAPFUNCT_LIGHT;
                packet.regAddr = dtTouchConfig[i * touchConfigLength + 1];
                packet.regId = SWAPREG_OUTPUTS;
                packet.value.length = 2;
                commandValue[0] = dtTouchConfig[i * touchConfigLength + 2];
                commandValue[1] = -1;
                packet.value.data = commandValue;
                packet.value.type = SWDTYPE_OTHER;
                packet.send();
                
            #ifdef DEBUG
                Serial.print(i); 
                Serial.println(" sent");
            #endif
            }
        }
        
        //Ready to receive new PC interrupts
    #ifdef USE_INTERRUPT
        touchboardIRQ = false;
    
        // Sleep for panstamp.txInterval seconds (register 10)
        //swap.goToSleep();
    #else
        delay(100);
    #endif
    }
}

/*void debug()
{
    // debugging info, what
    Serial.print("\t\t\t\t\t\t\t\t\t\t\t\t\t Ox"); 
    Serial.println(cap.touched(), HEX);
    Serial.print("Filt: ");
    for (uint8_t i=0; i<12; i++) {
        Serial.print(cap.filteredData(i)); Serial.print("\t");
    }
    Serial.println();
    Serial.print("Base: ");
    for (uint8_t i=0; i<12; i++) {
        Serial.print(cap.baselineData(i)); Serial.print("\t");
    }
    Serial.println();
    
    // put a delay so it isn't overwhelming
    delay(100);
}
*/
