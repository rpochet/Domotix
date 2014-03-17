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
#include "sensor.h"

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
 * dtSensorDelay: 2 bytes
 * dtSensor: temperature, 4 bytes
 */
REGISTER regSensorDelay((unsigned char*)dtSensorDelay, sizeof(dtSensorDelay), NULL, &setSensorDelay, SWDTYPE_INTEGER, EEPROM_FIRST_CUSTOM);

// Sensor value register
REGISTER regSensor(dtSensor, sizeof(dtSensor), &updtSensor, NULL);

// Touch value register
REGISTER regTouch0((unsigned char*)(*touchStates), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch1((unsigned char*)(*touchStates + 1), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch2((unsigned char*)(*touchStates + 2), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch3((unsigned char*)(*touchStates + 3), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch4((unsigned char*)(*touchStates + 4), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch5((unsigned char*)(*touchStates + 5), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch6((unsigned char*)(*touchStates + 6), TOUCH_STATE_LENGTH, NULL, NULL);
REGISTER regTouch7((unsigned char*)(*touchStates + 7), TOUCH_STATE_LENGTH, NULL, NULL);

/**
 * Initialize table of registers
 */
DECLARE_REGISTERS_START()
  // Pointers to the custom registers
  &regSensorDelay,
  &regSensor
DECLARE_REGISTERS_END()

/**
 * Definition of common getter/setter callback functions
 */
DEFINE_COMMON_CALLBACKS()

/**
 * setSensorDelay
 *
 * rId: register ID
 * value: new register value
*/
const void setSensorDelay(byte rId, byte *value)
{
  /* Set new Sensor Delay. BE to LE conversion */
  regSensorDelay.setValueFromBeBuffer(value);
}

/**
 * updtSensor
 *
 * Measure sensor data and update register
 *
 * rId: Register ID
 */
const void updtSensor(byte rId)
{
  #ifdef TEMPHUM
  // Read temperature and humidity from sensor
  sensor_ReadTempHum();
  #elif TEMP
  // Read temperature from sensor
  //sensor_ReadTemp();
  #elif TEMPPRESS
  // Read temperature and pressure from sensor
  sensor_ReadTempPress();
  #endif
}



