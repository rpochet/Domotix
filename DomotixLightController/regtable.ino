/**
 * regtable.ino
 *
 * List of registers. Definition and handlers.
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */
#include <EEPROM.h>
#include <Wire.h>
#include "product.h"
#include "panstamp.h"
#include "regtable.h"

/**
 * EEPROM addresses
 */
#define EEPROM_CONFIG_SENSOR_DELAY   EEPROM_FIRST_CUSTOM
#define EEPROM_CONFIG_PULSE_WIDTH    EEPROM_CONFIG_SENSOR_DELAY + 2

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

REGISTER regSensorDelay(dtSensorDelay, sizeof(dtSensorDelay), NULL, &updtSensorDelay, SWDTYPE_INTEGER, EEPROM_CONFIG_SENSOR_DELAY);

static byte dtSensor[6];
REGISTER regSensor(dtSensor, sizeof(dtSensor), &updtSensor, NULL);

REGISTER regPulseWidth(dtPulseWidth, sizeof(dtPulseWidth), NULL, &updtPulseWidth, SWDTYPE_INTEGER, EEPROM_CONFIG_PULSE_WIDTH);

static byte dtoutput0[1];
REGISTER regoutput0(dtoutput0, sizeof(dtoutput0), NULL, &updtOutput);
static byte dtoutput1[1];
REGISTER regoutput1(dtoutput1, sizeof(dtoutput1), NULL, &updtOutput);
static byte dtoutput2[1];
REGISTER regoutput2(dtoutput2, sizeof(dtoutput2), NULL, &updtOutput);
static byte dtoutput3[1];
REGISTER regoutput3(dtoutput3, sizeof(dtoutput3), NULL, &updtOutput);
static byte dtoutput4[1];
REGISTER regoutput4(dtoutput4, sizeof(dtoutput4), NULL, &updtOutput);
static byte dtoutput5[1];
REGISTER regoutput5(dtoutput5, sizeof(dtoutput5), NULL, &updtOutput);
static byte dtoutput6[1];
REGISTER regoutput6(dtoutput6, sizeof(dtoutput6), NULL, &updtOutput);
static byte dtoutput7[1];
REGISTER regoutput7(dtoutput7, sizeof(dtoutput7), NULL, &updtOutput);
static byte dtoutput8[1];
REGISTER regoutput8(dtoutput8, sizeof(dtoutput8), NULL, &updtOutput);
static byte dtoutput9[1];
REGISTER regoutput9(dtoutput9, sizeof(dtoutput9), NULL, &updtOutput);
static byte dtoutput10[1];
REGISTER regoutput10(dtoutput10, sizeof(dtoutput10), NULL, &updtOutput);
static byte dtoutput11[1];
REGISTER regoutput11(dtoutput11, sizeof(dtoutput11), NULL, &updtOutput);
static byte dtoutput12[1];
REGISTER regoutput12(dtoutput12, sizeof(dtoutput12), NULL, &updtOutput);
static byte dtoutput13[1];
REGISTER regoutput13(dtoutput13, sizeof(dtoutput13), NULL, &updtOutput);
static byte dtoutput14[1];
REGISTER regoutput14(dtoutput14, sizeof(dtoutput14), NULL, &updtOutput);
static byte dtoutput15[1];
REGISTER regoutput15(dtoutput15, sizeof(dtoutput15), NULL, &updtOutput);
static byte dtoutput16[1];
REGISTER regoutput16(dtoutput16, sizeof(dtoutput16), NULL, &updtOutput);
static byte dtoutput17[1];
REGISTER regoutput17(dtoutput17, sizeof(dtoutput17), NULL, &updtOutput);
static byte dtoutput18[1];
REGISTER regoutput18(dtoutput18, sizeof(dtoutput18), NULL, &updtOutput);
static byte dtoutput19[1];
REGISTER regoutput19(dtoutput19, sizeof(dtoutput19), NULL, &updtOutput);
static byte dtoutput20[1];
REGISTER regoutput20(dtoutput20, sizeof(dtoutput20), NULL, &updtOutput);
static byte dtoutput21[1];
REGISTER regoutput21(dtoutput21, sizeof(dtoutput21), NULL, &updtOutput);
static byte dtoutput22[1];
REGISTER regoutput22(dtoutput22, sizeof(dtoutput22), NULL, &updtOutput);
static byte dtoutput23[1];
REGISTER regoutput23(dtoutput23, sizeof(dtoutput23), NULL, &updtOutput);


/**
 * Initialize table of registers
 */
DECLARE_REGISTERS_START()
  // Pointers to the custom registers
  &regSensorDelay,
  &regSensor,
  &regPulseWidth,
  &regoutput0,
  &regoutput1,
  &regoutput2,
  &regoutput3,
  &regoutput4,
  &regoutput5,
  &regoutput6,
  &regoutput7,
  &regoutput8,
  &regoutput9,
  &regoutput10,
  &regoutput11,
  &regoutput12,
  &regoutput13,
  &regoutput14,
  &regoutput15,
  &regoutput16,
  &regoutput17,
  &regoutput18,
  &regoutput19,
  &regoutput20,
  &regoutput21,
  &regoutput22,
  &regoutput23
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
  float pressure_Pa;
  int temperature2send;
  long pressure2send;
  
  bmp.getTemperature(&temperature);
#ifdef DEBUG
  Serial.print("Temperature: ");
  Serial.print(temperature);
  Serial.println(" °C");
#endif
    
  bmp.getPressure(&pressure_Pa);
#ifdef DEBUG
  Serial.print("Pressure: ");
  Serial.print(pressure_Pa / 100.0F);
  Serial.println(" hPa");
#endif
  
  temperature2send = (int) (temperature * 100);
  dtSensor[0] = (temperature2send >> 8) & 0xFF;
  dtSensor[1] = temperature2send & 0xFF;
  
  pressure2send = (long) (pressure_Pa);
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

/**
 * updtOutput
 *
 * Set output. Only ON / OFF level allowed. Both value requires a toggle via a pulse.
 *
 * rId: register ID
 * value: New register value
 */
const void updtOutput(byte rId, byte *value)
{
#ifdef DEBUG
  Serial.print("rId: ");
  Serial.println(rId);
  Serial.print("value: ");
  Serial.println(value[0]);
#endif

  // Update register from:
  // level (Position = byte 0 bit 0 - Size = 8 bits)
  // Output is rId - REGI_OUTPUT0
  
  // Set new Sensor Delay. BE to LE conversion
  getRegister(rId)->setValueFromBeBuffer(value);
  
  currentOutput = rId - REGI_OUTPUT0;
  currentOutputBoard = currentOutput / OUTPUT_PER_BOARD;
  currentOutputInBoard = currentOutput % OUTPUT_PER_BOARD;
  
#ifdef DEBUG
  Serial.println(currentOutput);
  Serial.println(currentOutputBoard);
  Serial.println(currentOutputInBoard);
#endif

  startPulse();
}



