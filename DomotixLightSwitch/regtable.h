/**
 * regtable.h
 *
 * List of registers. Header file.
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 12 Nov 2013
 */
#ifndef _REGTABLE_H
#define _REGTABLE_H

#include "Arduino.h"
#include "register.h"
#include "commonregs.h"

#define EEPROM_CONFIG_SENSOR_DELAY   EEPROM_FIRST_CUSTOM
#define EEPROM_TOUCH_CONFIG          EEPROM_CONFIG_SENSOR_DELAY + 2

/**
 * Register indexes
 */
DEFINE_REGINDEX_START()
  // First index here = 11
  REGI_VOLTSUPPLY,
  REGI_SENSOR_DELAY,
  REGI_SENSOR,
  REGI_TOUCH_CONFIG
DEFINE_REGINDEX_END()

#endif
