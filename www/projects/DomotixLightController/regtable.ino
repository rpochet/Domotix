/**
 * regtable.ino
 *
 * List of registers. Definition and handlers.
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */
#include "product.h"
#include "swap.h"
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
 * dtSensorDelay: 2 bytes
 * dtSensor: temperature, 4 bytes / pressure, 4 bytes
 * dtPulseWidth: 2 bytes
 * dtoutputX: level, 1 byte
 */

REGISTER regSensorDelay(dtSensorDelay, sizeof(dtSensorDelay), NULL, &updtSensorDelay, SWDTYPE_INTEGER, NVOLAT_CONFIG_SENSOR_DELAY);

byte dtSensor[6];
REGISTER regSensor(dtSensor, sizeof(dtSensor), &updtSensor, NULL);

REGISTER regPulseWidth(dtPulseWidth, sizeof(dtPulseWidth), NULL, &updtPulseWidth, SWDTYPE_INTEGER, NVOLAT_CONFIG_PULSE_WIDTH);

REGISTER regOutputs(dtOutputs, sizeof(dtOutputs), NULL, NULL, SWDTYPE_OTHER, NVOLAT_CONFIG_OUTPUTS);


/**
 * Initialize table of registers
 */
DECLARE_REGISTERS_START()
  // Pointers to the custom registers
  &regSensorDelay,
  &regSensor,
  &regPulseWidth,
  &regOutputs
DECLARE_REGISTERS_END()

/**
 * Definition of common getter/setter callback functions
 */
DEFINE_COMMON_CALLBACKS()

/**
 * updtSensorDelay
 *
 * rId: register ID
 * value: new register value
*/
const void updtSensorDelay(byte rId, byte *value)
{
    // Set new Sensor Delay. BE to LE conversion
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
    float temperature;
    float pressurePa;
    int temperature2send;
    long pressure2send;
  
    bmp.getTemperature(&temperature);
    temperature2send = (int) (temperature * 100);
    dtSensor[0] = (temperature2send >> 8) & 0xFF;
    dtSensor[1] = temperature2send & 0xFF;
    
    bmp.getPressure(&pressurePa);
    pressure2send = (long) (pressurePa);
    dtSensor[2] = (pressure2send >> 24) & 0xFF;
    dtSensor[3] = (pressure2send >> 16) & 0xFF;
    dtSensor[4] = (pressure2send >> 8) & 0xFF;
    dtSensor[5] = pressure2send & 0xFF;
}

/**
 * updtPulseWidth
 *
 * rId: register ID
 * value: new register value
*/
const void updtPulseWidth(byte rId, byte *value)
{
    // Set new Pulse Width. BE to LE conversion
    regPulseWidth.setValueFromBeBuffer(value);
    initRegister();
}

