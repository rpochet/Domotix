/**
 * regtable.ino
 *
 * List of registers. Definition and handlers.
 *
 * Product name: DomotixLightSwitch (Domotix light controller with temperature sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */
#include <EEPROM.h>
#include "product.h"
#include "panstamp.h"
#include "regtable.h"

/**
 * Declaration of common callback functions
 */
DECLARE_COMMON_CALLBACKS()

/**
 * Definition of common registers
 */
DEFINE_COMMON_REGISTERS()

/*
 * Definition of custom registers
 *
 * dtVoltSupply: 2 bytes
 * dtSensorDelay: 2 bytes
 * dtSensor: temperature, 2 bytes
 * dtoutputX: level, 1 byte
 */
static byte dtVoltSupply[2];
REGISTER regVoltSupply(dtVoltSupply, sizeof(dtVoltSupply), &updtVoltSupply, NULL);

REGISTER regSensorDelay(dtSensorDelay, sizeof(dtSensorDelay), NULL, &updtSensorDelay, SWDTYPE_INTEGER, EEPROM_CONFIG_SENSOR_DELAY);

static byte dtSensor[2];
REGISTER regSensor(dtSensor, sizeof(dtSensor), &updtSensor, NULL);

REGISTER regTouchConfig(dtTouchConfig, sizeof(dtTouchConfig), NULL, NULL, SWDTYPE_OTHER, EEPROM_TOUCH_CONFIG);

/**
 * Initialize table of registers
 */
DECLARE_REGISTERS_START()
  // Pointers to the custom registers
  &regVoltSupply,
  &regSensorDelay,
  &regSensor,
  &regTouchConfig
DECLARE_REGISTERS_END()

/**
 * Definition of common getter/setter callback functions
 */
DEFINE_COMMON_CALLBACKS()

/**
 * updtVoltSupply
 *
 * Measure voltage supply and update register
 *
 * 'rId'  Register ID
 */
const void updtVoltSupply(byte rId)
{
  unsigned short result;
  
  // Read 1.1V reference against AVcc
  ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Convert
  while (bit_is_set(ADCSRA,ADSC));
  result = ADCL;
  result |= ADCH << 8;
  result = 1126400L / result; // Back-calculate AVcc in mV

  /**
   * register[eId]->member can be replaced by regVoltSupply in this case since
   * no other register is going to use "updtVoltSupply" as "updater" function
   */

  // Update register value
  regTable[rId]->value[0] = (result >> 8) & 0xFF;
  regTable[rId]->value[1] = result & 0xFF;
}

/**
 * updtSensorDelay
 *
 * rId: register ID
 * value: new register value
*/
const void updtSensorDelay(byte rId, byte *value)
{
  regSensorDelay.setValueFromBeBuffer(value);
  initRegister();
}

/**
 * Measure sensor data and update register
 *
 * rId: Register ID
 */
const void updtSensor(byte rId)
{
  int temp;
#if SENSOR == SENSOR_MCP9808
  temp = (tempsensor.readTempC() * 100);
#else
  Wire.requestFrom(sensorAddress, 2); 

  byte MSB = Wire.read();
  byte LSB = Wire.read();

  Serial.print(MSB);
  Serial.print('-');
  Serial.println(LSB);
  
  //it's a 12bit int, using two's compliment for negative
  temp = ((MSB << 8) | LSB) >> 4; 
  //But if we want, we can convert this directly to a celsius temp reading
  //temp *= 0.0625; //This is the same as a divide by 16
  //temp >>= 4; //Which is really just a shift of 4 so it's much faster and doesn't require floating point
  //Shifts may not work with signed ints (negative temperatures). Let's do a divide instead
  //temp /= 16;
  temp *= 6.25; // 0.01 °C
#endif  
  
  dtSensor[0] = (temp >> 8) & 0xFF;
  dtSensor[1] = temp & 0xFF;
}
