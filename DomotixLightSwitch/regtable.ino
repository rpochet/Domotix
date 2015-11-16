/**
 * regtable.ino
 *
 * List of registers. Definition and handlers.
 *
 * Product name: DomotixLightSwitch (Domotix light controller with temperature sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */
#include "product.h"
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
// Voltage supply
static unsigned long voltageSupply = 3300;
static byte dtVoltSupply[2];
REGISTER regVoltSupply(dtVoltSupply, sizeof(dtVoltSupply), &updtVoltSupply, NULL);

REGISTER regSensorDelay(dtSensorDelay, sizeof(dtSensorDelay), NULL, &updtSensorDelay, SWDTYPE_INTEGER, NVOLAT_CONFIG_SENSOR_DELAY);

static byte dtSensor[2];
REGISTER regSensor(dtSensor, sizeof(dtSensor), &updtSensor, NULL);

REGISTER regTouchConfig(dtTouchConfig, sizeof(dtTouchConfig), NULL, NULL, SWDTYPE_OTHER, NVOLAT_TOUCH_CONFIG);

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
  unsigned long result;

  #ifdef VOLT_SUPPLY_A0 
  // Read voltage supply from A0
  unsigned short ref = voltageSupply;
  result = analogRead(A0);
  result *= ref;
  result /= 4095;
  #else
  result = panstamp.getVcc();
  #endif

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
    int temp = (tempsensor.readTempC() * 100);
    dtSensor[0] = (temp >> 8) & 0xFF;
    dtSensor[1] = temp & 0xFF;
}
